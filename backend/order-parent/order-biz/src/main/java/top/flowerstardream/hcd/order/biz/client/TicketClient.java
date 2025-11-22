package top.flowerstardream.hcd.order.biz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.order.ao.dto.CalcTicketPriceDTO;
import top.flowerstardream.hcd.order.ao.dto.CancelTicketDTO;
import top.flowerstardream.hcd.order.ao.dto.TicketDTO;
import top.flowerstardream.hcd.tools.result.Result;

/**
 * @Author: 花海
 * @Date: 2025/11/11/22:52
 * @Description: 票务服务客户端接口
 */
@FeignClient("hcd-ticket")
@RequestMapping("/api/v1/internal/ticket")
public interface TicketClient {

    /**
     * 取消车票
     * @param cancelTicketDTO
     */
    @PostMapping("/ticket/cancel")
    Result<Void> cancelTicket(@RequestBody CancelTicketDTO cancelTicketDTO);

    /**
     * 创建车票
     * @param req
     * @return
     */
    @PostMapping("/ticket/create")
    Result<Void> createTicket(@RequestBody TicketDTO req);

}
