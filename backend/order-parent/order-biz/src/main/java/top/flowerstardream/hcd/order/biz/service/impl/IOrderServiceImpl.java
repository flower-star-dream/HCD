package top.flowerstardream.hcd.order.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.flowerstardream.hcd.order.ao.dto.CalcTicketPriceDTO;
import top.flowerstardream.hcd.order.ao.dto.TicketDTO;
import top.flowerstardream.hcd.order.ao.req.OrderPageQueryREQ;
import top.flowerstardream.hcd.order.ao.req.OrderREQ;
import top.flowerstardream.hcd.order.ao.req.OrderStatusREQ;
import top.flowerstardream.hcd.order.ao.res.OrderPaymentRES;
import top.flowerstardream.hcd.order.ao.res.OrderRES;
import top.flowerstardream.hcd.order.biz.client.TicketClient;
import top.flowerstardream.hcd.order.biz.client.TrainSeatClient;
import top.flowerstardream.hcd.order.biz.client.UserClient;
import top.flowerstardream.hcd.order.biz.mapper.OrderMapper;
import top.flowerstardream.hcd.order.biz.service.IOrderService;
import top.flowerstardream.hcd.order.bo.OrderEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.tools.utils.WeChatPayUtil;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.order.constant.OrderConstant.*;
import static top.flowerstardream.hcd.order.constant.OrderExceptionEnum.*;
import static top.flowerstardream.hcd.order.constant.OrderRedisPrefixConstant.*;
import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.getTenantId;

/**
 * @Author: 花海
 * @Date: 2025/11/11
 * @Description: 订单服务实现类
 */
