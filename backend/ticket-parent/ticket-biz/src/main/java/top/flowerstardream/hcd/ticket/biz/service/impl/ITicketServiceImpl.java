package top.flowerstardream.hcd.ticket.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.flowerstardream.hcd.base.ao.req.StatusChangeREQ;
import top.flowerstardream.hcd.ticket.ao.dto.*;
import top.flowerstardream.hcd.ticket.ao.req.TicketPageQueryREQ;
import top.flowerstardream.hcd.ticket.ao.req.TicketStatusChangeREQ;
import top.flowerstardream.hcd.ticket.ao.res.TicketRES;
import top.flowerstardream.hcd.ticket.biz.client.OrderClient;
import top.flowerstardream.hcd.ticket.biz.mapper.TicketMapper;
import top.flowerstardream.hcd.ticket.biz.service.ITicketService;
import top.flowerstardream.hcd.ticket.bo.TicketEO;
import top.flowerstardream.hcd.ticket.constant.OrderConstant;
import top.flowerstardream.hcd.ticket.constant.TicketStatusEnum;
import top.flowerstardream.hcd.ticket.biz.client.TrainSeatClient;
import top.flowerstardream.hcd.ticket.biz.client.UserClient;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static top.flowerstardream.hcd.ticket.constant.OrderConstant.*;
import static top.flowerstardream.hcd.ticket.constant.TicketExceptionEnum.*;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.*;

/**
 * @Author: 花海
 * @Date: 2024/11/11
 * @Description: 车票服务实现类
 */
@Service
@Slf4j
public class ITicketServiceImpl extends ServiceImpl<TicketMapper, TicketEO> implements ITicketService {

    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private UserClient userClient;

    @Resource
    private OrderClient orderClient;

    @Resource
    private TrainSeatClient trainSeatClient;

    @Resource
    private ITicketServiceImpl self;

    /**
     * 新增车票
     * @param ticketDTO 车票请求信息
     */
    @Override
    public void createTickets(TicketDTO ticketDTO) {
        // 1. 调用列车服务查询余票并预订座位
        ReserveSeatDTO reserveSeatDTO = ReserveSeatDTO.builder()
                .scheduleId(ticketDTO.getScheduleId())
                .startStationId(ticketDTO.getStartStationId())
                .endStationId(ticketDTO.getEndStationId())
                .build();
        ReserveSeatResultDTO reserveSeatResultDTO = trainSeatClient.reserveSeat(reserveSeatDTO).getData();
        List<Long> seatReservationIds = reserveSeatResultDTO.getSeatReservationIds();
        // 2. 校验数据一致性
        if (ticketDTO.getPassengerIds().size() != seatReservationIds.size()) {
            SEAT_RESERVATION_FAILED.throwException();
        }
        seatReservationIds.forEach(seatReservationId -> {
            if (seatReservationId == null || seatReservationId <= 0) {
                // 余票不足，抛出异常
                TICKET_INSUFFICIENT.throwException();
            }
        });
        // 3. 为每个乘车人创建车票
        List<TicketEO> ticketList = new ArrayList<>();
        List<Long> passengerIds = ticketDTO.getPassengerIds();
        for (int i = 0; i < passengerIds.size(); i++) {
            TicketEO ticketEO = TicketEO.builder()
                    .orderId(ticketDTO.getOrderId())
                    .passengerId(passengerIds.get(i))
                    .seatReservationId(seatReservationIds.get(i))
                    .status(TicketStatusEnum.NORMAL.getCode())
                    .money(ticketDTO.getMoney())
                    .startTime(reserveSeatResultDTO.getStartTime())
                    .endTime(reserveSeatResultDTO.getEndTime())
                    .startStationId(ticketDTO.getStartStationId())
                    .endStationId(ticketDTO.getEndStationId())
                    .build();
            ticketList.add(ticketEO);
        }
        // 批量插入车票
        if (!self.saveBatch(ticketList)){
            SEAT_RESERVATION_FAILED.throwException();
        }
    }

