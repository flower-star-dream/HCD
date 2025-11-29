package top.flowerstardream.hcd.trainSeat.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.flowerstardream.hcd.trainSeat.ao.dto.CalcTicketPriceDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.ReserveSeatDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.SeatReservationDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.TimeDTO;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RealTimeSchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.RealTimeScheduleREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.ScheduleREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RealTimeScheduleRES;
import top.flowerstardream.hcd.trainSeat.ao.res.ScheduleRES;
import top.flowerstardream.hcd.trainSeat.biz.mapper.*;
import top.flowerstardream.hcd.trainSeat.biz.tool.Calculation;
import top.flowerstardream.hcd.trainSeat.bo.*;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.biz.service.IScheduleService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.SCHEDULE_ALREADY_EXISTS;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.SCHEDULE_IS_USED;

@Slf4j
@Service
public class IScheduleServiceImpl extends ServiceImpl<ScheduleMapper, ScheduleEO> implements IScheduleService {

    @Lazy
    @Resource
    private IScheduleServiceImpl self;

    @Resource
    private ScheduleMapper scheduleMapper;

    @Resource
    private SeatReservationMapper seatReservationMapper;

    @Resource
    private RouteStationsMapper routeStationsMapper;

    @Resource
    private TrainMapper trainMapper;

    @Resource
    private Calculation calculation;

    @Resource
    private StationMapper stationMapper;

    @Resource
    private RouteMapper routeMapper;