@Slf4j
@Service
public class IOrderServiceImpl extends ServiceImpl<OrderMapper, OrderEO> implements IOrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private TicketClient ticketClient;

    @Resource
    private UserClient userClient;

    @Resource
    private TrainSeatClient trainSeatClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private WeChatPayUtil weChatPayUtil;

    @Resource
    @Lazy
    private IOrderServiceImpl self;

    @Override
    public void createOrder(OrderREQ req) {
        Long userId = getTenantId();
        // 参数校验
        if (req == null || userId == null || userId <= 0) {
            ORDER_PERMISSION_DENIED.throwException();
        }
        // 1. 计算票价
        CalcTicketPriceDTO calcTicketPriceDTO = CalcTicketPriceDTO.builder()
                .scheduleId(req.getScheduleId())
                .startStationId(req.getStartStationId())
                .endStationId(req.getEndStationId())
                .build();
        // 向train-seat服务请求计算车票价格
        BigDecimal price = trainSeatClient.calcTicketPrice(calcTicketPriceDTO).getData();
        BigDecimal totalPrice = price.multiply(new BigDecimal(req.getPassengerIds().size()));

        // 2. 幂等性校验, 10s内视为同一订单，不能重复创建订单
        String lockKey = ORDER_REPEAT_PREFIX + userId;
        Boolean firstAccess = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(10));
        if (!Objects.equals(firstAccess, false)) {
            ORDER_REPEAT_CREATE.throwException();
        }

        // 3. 创建订单
        LocalDateTime createTime = LocalDateTime.now();
        OrderEO orderEO = OrderEO.builder()
                        .userId(userId)
                        .status(ORDER_STATUS_PENDING_PAY)
                        .totalPrice(totalPrice)
                        .remarks(req.getRemarks())
                        .createTime(createTime)
                        .updateTime(createTime)
                        .build();
        // 4. 创建车票
        TicketDTO ticketDTO = new TicketDTO();
        BeanUtils.copyProperties(req, ticketDTO);
        ticketDTO.setOrderId(orderEO.getId());
        ticketDTO.setMoney(price);
        createOrderAndTicket(ticketDTO, orderEO);
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    private void createOrderAndTicket(TicketDTO ticketDTO, OrderEO orderEO) {
        if (!self.save(orderEO)) {
            ORDER_CREATE_FAILED.throwException();
        }
        ticketDTO.setOrderId(orderEO.getId());
        // 向票务服务请求新增车票
        ticketClient.createTicket(ticketDTO);
    }

    @Override
    public OrderEO getOrderById(Long id) {
        // 参数校验
        if (id == null || id <= 0) {
            ORDER_PERMISSION_DENIED.throwException();
        }

        // 查询订单
        OrderEO orderEO = getById(id);
        if (orderEO == null) {
            ORDER_NOT_FOUND.throwException();
        }

        return orderEO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(OrderStatusREQ req) {
        // 参数校验
        if (req == null || req.getId() == null || req.getId() <= 0 || req.getStatus() == null) {
            ORDER_PERMISSION_DENIED.throwException();
        }

        // 查询订单是否存在
        OrderEO orderEO = self.getById(req.getId());
        if (orderEO == null) {
            ORDER_NOT_FOUND.throwException();
        }

        // 更新订单状态
        if (Objects.equals(orderEO.getStatus(), ORDER_STATUS_COMPLETED) ||
                Objects.equals(orderEO.getStatus(), ORDER_STATUS_TICKETED) ||
                Objects.equals(orderEO.getStatus(), ORDER_STATUS_CANCELLED)) {
            ORDER_STATUS_INVALID.throwException();
        }

        orderEO.setStatus(req.getStatus());
        if (req.getRemarks() != null) {
            orderEO.setRemarks(req.getRemarks());
        }

        if (!self.updateById(orderEO)) {
            ORDER_UPDATE_FAILED.throwException();
        }
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long userId) {
        // 参数校验
        if (orderId == null || userId == null || userId <= 0) {
            ORDER_PERMISSION_DENIED.throwException();
        }

        // 查询订单
        OrderEO orderEO = self.getById(orderId);
        if (orderEO == null) {
            ORDER_NOT_FOUND.throwException();
        }

        // 验证订单归属
        if (!orderEO.getUserId().equals(userId)) {
            ORDER_PERMISSION_DENIED.throwException();
        }

        // 检查订单状态是否可以取消
        if (Objects.equals(orderEO.getStatus(), ORDER_STATUS_COMPLETED) ||
                Objects.equals(orderEO.getStatus(), ORDER_STATUS_CANCELLED) ||
                Objects.equals(orderEO.getStatus(), ORDER_STATUS_REFUNDED)) {
            ORDER_STATUS_INVALID.throwException();
        }

        // 调用票务服务取消车票
        // 发送MQ消息取消车票
        // TODO: 发送MQ消息到票务服务，由票务服务消费消息后执行车票取消逻辑
        // 示例伪代码：rabbitTemplate.convertAndSend("ticket.cancel", id);
        // 这里暂时直接调用客户端方法，后续需要改造为MQ异步处理
        ticketClient.cancelTicket(orderId);

        // 更新订单状态为已取消
        orderEO.setStatus(ORDER_STATUS_CANCELLED);
        if (!self.updateById(orderEO)) {
            ORDER_CANCEL_FAILED.throwException();
        }
    }

    /**
     * 后管分页查询
     * @param req 查询条件
     * @return
     */
    @Override
    public PageResult<OrderEO> pageQuery(OrderPageQueryREQ req) {
        // 参数校验
        if (req == null) {
            ORDER_PERMISSION_DENIED.throwException();
        }

        // 构建查询条件
        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(10);
        }
        Page<OrderEO> page = new Page<>(req.getPage(), req.getPageSize());
        // 这里可以根据req中的条件构建查询
         LambdaQueryWrapper<OrderEO> queryWrapper = Wrappers.lambdaQuery();
         if (req.getUserId() != null) {
             queryWrapper.eq(OrderEO::getUserId, req.getUserId());
         }
         if (req.getId() != null) {
             queryWrapper.eq(OrderEO::getId, req.getId());
         }

        IPage<OrderEO> orderPage = orderMapper.selectPage(page, queryWrapper);
        // 封装返回结果
        PageResult<OrderEO> pageResult = new PageResult<>();
        pageResult.setTotal(orderPage.getTotal());
        pageResult.setRecords(orderPage.getRecords());
        return pageResult;
    }

    @Override
    public List<OrderRES> getUserOrders(Long userId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            ORDER_PERMISSION_DENIED.throwException();
        }

        // 构建查询条件
        LambdaQueryWrapper<OrderEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(OrderEO::getUserId, userId);

        // 查询用户的所有订单
        List<OrderEO> orderList = orderMapper.selectList(queryWrapper);

        // 转换为OrderRES列表
        return orderList.stream().map(orderEO -> {
            OrderRES orderRES = new OrderRES();
            BeanUtils.copyProperties(orderEO, orderRES);
            return orderRES;
        }).collect(Collectors.toList());
    }

    /**
     * 更新订单总价
     *
     * @param orderId       订单ID
     * @param newTotalPrice 新的总价
     */
    @Override
    public void updateTotalPrice(Long orderId, BigDecimal newTotalPrice) {
        if (orderId == null || newTotalPrice == null) {
            ORDER_PERMISSION_DENIED.throwException();
        }
        OrderEO orderEO = self.getById(orderId);
        if (orderEO == null) {
            ORDER_NOT_FOUND.throwException();
        }
        if (newTotalPrice.compareTo(BigDecimal.ZERO) > 0) {
            // TODO 订单总价减少，需要退款

        } else if (newTotalPrice.compareTo(BigDecimal.ZERO) < 0) {
            orderEO.setStatus(ORDER_STATUS_PENDING_PAY);
        } else {
            // 订单总价不变
            return;
        }
        orderEO.setTotalPrice(orderEO.getTotalPrice().add(newTotalPrice));
        if (!self.updateById(orderEO)) {
            ORDER_UPDATE_FAILED.throwException();
        }
    }

    /**
     * 订单支付
     *
     * @param orderId
     * @return
     */
    public OrderPaymentRES payment(Long orderId) throws Exception {
        // 当前登录用户id
        Long userId = getTenantId();
        String openid = userClient.getOpenid(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                String.valueOf(orderId), //商户订单号
                new BigDecimal("0.01"), //支付金额，单位 元
                "车票订单", //商品描述
                openid //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            ORDER_ALREADY_PAID.throwException();
        }

        OrderPaymentRES res = jsonObject.toJavaObject(OrderPaymentRES.class);
        res.setPackageStr(jsonObject.getString("package"));

//        // 用于测试，直接跳过预支付，交易单生成
//        OrderPaymentRES res = new OrderPaymentRES();
//        paySuccess(orderId);
        return res;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(Long outTradeNo) {

        // 根据订单号查询订单
        OrderEO ordersDB = self.getById(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        OrderEO orders = OrderEO.builder()
                .id(ordersDB.getId())
                .status(ORDER_STATUS_PAID)
                .payTime(LocalDateTime.now())
                .build();

        self.updateById(orders);
    }

    public OrderEO refund(OrderEO ordersDB, BigDecimal orderMoney, BigDecimal originalOrderMoney) throws Exception {
        OrderEO order = OrderEO.builder()
                .id(ordersDB.getId())
                .status(ORDER_STATUS_CANCELLED)
                .build();
        // 订单处于已支付和已出票状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(ORDER_STATUS_PAID) && ordersDB.getStatus().equals(ORDER_STATUS_TICKETED)) {
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    ordersDB.getId().toString(), //商户订单号
                    ordersDB.getId().toString(), //商户退款单号
                    orderMoney,//退款金额，单位 元
                    originalOrderMoney);//原订单金额

            //支付状态修改为 退款
            order.setStatus(ORDER_STATUS_REFUNDED);
        }
        return order;
    }
}