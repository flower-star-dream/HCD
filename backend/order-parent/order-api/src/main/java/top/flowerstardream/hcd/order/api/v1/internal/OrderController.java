package top.flowerstardream.hcd.order.api.v1.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.order.ao.res.OrderRES;
import top.flowerstardream.hcd.order.biz.service.IOrderService;
import top.flowerstardream.hcd.order.biz.service.impl.IOrderServiceImpl;
import top.flowerstardream.hcd.tools.result.Result;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 花海
 * @Date: 2025/11/14/02:04
 * @Description:
 */
@RestController("internalOrderController")
@RequestMapping("/api/v1/internal/order/order")
@Tag(name = "订单服务", description = "订单服务")
public class OrderController {
    @Resource
    private IOrderService orderService;

    /**
     * 根据用户ID获取订单ID列表
     * @param userId 用户ID
     * @return 订单ID列表
     */
    @GetMapping("/by-user")
    @Operation(summary = "根据用户ID获取订单ID列表", description = "根据用户ID获取订单ID列表")
    Result<List<Long>> getOrderIdsByUserId(@RequestParam("userId") Long userId) {
        List<OrderRES> orders = orderService.getUserOrders(userId);
        List<Long> orderIds = orders.stream().map(OrderRES::getId).toList();
        return Result.successResult(orderIds);
    }

    /**
     * 更新订单总价
     * @param orderId 订单ID
     * @param newTotalPrice 新的总价
     */
    @PutMapping("/totalPrice")
    Result<Void> updateTotalPrice(@RequestParam("orderId") Long orderId, @RequestParam("newTotalPrice") BigDecimal newTotalPrice){
        orderService.updateTotalPrice(orderId, newTotalPrice);
        return Result.successResult();
    }

    /**
     * 订单退款
     * @param orderId 订单ID
     */
    @PutMapping("/refund")
    @Operation(summary = "订单退款", description = "订单退款")
    Result<Void> orderRefund(@RequestParam("orderId") Long orderId) throws Exception {
        orderService.orderRefund(orderId);
        return Result.successResult();
    }

    /**
     * 获取订单状态
     * @param orderId 订单ID
     * @return 订单状态
     */
    @GetMapping("/order/status")
    @Operation(summary = "获取订单状态", description = "获取订单状态")
    Result<Integer> getOrderStatus(@RequestParam("orderId") Long orderId) {
        Integer status = orderService.getOrderStatus(orderId);
        return Result.successResult(status);
    }
}
