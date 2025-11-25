package top.flowerstardream.hcd.trainSeat.biz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import top.flowerstardream.hcd.trainSeat.ao.DTO.OrderDTO;

import java.util.List;

/**
 * @author: QAQ
 * @date: 2025/11/25 16:28
 * @description: 订单服务客户端接口
 */
@FeignClient(name = "hcd-order")
@RequestMapping("/api/v1/internal/order")
public interface OrderClient {
    /**
     * 获取订单对象
     *
     */
    //TODO  订单信息
    @GetMapping("/order/getOrders")
    List<OrderDTO> getOrders(@RequestParam Long id);





}
