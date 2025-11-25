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
import top.flowerstardream.hcd.bo.eo.SeatReservationEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.DTO.OrderDTO;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.SeatReservationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.SeatReservationREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.SeatReservationRES;
import top.flowerstardream.hcd.trainSeat.biz.client.OrderClient;
import top.flowerstardream.hcd.trainSeat.biz.mapper.SeatReservationMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.ISeatReservationService;
import java.util.List;
import java.util.stream.Collectors;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.SEAT_RESERVATION_ALREADY_EXISTS;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.SEAT_RESERVATION_IS_USED;

@Slf4j
@Service
public class ISeatReservationServiceImpl extends ServiceImpl<SeatReservationMapper, SeatReservationEO> implements ISeatReservationService {

    @Resource
    private SeatReservationMapper seatReservationMapper;

    @Lazy
    @Resource
    private ISeatReservationServiceImpl self;

    @Resource
    private OrderClient orderClient;


    @Override
    public void addSeatReservation(SeatReservationREQ seatReservationREQ) {
        //参数校验
        if (seatReservationREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        //判断座位预订存在，存在则中断
        validateSeatReservationIsExist(seatReservationREQ.getScheduleId(), seatReservationREQ.getSeatId(), seatReservationREQ.getBookingStatus());

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

            // TODO 是否有使用座位预订
            List<OrderDTO> orders = orderClient.getOrders(id);

            if (orders != null){
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
    public PageResult<SeatReservationEO> EmployeePageQuery(SeatReservationPageQueryREQ seatReservationPageQueryREQ) {
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
        queryWrapper.eq(SeatReservationEO::getScheduleId, seatReservationPageQueryREQ.getScheduleId())
                .eq(SeatReservationEO::getSeatId, seatReservationPageQueryREQ.getSeatId())
                .eq(SeatReservationEO::getBookingStatus, seatReservationPageQueryREQ.getBookingStatus());

        //执行分页查询
        Page<SeatReservationEO> seatReservationResult = seatReservationMapper.selectPage(page, queryWrapper);

        //封装返回结果
        PageResult<SeatReservationEO> pageResult = new PageResult<>();
        pageResult.setTotal(seatReservationResult.getTotal());
        pageResult.setRecords(seatReservationResult.getRecords());
        return pageResult;
    }

    @Override
    public PageResult<SeatReservationRES> UserPageQuery(SeatReservationPageQueryREQ seatReservationPageQueryREQ) {
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
        queryWrapper.eq(SeatReservationEO::getScheduleId, seatReservationPageQueryREQ.getScheduleId())
                .eq(SeatReservationEO::getSeatId, seatReservationPageQueryREQ.getSeatId())
                .eq(SeatReservationEO::getBookingStatus, seatReservationPageQueryREQ.getBookingStatus());

        //执行分页查询
        Page<SeatReservationEO> seatReservationResult = seatReservationMapper.selectPage(page, queryWrapper);

        //将EO转换为RES
        List<SeatReservationRES> resList = seatReservationResult.getRecords().stream()
                .map(eo -> {
                    SeatReservationRES res = new SeatReservationRES();
                    BeanUtil.copyProperties(eo, res);
                    return res;
                })
                .collect(Collectors.toList());

        //封装返回结果
        PageResult<SeatReservationRES> pageResult = new PageResult<>();
        pageResult.setTotal(seatReservationResult.getTotal());
        pageResult.setRecords(resList);
        return pageResult;
    }

    /*
    *
    */
    private SeatReservationEO getSeatReservationEO(Long scheduleId, Long seatId, Integer bookingStatus){
        LambdaQueryWrapper<SeatReservationEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SeatReservationEO::getScheduleId, scheduleId)
                .eq(SeatReservationEO::getSeatId, seatId)
                .eq(SeatReservationEO::getBookingStatus, bookingStatus) ;
        return seatReservationMapper.selectOne(queryWrapper);
    }
    private void validateSeatReservationIsExist(Long scheduleId, Long seatId, Integer bookingStatus){
        SeatReservationEO seatReservationEO = getSeatReservationEO(scheduleId, seatId, bookingStatus);
        if (seatReservationEO != null) {
            SEAT_RESERVATION_ALREADY_EXISTS.throwException();
        }
    }
}
