package top.flowerstardream.hcd.ticket.biz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.flowerstardream.hcd.tools.result.Result;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 花海
 * @Date: 2025/11/13/20:44
 * @Description: 订单服务客户端
 */
@FeignClient("hcd-order")
@RequestMapping("/api/v1/internal/order")
public interface OrderClient {
    /**
     * 根据用户ID获取订单ID列表
     * @param userId 用户ID
     * @return 订单ID列表
     */
    @GetMapping("/order/by-user")
    Result<List<Long>> getOrderIdsByUserId(@RequestParam("userId") Long userId);

    /**
     * 更新订单总价
     * @param orderId 订单ID
     * @param newTotalPrice 新的总价
     */
    @PutMapping("/order/totalPrice")
    Result<Void> updateTotalPrice(@RequestParam("orderId") Long orderId, @RequestParam("newTotalPrice") BigDecimal newTotalPrice);
}