    /**
     * 根据用户ID查询车票列表
     * @param userId 用户ID
     * @return 车票响应列表
     */
    @Override
    public List<TicketRES> getTicketsByUserId(Long userId) {
        // 1. 调用订单服务获取该用户的所有订单ID
        List<Long> orderIds = orderClient.getOrderIdsByUserId(userId).getData();
        if (orderIds == null || orderIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 根据订单ID列表查询车票
        LambdaQueryWrapper<TicketEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(TicketEO::getOrderId, orderIds);
        List<TicketEO> ticketList = ticketMapper.selectList(queryWrapper);

        // 转换为响应对象并填充信息
        return convertToRES(ticketList);
    }

    /**
     * 分页查询车票列表（后管端）
     * @param req 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<TicketRES> pageQuery(TicketPageQueryREQ req) {
        if (req == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(10);
        }
        Page<TicketEO> page = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<TicketEO> queryWrapper = Wrappers.lambdaQuery();
        
        // 构建查询条件
        if (req.getOrderId() != null) {
            queryWrapper.eq(TicketEO::getOrderId, req.getOrderId());
        }
        // 根据乘车人姓名查询
        if (StringUtils.isNotBlank((req.getPassengerName()))) {
            List<Long> passengerIds = userClient.getPassengerIdsByName(req.getPassengerName()).getData();
            if (CollUtil.isNotEmpty(passengerIds)) {
                queryWrapper.in(TicketEO::getPassengerId, passengerIds);
            }
        }
        if (StringUtils.isNotBlank((req.getStartStation()))) {
            List<Long> startStationIds = trainSeatClient.getStationIdsByName(req.getStartStation()).getData();
            queryWrapper.in(TicketEO::getStartStationId, startStationIds);
        }
        if (StringUtils.isNotBlank((req.getEndStation()))) {
            List<Long> endStationIds = trainSeatClient.getStationIdsByName(req.getEndStation()).getData();
            queryWrapper.in(TicketEO::getEndStationId, endStationIds);
        }
        if (ObjectUtil.isNotEmpty((req.getRideDateStart()))) {
            queryWrapper.ge(TicketEO::getStartTime, req.getRideDateStart());
        }
        if (ObjectUtil.isNotEmpty(req.getRideDateEnd())) {
            queryWrapper.le(TicketEO::getEndTime, req.getRideDateEnd());
        }
        
        IPage<TicketEO> result = ticketMapper.selectPage(page, queryWrapper);
        List<TicketRES> ticketRESList = convertToRES(result.getRecords());
        return new PageResult<>(result.getTotal(), ticketRESList);
    }

    /**
     * 更新车票状态
     * @param req 状态变更请求
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void updateTicketStatus(TicketStatusChangeREQ req) {
        if (req == null) {
            PARAM_ERROR.throwException();
        }
        TicketEO ticketEO = new TicketEO();
        ticketEO.setId(req.getId());
        TicketEO oldTicket = ticketMapper.selectById(req.getId());
        if (oldTicket == null) {
            TICKET_NOT_EXIST.throwException();
        }
        if (TicketStatusEnum.USED.getCode().equals(oldTicket.getStatus())) {
            TICKET_ALREADY_USED.throwException();
        }
        // 如果是取消车票，需要调用列车服务释放座位
        if (TicketStatusEnum.CANCELLED.getCode().equals(req.getStatus())) {
            if (oldTicket.getSeatReservationId() != null) {
                trainSeatClient.releaseSeat(Collections.singletonList(oldTicket.getSeatReservationId()));
            }
        }
        // 如果是改签车票，需要调用列车服务释放座位并重新预约座位
        if (TicketStatusEnum.CHANGED.getCode().equals(req.getStatus())) {
            if (oldTicket.getSeatReservationId() != null) {
                trainSeatClient.releaseSeat(Collections.singletonList(oldTicket.getSeatReservationId()));
                TicketDTO ticketDTO = TicketDTO.builder()
                        .scheduleId(req.getScheduleId())
                        .orderId(oldTicket.getOrderId())
                        .passengerIds(Collections.singletonList(oldTicket.getPassengerId()))
                        .startStationId(req.getStartStationId())
                        .endStationId(req.getEndStationId())
                        .build();
                ReserveSeatDTO reserveSeatDTO = ReserveSeatDTO.builder()
                        .scheduleId(req.getScheduleId())
                        .startStationId(req.getStartStationId())
                        .endStationId(req.getEndStationId())
                        .build();
                BigDecimal newPrice = trainSeatClient.calcTicketPrice(reserveSeatDTO).getData();
                createTickets(ticketDTO);
                BigDecimal newTotalPrice = newPrice.subtract(oldTicket.getMoney());
                orderClient.updateTotalPrice(oldTicket.getOrderId(), newTotalPrice);
            }
        }
        // 如果是退票车票，需要调用列车服务释放座位
        if (TicketStatusEnum.REFUNDED.getCode().equals(req.getStatus())) {
            if (oldTicket.getSeatReservationId() != null) {
                trainSeatClient.releaseSeat(Collections.singletonList(oldTicket.getSeatReservationId()));
                orderClient.orderRefund(oldTicket.getOrderId());
            }
        }
        ticketEO.setStatus(req.getStatus());
        self.updateById(ticketEO);
    }

    /**
     * 取消车票
     * @param ticketId 车票ID
     * @param userId 用户ID
     */
    @Override
    public void cancelTicket(Long ticketId, Long userId) {
        // 验证车票归属
        TicketEO ticketEO = ticketMapper.selectById(ticketId);
        if (ticketEO == null) {
            TICKET_NOT_EXIST.throwException();
        }
        
        // 通过验证订单归属
        boolean isOwner = ticketEO.getOrderId().equals(userId);
        if (!isOwner) {
            TICKET_PERMISSION_DENIED.throwException();
        }

        if (!TicketStatusEnum.NORMAL.getCode().equals(ticketEO.getStatus())) {
            TICKET_STATUS_NOT_ALLOWED.throwException();
        }

        // 调用订单服务访问订单状态
        Integer orderStatus = orderClient.getOrderStatus(ticketEO.getOrderId()).getData();
        if (orderStatus > ORDER_STATUS_TICKETED) {
            ORDER_STATUS_NOT_ALLOWED.throwException();
        }
        // 更新状态为已取消
        TicketStatusChangeREQ ticketStatusChangeREQ = new TicketStatusChangeREQ();
        ticketStatusChangeREQ.setId(ticketId);
        if (orderStatus.equals(ORDER_STATUS_PENDING_PAY)) {
            ticketStatusChangeREQ.setStatus(TicketStatusEnum.CANCELLED.getCode());
        } else {
            ticketStatusChangeREQ.setStatus(TicketStatusEnum.REFUNDED.getCode());
        }
        updateTicketStatus(ticketStatusChangeREQ);
    }

    /**
     * 根据订单ID查询车票列表
     *
     * @param orderId 订单ID
     * @return 车票响应列表
     */
    @Override
    public List<TicketRES> getTicketsByOrderId(Long orderId) {
        if (orderId == null) {
            PARAM_ERROR.throwException();
        }
        // 根据订单ID查询车票列表
        LambdaQueryWrapper<TicketEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TicketEO::getOrderId, orderId);
        List<TicketEO> ticketList = ticketMapper.selectList(queryWrapper);

        // 转换为响应对象并填充信息
        return convertToRES(ticketList);
    }

