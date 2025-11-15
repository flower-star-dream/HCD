package top.flowerstardream.hcd.ticket.api.v1.mgmt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.base.ao.req.StatusChangeREQ;
import top.flowerstardream.hcd.ticket.ao.req.TicketPageQueryREQ;
import top.flowerstardream.hcd.ticket.ao.req.TicketStatusChangeREQ;
import top.flowerstardream.hcd.ticket.ao.res.TicketRES;
import top.flowerstardream.hcd.ticket.biz.service.ITicketService;
import top.flowerstardream.hcd.ticket.bo.TicketEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.tools.utils.GetInfoUtil;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.*;

/**
 * @Author: 花海
 * @Date: 2024/11/11
 * @Description: 后管端车票控制器
 */
@RestController("mgmtTicketController")
@RequestMapping("/api/v1/mgmt/ticket/ticket")
@Tag(name = "后管端-车票管理")
@Slf4j
public class TicketController {

    @Resource
    private ITicketService ticketService;

    /**
     * 分页查询车票列表
     * @param req 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询车票", description = "B端分页查询车票列表")
    @GetMapping("/page")
    public Result<PageResult<TicketRES>> pageQuery(TicketPageQueryREQ req) {
        log.info("【车票-后管】traceId:{}, 分页查询车票，请求参数: {}", getTraceId(), req);
        PageResult<TicketRES> result = ticketService.pageQuery(req);
        return Result.successResult(result);
    }

    /**
     * 更新车票状态
     * @param req 状态变更请求
     * @return 操作结果
     */
    @Operation(summary = "更新车票状态", description = "B端更新车票状态")
    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestBody TicketStatusChangeREQ req) {
        log.info("【车票-后管】traceId:{}, 更新车票状态，请求参数: {}", getTraceId(), req);
        ticketService.updateTicketStatus(req);
        return Result.successResult();
    }

    /**
     * 根据ID查询车票详情
     * @param id 车票ID
     * @return 车票详情
     */
    @Operation(summary = "查询车票详情", description = "B端根据ID查询车票详情")
    @GetMapping("/{id}")
    public Result<TicketRES> getById(@PathVariable("id") Long id) {
        log.info("【车票-后管】traceId:{}, 查询车票详情，车票ID: {}", getTraceId(), id);

        // 这里需要实现根据ID查询详情的方法
        // TicketDTO ticketDTO = ticketService.getById(id);
        // return Result.successResult(ticketDTO);
        return Result.successResult();
    }
}