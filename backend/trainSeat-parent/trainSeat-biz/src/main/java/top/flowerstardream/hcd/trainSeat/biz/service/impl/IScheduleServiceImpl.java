package top.flowerstardream.hcd.trainSeat.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.flowerstardream.hcd.trainSeat.ao.dto.SeatReservationDTO;
import top.flowerstardream.hcd.trainSeat.ao.req.ScheduleREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.ScheduleRES;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;
import top.flowerstardream.hcd.trainSeat.bo.SeatReservationEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.biz.mapper.ScheduleMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.SeatReservationMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.IScheduleService;
import java.util.List;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.SCHEDULE_ALREADY_EXISTS;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.SCHEDULE_IS_USED;

@Slf4j
@Service
public class IScheduleServiceImpl extends ServiceImpl<ScheduleMapper, ScheduleEO> implements IScheduleService {

    @Resource
    private ScheduleMapper scheduleMapper;

    @Lazy
    @Resource
    private IScheduleServiceImpl self;

    @Resource
    private SeatReservationMapper seatReservationMapper;

    @Override
    public void addSchedule(ScheduleREQ scheduleREQ) {
        //参数校验
        if (scheduleREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
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
    public PageResult<ScheduleEO> EmployeePageQuery(SchedulePageQueryREQ schedulePageQueryREQ) {
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
        queryWrapper.eq(ScheduleEO::getTrainId, schedulePageQueryREQ.getTrainId())
                .eq(ScheduleEO::getRouteId, schedulePageQueryREQ.getRouteId())
                .like(ScheduleEO::getConductor, schedulePageQueryREQ.getConductor())
                //剩余座位数 TODO
                .like(ScheduleEO::getAvailingTickets, schedulePageQueryREQ.getAvailingTickets())
                .eq(ScheduleEO::getStartTime, schedulePageQueryREQ.getStartTime())
                .eq(ScheduleEO::getEndTime, schedulePageQueryREQ.getEndTime());

        //执行分页查询
        Page<ScheduleEO> schedulePage = scheduleMapper.selectPage(page, queryWrapper);

        //封装返回结果
        PageResult<ScheduleEO> pageResult = new PageResult<>();
        pageResult.setTotal(schedulePage.getTotal());
        pageResult.setRecords(schedulePage.getRecords());
        return pageResult;
    }

    @Override
    public PageResult<ScheduleRES> UserPageQuery(SchedulePageQueryREQ schedulePageQueryREQ) {
        //参数校验
        if (schedulePageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        // 设置分页参数默认值
        if (schedulePageQueryREQ.getPage() <= 0) {
            schedulePageQueryREQ.setPage(1);
        }
        if (schedulePageQueryREQ.getPageSize() <= 0) {
            schedulePageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<ScheduleEO> page = new Page<>(schedulePageQueryREQ.getPage(), schedulePageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<ScheduleEO> queryWrapper = Wrappers.lambdaQuery();

        //查询条件
        queryWrapper.eq(ScheduleEO::getTrainId, schedulePageQueryREQ.getTrainId())
                .eq(ScheduleEO::getRouteId, schedulePageQueryREQ.getRouteId())
                .like(ScheduleEO::getConductor, schedulePageQueryREQ.getConductor())
                //剩余座位数 TODO
                .like(ScheduleEO::getAvailingTickets, schedulePageQueryREQ.getAvailingTickets())
                .eq(ScheduleEO::getStartTime, schedulePageQueryREQ.getStartTime())
                .eq(ScheduleEO::getEndTime, schedulePageQueryREQ.getEndTime());


        //执行分页查询
        Page<ScheduleEO> schedulePage = scheduleMapper.selectPage(page, queryWrapper);

        //将EO转换为RES
        List<ScheduleRES> resList = schedulePage.getRecords().stream()
                .map(eo -> {
                    ScheduleRES res = new ScheduleRES();
                    BeanUtil.copyProperties(eo, res);
                    return res;
                })
                .collect(Collectors.toList());

        //封装返回结果
        PageResult<ScheduleRES> pageResult = new PageResult<>();
        pageResult.setTotal(schedulePage.getTotal());
        pageResult.setRecords(resList);
        return pageResult ;
    }

    private ScheduleEO getSchedule(Long trainId, Long routeId, String startTime){
        LambdaQueryWrapper<ScheduleEO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScheduleEO::getTrainId, trainId)
                .eq(ScheduleEO::getRouteId, routeId)
                .eq(ScheduleEO::getStartTime, startTime);
        return scheduleMapper.selectOne(queryWrapper);
    }

    private void validateScheduleIsExist(Long trainId, Long routeId, String startTime) {

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


}