    /**
     * 根据订单信息取消车票
     *
     * @param cancelTicketDTO  订单信息
     */
    @Override
    public void cancelTicketByOrder(CancelTicketDTO cancelTicketDTO) {
        LambdaQueryWrapper<TicketEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TicketEO::getOrderId, cancelTicketDTO.getOrderId());
        List<TicketEO> ticketList = ticketMapper.selectList(queryWrapper);
        List<Long> seatReservationIds = ticketList.stream()
                                                .map(TicketEO::getSeatReservationId)
                                                .filter(Objects::nonNull)
                                                .distinct()
                                                .toList();
        if (CollUtil.isNotEmpty(seatReservationIds)) {
            trainSeatClient.releaseSeat(seatReservationIds);
            // 更新车票状态
            if (Objects.equals(cancelTicketDTO.getStatus(), ORDER_STATUS_CANCELLED)) {
                ticketList.forEach(ticket -> ticket.setStatus(TicketStatusEnum.CANCELLED.getCode()));
            } else {
                ticketList.forEach(ticket -> ticket.setStatus(TicketStatusEnum.REFUNDED.getCode()));
            }
            self.updateBatchById(ticketList);
        }
    }

    /**
     * 将EO转换为RES
     * @param ticketList 车票实体列表对象
     * @return 车票响应对象
     */
    private List<TicketRES> convertToRES(List<TicketEO> ticketList) {
        if (CollUtil.isEmpty(ticketList)) {
            return Collections.emptyList();
        }

        /* 1. 一次性查所有关联数据 */
        // 1.1 乘车人
        List<Long> passengerIds = ticketList.stream()
                                            .map(TicketEO::getPassengerId)
                                            .distinct()
                                            .toList();
        Map<Long, PassengerDTO> passengerMap = userClient.getPassengerByIds(passengerIds)
                                                         .getData()
                                                         .stream()
                                                         .collect(Collectors.toMap(PassengerDTO::getId, Function.identity()));

        // 1.2 座位
        List<Long> seatIds = ticketList.stream()
                                          .map(TicketEO::getSeatReservationId)
                                          .distinct()
                                          .toList();
        Map<Long, SeatReservationDTO> seatNumMap = trainSeatClient.getSeatReservationByIds(seatIds)
                                                            .getData()
                                                            .stream()
                                                            .collect(Collectors.toMap(SeatReservationDTO::getId, Function.identity()));

        // 1.3 站点
        List<Long> stationIds = Stream.concat(
                                        ticketList.stream().map(TicketEO::getStartStationId),
                                        ticketList.stream().map(TicketEO::getEndStationId))
                                      .distinct()
                                      .toList();
        Map<Long, StationsDTO> stationNameMap = trainSeatClient.getStationNamesByStationIds(stationIds)
                                                            .getData()
                                                            .stream()
                                                            .collect(Collectors.toMap(StationsDTO::getId, Function.identity()));

        /* 2. 组装结果 */
        return ticketList.stream()
                         .map(ticket -> {
                             TicketRES res = new TicketRES();
                             BeanUtils.copyProperties(ticket, res);

                             // 2.1 乘车人
                             PassengerDTO passenger = passengerMap.get(ticket.getPassengerId());
                             if (passenger != null) {
                                 res.setRealName(passenger.getRealName());
                                 res.setIdCard(passenger.getIdCard());
                                 res.setCardType(passenger.getCardType());
                             }

                             // 2.2 座位号
                             SeatReservationDTO seat = seatNumMap.get(ticket.getSeatReservationId());
                             res.setSeatNumber(seat.getSeatNum());

                             // 2.3 起终站
                             StationsDTO startStation = stationNameMap.get(ticket.getStartStationId());
                             StationsDTO endStation = stationNameMap.get(ticket.getEndStationId());
                             res.setStartStation(startStation.getName());
                             res.setEndStation(endStation.getName());

                             return res;
                         })
                         .toList();
    }
}