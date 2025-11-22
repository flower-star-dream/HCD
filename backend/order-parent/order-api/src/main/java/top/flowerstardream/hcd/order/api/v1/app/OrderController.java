package top.flowerstardream.hcd.order.api.v1.app;

import lombok.extern.slf4j.Slf4j;
import top.flowerstardream.hcd.order.ao.req.OrdersPaymentREQ;
import top.flowerstardream.hcd.order.ao.res.OrderMgmtRES;
import top.flowerstardream.hcd.order.ao.res.OrderPaymentRES;
import top.flowerstardream.hcd.tools.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.order.ao.req.OrderREQ;
import top.flowerstardream.hcd.order.ao.res.OrderRES;
import top.flowerstardream.hcd.order.biz.service.IOrderService;
import top.flowerstardream.hcd.order.bo.OrderEO;

import java.util.List;

import static top.flowerstardream.hcd.order.constant.OrderExceptionEnum.*;
import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.*;

/**
 * @Author: 花海
 * @Date: 2025/11/11
 * @Description: C端订单控制器
 */
@RestController("appOrderController")
@RequestMapping("/api/v1/app/order/order")
@Tag(name = "订单服务", description = "C端订单接口")
@Slf4j
public class OrderController {

    @Resource
    private IOrderService orderService;

    /**
     * 创建订单
     * @param req 订单创建请求
     * @return 订单ID
     */
    @Operation(summary = "创建订单", description = "C端创建新订单")
    @PostMapping
    public Result<String> createOrder(@RequestBody OrderREQ req) {
        orderService.createOrder(req);
        return Result.successResult();
    }

    /**
     * 查询订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详情")
    @GetMapping("/{id}")
    public Result<OrderRES> getOrderById(@PathVariable Long id) {
        // 获取当前用户ID
        Long userId = getTenantId();
        // 查询订单并验证归属
        OrderMgmtRES order = orderService.getOrderById(id);
        if (!order.getUserId().equals(userId)) {
            ORDER_PERMISSION_DENIED.throwException();
        }
        OrderRES orderRes = new OrderRES();
        BeanUtils.copyProperties(order, orderRes);
        return Result.successResult(orderRes);
    }

    /**
     * 订单支付
     * @param ordersPaymentREQ
     * @return
     */
    @PutMapping("/payment")
    @Operation(summary = "订单支付", description = "C端订单支付")
    public Result<OrderPaymentRES> payment(@RequestBody OrdersPaymentREQ ordersPaymentREQ) throws Exception {
        log.info("订单支付：{}", ordersPaymentREQ);
        OrderPaymentRES orderPaymentVO = orderService.payment(ordersPaymentREQ);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.successResult(orderPaymentVO);
    }

    /**
     * 取消订单
     * @param orderId 订单ID
     */
    @Operation(summary = "取消订单", description = "C端取消订单")
    @DeleteMapping("/{orderId}")
    public Result<String> cancelOrder(@PathVariable Long orderId) throws Exception {
        // 获取当前用户ID
        Long userId = getTenantId();
        orderService.cancelOrder(orderId, userId);
        return Result.successResult();
    }

    /**
     * 查询用户订单列表
     * @return 分页结果
     */
    @Operation(summary = "查询用户订单列表", description = "C端查询当前用户的订单列表")
    @GetMapping("/list")
    public Result<List<OrderRES>> getUserOrders() {
        // 获取当前用户ID
        Long userId = getTenantId();
        return Result.successResult(orderService.getUserOrders(userId));
    }
}