package top.flowerstardream.hcd.trainSeat.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import top.flowerstardream.hcd.base.ao.res.StatusRES;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.dto.*;
import top.flowerstardream.hcd.trainSeat.ao.req.SeatReservationREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.SeatReservationRES;
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.ScheduleMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.TrainMapper;
import top.flowerstardream.hcd.trainSeat.bo.RouteEO;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;
import top.flowerstardream.hcd.trainSeat.bo.SeatReservationEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SeatReservationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.biz.client.TicketClient;
import top.flowerstardream.hcd.trainSeat.biz.mapper.SeatReservationMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.ISeatReservationService;
import top.flowerstardream.hcd.trainSeat.biz.tool.Calculation;
import top.flowerstardream.hcd.trainSeat.bo.TrainEO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.base.constant.CommonConstant.PAGE_TOTAL;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.trainSeat.constant.BookingStatus.*;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.*;

@Slf4j
@Service
public class ISeatReservationServiceImpl extends ServiceImpl<SeatReservationMapper, SeatReservationEO> implements ISeatReservationService {

    @Resource
    private SeatReservationMapper seatReservationMapper;

    @Resource
    private ScheduleMapper scheduleMapper;

    @Resource
    private TrainMapper trainMapper;

    @Resource
    private RouteMapper routeMapper;

    @Resource
    private Calculation calculation;

    @Lazy
    @Resource
    private ISeatReservationServiceImpl self;

    @Resource
    private TicketClient ticketClient;


    @Override
    public void addSeatReservation(SeatReservationREQ seatReservationREQ) {
        //参数校验
        if (seatReservationREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
            return;
        }

        //判断座位预订存在，存在则中断
        validateSeatReservationIsExist(seatReservationREQ.getScheduleId(), seatReservationREQ.getSeatNum(), seatReservationREQ.getBookingStatus());

        SeatReservationEO seatReservationEO = new SeatReservationEO();
        BeanUtil.copyProperties(seatReservationREQ, seatReservationEO);
        boolean insert = self.save(seatReservationEO);
        if (!insert) {
            INSERTION_FAILED.throwException();
        }
    }

    @Override
    public void deleteSeatReservation(List<Long> ids) {
        if(CollUtil.isEmpty(ids)){
            return;
        }

        ids.forEach(id -> {
            //获取路线信息
            SeatReservationEO seatReservationEO = self.getById(id);
            if (seatReservationEO == null) {
                return;
            }
            Result<List<TicketSeatReservationDTO>> tickets = ticketClient.getTickets(id);

            if (CollUtil.isNotEmpty(tickets.getData())) {
                SEAT_RESERVATION_IS_USED.throwException();
            }
        });


        //批量删除座位预订
        boolean delete = self.removeByIds(ids);
        if (!delete) {
            DELETION_FAILED.throwException();
        }

    }

