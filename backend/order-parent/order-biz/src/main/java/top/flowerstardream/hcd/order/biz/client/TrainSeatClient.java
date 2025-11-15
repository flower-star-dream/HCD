package top.flowerstardream.hcd.order.biz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import top.flowerstardream.hcd.order.ao.dto.CalcTicketPriceDTO;
import top.flowerstardream.hcd.tools.result.Result;

import java.math.BigDecimal;

/**
 * @Author: 花海
 * @Date: 2025/11/14/00:26
 * @Description: 火车座位服务客户端接口
 */
@FeignClient("hcd-trainSeat")
@RequestMapping("/api/v1/internal/trainSeat")
public interface TrainSeatClient {
    /**
     * 计算车票价格
     * @param calcTicketPriceDTO
     * @return
     */
    @GetMapping("/routeStations/calc")
    Result<BigDecimal> calcTicketPrice(@RequestBody CalcTicketPriceDTO calcTicketPriceDTO);
}
