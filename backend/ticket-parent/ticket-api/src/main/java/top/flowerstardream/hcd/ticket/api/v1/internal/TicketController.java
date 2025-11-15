package top.flowerstardream.hcd.ticket.api.v1.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.ticket.ao.dto.TicketDTO;
import top.flowerstardream.hcd.ticket.biz.service.ITicketService;
import top.flowerstardream.hcd.tools.result.Result;

import java.math.BigDecimal;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.getTenantId;

/**
 * @Author: 花海
 * @Date: 2024/11/11
 * @Description: 内部车票服务接口（供其他服务调用）
 */
@RestController("internalTicketController")
@RequestMapping("/api/v1/internal/ticket/ticket")
@Tag(name = "内部服务-车票管理")
@Slf4j
public class TicketController {

    @Resource
    private ITicketService ticketService;

    /**
     * 创建车票（由订单服务调用）
     * @param ticketDTO 车票请求信息
     * @return 总票价
     */
    @Operation(summary = "创建车票", description = "内部服务接口，由订单服务调用创建车票")
    @PostMapping("/create")
    public Result<Void> createTickets(@RequestBody TicketDTO ticketDTO) {
        log.info("【车票-内部服务】创建车票，订单ID: {}", ticketDTO.getOrderId());
        ticketService.createTickets(ticketDTO);
        return Result.successResult();
    }

    /**
     * 取消车票
     * @param orderId
     */
    @PostMapping("/cancel")
    Result<Void> cancelTicket(@RequestParam Long orderId){
        log.info("【车票-内部服务】取消车票，订单ID: {}", orderId);
        ticketService.cancelTicketByOrder(orderId);
        return Result.successResult();
    }
}