    @Override
    public void updateSeatReservation(SeatReservationREQ seatReservationREQ) {
        //参数校验
        if (seatReservationREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        SeatReservationEO seatReservationEO = new SeatReservationEO();
        BeanUtil.copyProperties(seatReservationREQ, seatReservationEO);
        boolean update = self.updateById(seatReservationEO);
        if (!update) {
            MODIFICATION_FAILED.throwException();
        }

    }

    @Override
    public PageResult<SeatReservationRES> EmployeePageQuery(SeatReservationPageQueryREQ seatReservationPageQueryREQ) {
        //参数校验
        if (seatReservationPageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //设置分页参数默认值
        if (seatReservationPageQueryREQ.getPage() <= 0) {
            seatReservationPageQueryREQ.setPage(1);
        }
        if (seatReservationPageQueryREQ.getPageSize() <= 0) {
            seatReservationPageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<SeatReservationEO> page = new Page<>(seatReservationPageQueryREQ.getPage(), seatReservationPageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<SeatReservationEO> queryWrapper = Wrappers.lambdaQuery();

        //查询条件
        if (seatReservationPageQueryREQ.getId() != null) {
            queryWrapper.eq(SeatReservationEO::getId, seatReservationPageQueryREQ.getId());
        }
        if (seatReservationPageQueryREQ.getScheduleId() != null) {
            queryWrapper.eq(SeatReservationEO::getScheduleId, seatReservationPageQueryREQ.getScheduleId());
        }
        if (seatReservationPageQueryREQ.getSeatNum() != null) {
            queryWrapper.eq(SeatReservationEO::getSeatNum, seatReservationPageQueryREQ.getSeatNum());
        }
        if (seatReservationPageQueryREQ.getBookingStatus() != null) {
            queryWrapper.eq(SeatReservationEO::getBookingStatus, seatReservationPageQueryREQ.getBookingStatus());
        }

        //执行分页查询
        Page<SeatReservationEO> seatReservationResult = seatReservationMapper.selectPage(page, queryWrapper);

        List<SeatReservationEO> records = seatReservationResult.getRecords();
        List<SeatReservationRES> resList = new ArrayList<>();
        
        if (CollUtil.isNotEmpty(records)) {
            // 提取班次ID列表
            List<Long> scheduleIds = records.stream()
                    .map(SeatReservationEO::getScheduleId)
                    .distinct()
                    .toList();
            
            // 批量查询班次信息
            List<ScheduleEO> schedules = scheduleMapper.selectBatchIds(scheduleIds);
            Map<Long, ScheduleEO> scheduleMap = schedules.stream()
                    .collect(Collectors.toMap(ScheduleEO::getId, s -> s));
            
            // 提取列车和路线ID列表
            List<Long> trainIds = schedules.stream()
                    .map(ScheduleEO::getTrainId)
                    .distinct()
                    .toList();
            List<Long> routeIds = schedules.stream()
                    .map(ScheduleEO::getRouteId)
                    .distinct()
                    .toList();
            
            // 批量查询列车和路线信息
            List<TrainEO> trains = CollUtil.isEmpty(trainIds) ? Collections.emptyList() : trainMapper.selectBatchIds(trainIds);
            List<RouteEO> routes = CollUtil.isEmpty(routeIds) ? Collections.emptyList() : routeMapper.selectBatchIds(routeIds);
            
            // 构建映射关系
            Map<Long, String> trainNameMap = trains.stream()
                    .collect(Collectors.toMap(TrainEO::getId, TrainEO::getTrainName));
            Map<Long, String> routeNameMap = routes.stream()
                    .collect(Collectors.toMap(RouteEO::getId, RouteEO::getRouteName));
            
            // 转换为RES对象
            resList = records.stream()
                    .map(eo -> {
                        SeatReservationRES res = new SeatReservationRES();
                        BeanUtil.copyProperties(eo, res);
                        
                        ScheduleEO schedule = scheduleMap.get(eo.getScheduleId());
                        if (schedule != null) {
                            res.setTrainName(trainNameMap.get(schedule.getTrainId()));
                            res.setRouteName(routeNameMap.get(schedule.getRouteId()));
                        }
                        
                        return res;
                    })
                    .toList();
        }

        //封装返回结果
        PageResult<SeatReservationRES> pageResult = new PageResult<>();
        Long total = seatReservationMapper.selectCount(Wrappers.lambdaQuery(SeatReservationEO.class));
        pageResult.setTotal(total > PAGE_TOTAL ? PAGE_TOTAL : total);
        pageResult.setRecords(resList);
        return pageResult;
    }

    private SeatReservationEO getSeatReservationEO(Long scheduleId, Integer seatNum, Integer bookingStatus){
        LambdaQueryWrapper<SeatReservationEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SeatReservationEO::getScheduleId, scheduleId)
                .eq(SeatReservationEO::getSeatNum, seatNum)
                .eq(SeatReservationEO::getBookingStatus, bookingStatus) ;
        return seatReservationMapper.selectOne(queryWrapper);
    }
    private void validateSeatReservationIsExist(Long scheduleId, Integer seatNum, Integer bookingStatus){
        SeatReservationEO seatReservationEO = getSeatReservationEO(scheduleId, seatNum, bookingStatus);
        if (seatReservationEO != null) {
            SEAT_RESERVATION_ALREADY_EXISTS.throwException();
        }
    }


    /**
     * 外部调用
     * */
    public List<SeatReservationDTO> getSeatReservationByIds(List<Long> seatReservationIds){
        //参数校验
        if (seatReservationIds == null || seatReservationIds.isEmpty()) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        LambdaQueryWrapper<SeatReservationEO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SeatReservationEO::getId, seatReservationIds);
        List<SeatReservationEO> seatReservationEOs = seatReservationMapper.selectList(queryWrapper);

        return seatReservationEOs.stream().map(seatReservationEO -> SeatReservationDTO.builder()
                .id(seatReservationEO.getId())
                .seatNum(seatReservationEO.getSeatNum())
                .build()).toList();
    }
    /**
     * 外部调用
     * */
    public void releaseSeat(@RequestParam("seatReservationIds") List<Long> seatReservationIds){

        //参数校验
        if (seatReservationIds == null || seatReservationIds.isEmpty()) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
            return;
        }
        List<SeatReservationEO> seatReservationEOs = seatReservationMapper.selectBatchIds(seatReservationIds);
        // 按照 scheduleId 分组统计每个班次需要增加的余票数
        Map<Long, Long> scheduleTicketCountMap = seatReservationEOs.stream()
                .collect(Collectors.groupingBy(SeatReservationEO::getScheduleId, Collectors.counting()));
        // 对每个 scheduleId 更新对应的余票数
        scheduleTicketCountMap.forEach((scheduleId, ticketCount) -> {
            LambdaUpdateWrapper<ScheduleEO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.setSql("available_tickets", "available_tickets + " + ticketCount)
                         .eq(ScheduleEO::getId, scheduleId);
            scheduleMapper.update(updateWrapper);
        });
            //修改座位预订状态
        for (SeatReservationEO seatReservationEO : seatReservationEOs) {
            seatReservationEO.setBookingStatus(NOT_BOOKED.getValue());
        }
        self.updateBatchById(seatReservationEOs);
    }
    /**
     * 外部调用
     * */
    public ReserveSeatResultDTO reserveSeat(ReserveSeatDTO reserveSeatDTO){
        //参数校验
        if (reserveSeatDTO == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
            return null;
        }

        //根据用户的所选班次、起点站和终点站ID，计算上车时间和下车时间
        TimeDTO timeDTO = calculation.timeCalculation(reserveSeatDTO);
        LocalDateTime startStationTime = timeDTO.getStartStationTime();
        LocalDateTime endStationTime = timeDTO.getEndStationTime();
        /**
         * seatNumList
         */
        // 查询并扣除余票
        ScheduleEO scheduleEO = scheduleMapper.selectById(reserveSeatDTO.getScheduleId());
        Integer availingTickets = scheduleEO.getAvailableTickets();
        if (availingTickets < reserveSeatDTO.getTicketCount()) {
            NOT_ENOUGH_TICKETS.throwException();
        }
        LambdaUpdateWrapper<ScheduleEO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("available_tickets", "available_tickets - " + reserveSeatDTO.getTicketCount())
                    .eq(ScheduleEO::getId, reserveSeatDTO.getScheduleId())
                    .gt(ScheduleEO::getAvailableTickets, 0);
        int update = scheduleMapper.update(updateWrapper);
        if (update <= 0) {
            NOT_ENOUGH_TICKETS.throwException();
        }
        //根据班次号查询座位信息
        LambdaQueryWrapper<SeatReservationEO> queryWrapper0 = new LambdaQueryWrapper<>();
        queryWrapper0.eq(SeatReservationEO::getScheduleId, reserveSeatDTO.getScheduleId())
                .eq(SeatReservationEO::getBookingStatus, NOT_BOOKED);
        List<SeatReservationEO> seatReservationEOs = seatReservationMapper.selectList(queryWrapper0);
        if (CollUtil.isEmpty(seatReservationEOs)) {
            SEAT_RESERVATION_IS_USED.throwException();
        }
        if (seatReservationEOs.size() < reserveSeatDTO.getTicketCount()) {
            NOT_ENOUGH_SEATS.throwException();
        }
        // 随机选择
        List<SeatReservationEO> selectedSeats = seatReservationEOs.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.shuffle(list);
                    return list.stream().limit(reserveSeatDTO.getTicketCount()).toList();
                }));
        //修改座位预订状态
        for (SeatReservationEO seatReservationEO : selectedSeats) {
            seatReservationEO.setBookingStatus(BOOKED.getValue());
            seatReservationMapper.updateById(seatReservationEO);
        }
        //过滤符合条件的EO，收录座位id
        List<Long> seatIdList = selectedSeats.stream()
                .map(SeatReservationEO::getId)
                .toList();

        return  ReserveSeatResultDTO.builder()
                .seatReservationIds(seatIdList)
                .startTime(startStationTime)
                .endTime(endStationTime)
                .build();
    }

    /**
     * 获取状态列表
     * @return
     */
    @Override
    public List<StatusRES> getStatus() {
        // 使用LambdaQueryWrapper进行分组统计
        List<Map<String, Object>> statusCounts = seatReservationMapper.count();

        // 将统计结果转换为StatusRES列表
        return statusCounts.stream()
            .map(map -> {
                StatusRES statusRES = new StatusRES();
                statusRES.setStatus((Integer) map.get("booking_status"));
                statusRES.setCount((Integer) map.get("count"));
                statusRES.setDescription(getStatusDescription(statusRES.getStatus()));
                return statusRES;
            })
            .collect(Collectors.toList());
    }

    /**
     * 根据状态码获取状态描述
     * @param statusCode 状态码
     * @return 状态描述
     */
    private String getStatusDescription(Integer statusCode) {
        return switch (statusCode) {
            case 0 -> "未预订";
            case 1 -> "已预订";
            default -> "未知状态";
        };
    }

}
