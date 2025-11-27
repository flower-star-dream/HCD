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
import org.springframework.web.bind.annotation.RequestParam;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.dto.ReserveSeatDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.ReserveSeatResultDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.SeatReservationDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.TicketSeatReservationDTO;
import top.flowerstardream.hcd.trainSeat.ao.req.SeatReservationREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.SeatReservationRES;
import top.flowerstardream.hcd.trainSeat.biz.mapper.ScheduleMapper;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;
import top.flowerstardream.hcd.trainSeat.bo.SeatReservationEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SeatReservationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.biz.client.TicketClient;
import top.flowerstardream.hcd.trainSeat.biz.mapper.SeatReservationMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.ISeatReservationService;

import java.time.LocalDateTime;
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

    @Resource
    private ScheduleMapper scheduleMapper;

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

            if (tickets.getData() != null) {
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
                .eq(SeatReservationEO::getSeatNum, seatReservationPageQueryREQ.getSeatNum())
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
                .eq(SeatReservationEO::getSeatNum, seatReservationPageQueryREQ.getSeatNum())
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

        return seatReservationEOs.stream().map(seatReservationEO -> {
            SeatReservationDTO seatReservationDTO = new SeatReservationDTO();
            seatReservationDTO.builder()
                    .id(seatReservationEO.getId())
                    .seatNum(seatReservationEO.getSeatNum());
            return seatReservationDTO;
        }).collect(Collectors.toList());
    }
    /**
     * 外部调用
     * */
    public Result<Void> releaseSeat(@RequestParam("seatReservationIds") List<Long> seatReservationIds){

        //参数校验
        if (seatReservationIds == null || seatReservationIds.isEmpty()) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //修改座位预订状态
        LambdaQueryWrapper<SeatReservationEO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeatReservationEO::getId, seatReservationIds);
        List<SeatReservationEO> seatReservationEOs = seatReservationMapper.selectList(queryWrapper);

        for (SeatReservationEO seatReservationEO : seatReservationEOs) {
            seatReservationEO.setBookingStatus(0);
            seatReservationMapper.updateById(seatReservationEO);
        }
        return Result.successResult();
    }
    /**
     * 外部调用
     * */
    public ReserveSeatResultDTO reserveSeat(ReserveSeatDTO reserveSeatDTO){
        //参数校验
        if (reserveSeatDTO == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //查询起始时间与终止时间
        LambdaQueryWrapper<ScheduleEO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScheduleEO::getId, reserveSeatDTO.getScheduleId());
        ScheduleEO scheduleEO = scheduleMapper.selectOne(queryWrapper);
        LocalDateTime startTime = LocalDateTime.parse(scheduleEO.getStartTime());
        LocalDateTime endTime = LocalDateTime.parse(scheduleEO.getEndTime());

        //根据班次号查询座位信息
        LambdaQueryWrapper<SeatReservationEO> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(SeatReservationEO::getScheduleId, reserveSeatDTO.getScheduleId())
                .eq(SeatReservationEO::getBookingStatus, 1);

        List<SeatReservationEO> seatReservationEOs = seatReservationMapper.selectList(queryWrapper1);
        //过滤符合条件的EO，收录座位号
        List<Long> seatNumList = seatReservationEOs.stream()
                .map(SeatReservationEO::getId)
                .collect(Collectors.toList());

        //修改座位预订状态
        for (SeatReservationEO seatReservationEO : seatReservationEOs) {
            seatReservationEO.setBookingStatus(1);
            seatReservationMapper.updateById(seatReservationEO);
        }

        return  ReserveSeatResultDTO.builder()
                .seatReservationIds(seatNumList)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

}
