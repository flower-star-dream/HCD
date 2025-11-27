package top.flowerstardream.hcd.trainSeat.biz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.dto.TicketSeatReservationDTO;

import java.util.List;

/**
 * @author: QAQ
 * @date: 2025/11/25 16:28
 * @description: 订单服务客户端接口
 */
@FeignClient(name = "hcd-ticket")
@RequestMapping("/api/v1/internal/ticket")
public interface TicketClient {
    /**
     * 获取订单对象
     *
     */

    @GetMapping( "/tickets/getTickets")
    Result<List<TicketSeatReservationDTO>> getTickets(@RequestParam Long seatReservationId);





}