    @Override
    public void addSchedule(ScheduleREQ scheduleREQ) {
        //参数校验
        if (scheduleREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
            return;
        }

        //判断班次存在，存在则中断
        validateScheduleIsExist(scheduleREQ.getTrainId(), scheduleREQ.getRouteId(), scheduleREQ.getStartTime());

        //打包req的属性进入EO，然后插入数据库
        ScheduleEO scheduleEO = new ScheduleEO();
        BeanUtil.copyProperties(scheduleREQ, scheduleEO);
        boolean insert = self.save(scheduleEO);
        if (!insert) {
            INSERTION_FAILED.throwException();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(List<Long> ids) {
        if(CollUtil.isEmpty(ids)){
            return;
        }
        //排查有无使用路线
        ids.forEach(id -> {
            //获取班次信息
            ScheduleEO scheduleEO = self.getById(id);
            if (scheduleEO != null) {
                return;
            }

            LambdaQueryWrapper<SeatReservationEO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SeatReservationEO::getScheduleId,id);
            List<SeatReservationEO> seatReservations = seatReservationMapper.selectList(queryWrapper);

            if (seatReservations != null){
                SCHEDULE_IS_USED.throwException();
            }

        });
        //批量删除员工
        boolean delete = self.removeByIds(ids);
        if (!delete) {
            DELETION_FAILED.throwException();
        }

    }

    @Override
    public void updateSchedule(ScheduleREQ scheduleREQ) {
        //参数校验
        if (scheduleREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        ScheduleEO scheduleEO = new ScheduleEO();
        BeanUtil.copyProperties(scheduleREQ, scheduleEO);
        boolean update = self.updateById(scheduleEO);
        if (!update) {
            MODIFICATION_FAILED.throwException();
        }

    }

    @Override
    public PageResult<ScheduleRES> EmployeePageQuery(SchedulePageQueryREQ schedulePageQueryREQ) {
        //参数校验
        if (schedulePageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //设置分页参数默认值
        if (schedulePageQueryREQ.getPage() <= 0) {
            schedulePageQueryREQ.setPage(1);
        }
        if (schedulePageQueryREQ.getPageSize() <= 0) {
            schedulePageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<ScheduleEO> page = new Page<>(schedulePageQueryREQ.getPage(), schedulePageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<ScheduleEO> queryWrapper = new LambdaQueryWrapper<>();

        //查询条件
        if (schedulePageQueryREQ.getId() != null) {
            queryWrapper.eq(ScheduleEO::getId, schedulePageQueryREQ.getId());
        }
        if (StrUtil.isNotBlank(schedulePageQueryREQ.getTrainName())) {
            LambdaQueryWrapper<TrainEO> trainQueryWrapper = new LambdaQueryWrapper<>();
            trainQueryWrapper.eq(TrainEO::getTrainName, schedulePageQueryREQ.getTrainName());
            List<Long> trainIds = trainMapper.selectList(trainQueryWrapper).stream().map(TrainEO::getId).toList();
            queryWrapper.in(ScheduleEO::getTrainId, trainIds);
        }
        if (StrUtil.isNotBlank(schedulePageQueryREQ.getRouteName())) {
            LambdaQueryWrapper<RouteEO> routeQueryWrapper = new LambdaQueryWrapper<>();
            routeQueryWrapper.eq(RouteEO::getRouteName, schedulePageQueryREQ.getRouteName());
            List<Long> routeIds = routeMapper.selectList(routeQueryWrapper).stream().map(RouteEO::getId).toList();
            queryWrapper.in(ScheduleEO::getRouteId, routeIds);
        }
        if (StrUtil.isNotBlank(schedulePageQueryREQ.getConductor())) {
            queryWrapper.like(ScheduleEO::getConductor, schedulePageQueryREQ.getConductor());
        }
        if (schedulePageQueryREQ.getStartTime() != null) {
            LocalDateTime startTime = schedulePageQueryREQ.getStartTime().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime startTimeX = schedulePageQueryREQ.getStartTime().withHour(23).withMinute(59).withSecond(59);
            queryWrapper.between(ScheduleEO::getStartTime, startTime, startTimeX);
        }
        if (schedulePageQueryREQ.getEndTime() != null) {
            LocalDateTime endTime = schedulePageQueryREQ.getEndTime().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endTimeX = schedulePageQueryREQ.getEndTime().withHour(23).withMinute(59).withSecond(59);
            queryWrapper.between(ScheduleEO::getEndTime, endTime, endTimeX);
        }

        //执行分页查询
        Page<ScheduleEO> schedulePage = scheduleMapper.selectPage(page, queryWrapper);
        List<ScheduleEO> records = schedulePage.getRecords();

        List<Long> trainIds = records.stream().map(ScheduleEO::getTrainId).distinct().toList();
        List<Long> routeIds = records.stream().map(ScheduleEO::getRouteId).distinct().toList();
        Map<Long, String> trainNameMap = trainMapper.selectBatchIds(trainIds).stream()
                .collect(Collectors.toMap(TrainEO::getId, TrainEO::getTrainName));
        Map<Long, String> routeNameMap = routeMapper.selectBatchIds(routeIds).stream()
                .collect(Collectors.toMap(RouteEO::getId, RouteEO::getRouteName));
        //将EO转换为RES
        List<ScheduleRES> resList = schedulePage.getRecords().stream()
                .map(eo -> {
                    ScheduleRES res = new ScheduleRES();
                    BeanUtil.copyProperties(eo, res);
                    res.setTrainName(trainNameMap.get(eo.getTrainId()));
                    res.setRouteName(routeNameMap.get(eo.getRouteId()));
                    return res;
                })
                .toList();

        //封装返回结果
        PageResult<ScheduleRES> pageResult = new PageResult<>();
        pageResult.setTotal(schedulePage.getTotal());
        pageResult.setRecords(resList);
        return pageResult;
    }

    @Override
    public RealTimeScheduleRES getSchedule(RealTimeScheduleREQ realTimeScheduleREQ) {
        if (realTimeScheduleREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        ScheduleEO scheduleEO = self.getById(realTimeScheduleREQ.getScheduleId());

        /**
         * 站点ID查站名
         */
        String startStation = stationMapper.selectById(realTimeScheduleREQ.getStartStationId()).getStationName();
        String endStation = stationMapper.selectById(realTimeScheduleREQ.getEndStationId()).getStationName();

        /**
         * 获取出发时间和到达时间
         */
        ReserveSeatDTO reserveSeatDTO = ReserveSeatDTO.builder()
                .scheduleId(realTimeScheduleREQ.getScheduleId())
                .startStationId(realTimeScheduleREQ.getStartStationId())
                .endStationId(realTimeScheduleREQ.getEndStationId())
                .build();
        TimeDTO timeDTO = calculation.timeCalculation(reserveSeatDTO);

        /**
         * 计算票价
         */
        CalcTicketPriceDTO calcTicketPriceDTO = CalcTicketPriceDTO.builder()
                .scheduleId(realTimeScheduleREQ.getScheduleId())
                .startStationId(realTimeScheduleREQ.getStartStationId())
                .endStationId(realTimeScheduleREQ.getEndStationId())
                .build();
        BigDecimal price = calculation.ticketPriceCalculation(calcTicketPriceDTO);


        return RealTimeScheduleRES.builder()
                .scheduleId(scheduleEO.getId())
                .startTime(timeDTO.getStartStationTime())
                .endTime(timeDTO.getEndStationTime())
                .startStation(startStation)
                .endStation(endStation)
                .price(price)
                .remainTicket(scheduleEO.getAvailingTickets())
                .build();
    }

    private ScheduleEO getSchedule(Long trainId, Long routeId, LocalDateTime startTime){
        LambdaQueryWrapper<ScheduleEO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScheduleEO::getTrainId, trainId)
                .eq(ScheduleEO::getRouteId, routeId)
                .eq(ScheduleEO::getStartTime, startTime);
        return scheduleMapper.selectOne(queryWrapper);
    }

    private void validateScheduleIsExist(Long trainId, Long routeId, LocalDateTime startTime) {

        ScheduleEO scheduleEO = getSchedule(trainId,routeId,startTime);
        if (scheduleEO != null) {
            SCHEDULE_ALREADY_EXISTS.throwException();
        }
    }



    /**外部调用*/
    public Integer getAvailingTickets(Long scheduleId){
        ScheduleEO scheduleEO = scheduleMapper.selectById(scheduleId);
        return scheduleEO.getAvailingTickets();
    }

    public PageResult<RealTimeScheduleRES> getRealTimeSchedule(RealTimeSchedulePageQueryREQ realTimeSchedulePageQueryREQ){
        //参数校验
        if (realTimeSchedulePageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //设置分页参数默认值
        if (realTimeSchedulePageQueryREQ.getPage() <= 0) {
            realTimeSchedulePageQueryREQ.setPage(1);
        }
        if (realTimeSchedulePageQueryREQ.getPageSize() <= 0) {
            realTimeSchedulePageQueryREQ.setPageSize(10);
        }

        /**
         * 站点ID查站名
         * */
        String startStation = stationMapper.selectById(realTimeSchedulePageQueryREQ.getStartStationId()).getStationName();
        String endStation = stationMapper.selectById(realTimeSchedulePageQueryREQ.getEndStationId()).getStationName();


        /**查找班次*/
        LambdaQueryWrapper<RouteStationsEO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RouteStationsEO::getStationId, realTimeSchedulePageQueryREQ.getStartStationId());
        List<RouteStationsEO> routeStationsStart = routeStationsMapper.selectList(queryWrapper);

        LambdaQueryWrapper<RouteStationsEO> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(RouteStationsEO::getStationId, realTimeSchedulePageQueryREQ.getEndStationId());
        List<RouteStationsEO> routeStationsEnd = routeStationsMapper.selectList(queryWrapper1);

        //找出同时包含出发站和终点站的路线ID
        List<Long> startRouteIds = routeStationsStart.stream()
                .map(RouteStationsEO::getRouteId)
                .toList();

        List<Long> commonRouteIds = routeStationsEnd.stream()
                .map(RouteStationsEO::getRouteId)
                .filter(startRouteIds::contains)
                .toList();

        //处理出发时间
        LocalDateTime startTime = realTimeSchedulePageQueryREQ.getNowTime().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endTime = realTimeSchedulePageQueryREQ.getNowTime().withHour(23).withMinute(59).withSecond(59);

        //根据路线和出发时间查询班次
        List<ScheduleEO> schedules = scheduleMapper.selectList(new LambdaQueryWrapper<ScheduleEO>()
                .between(ScheduleEO::getStartTime, startTime, endTime)
                .in(ScheduleEO::getRouteId, commonRouteIds));

        //每个班次都封装出一个实时班次响应
        List<RealTimeScheduleRES> resList = schedules.stream()
                .map(schedule -> {
                    RealTimeScheduleRES res = new RealTimeScheduleRES();
                    //计算时间
                    ReserveSeatDTO ReserveSeatDTO = new ReserveSeatDTO(
                            schedule.getId(),
                            realTimeSchedulePageQueryREQ.getStartStationId(),
                            realTimeSchedulePageQueryREQ.getEndStationId());
                    TimeDTO timeDTO = calculation.timeCalculation(ReserveSeatDTO);
                    //计算价格
                    BigDecimal price = calculation.ticketPriceCalculation(new CalcTicketPriceDTO(
                            schedule.getId(),
                            realTimeSchedulePageQueryREQ.getStartStationId(),
                            realTimeSchedulePageQueryREQ.getEndStationId()));
                    //封装结果
                    res.setScheduleId(schedule.getId());
                    res.setPrice(price);
                    res.setStartTime(timeDTO.getStartStationTime());
                    res.setEndTime(timeDTO.getEndStationTime());
                    res.setStartStation(startStation);
                    res.setEndStation(endStation);
                    res.setRemainTicket(schedule.getAvailingTickets());
                    return res;
                }).toList();

        // 将最终的结果转换为分页对象
        Page<RealTimeScheduleRES> page = new Page<>(realTimeSchedulePageQueryREQ.getPage(), realTimeSchedulePageQueryREQ.getPageSize(), resList.size());
        // 手动设置当前页的记录
        int fromIndex = (int) ((page.getCurrent() - 1) * page.getSize());
        int toIndex = (int) Math.min(fromIndex + page.getSize(), resList.size());
        page.setRecords(resList.subList(fromIndex, toIndex));

        PageResult<RealTimeScheduleRES> pageResult = new PageResult<>();
        pageResult.setTotal(resList.size());
        pageResult.setRecords(page.getRecords());
        return pageResult;
    }


}
