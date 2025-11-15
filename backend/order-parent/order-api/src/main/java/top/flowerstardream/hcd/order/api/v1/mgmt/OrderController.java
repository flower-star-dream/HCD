package top.flowerstardream.hcd.order.api.v1.mgmt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.order.ao.req.OrderStatusREQ;
import top.flowerstardream.hcd.order.bo.OrderEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.order.ao.dto.OrderDTO;
import top.flowerstardream.hcd.order.ao.req.OrderPageQueryREQ;
import top.flowerstardream.hcd.order.biz.service.IOrderService;
import top.flowerstardream.hcd.tools.result.Result;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.getTraceId;

/**
 * @Author: 花海
 * @Date: 2025/11/11
 * @Description: 后管端订单控制器
 */
@RestController("mgmtOrderController")
@RequestMapping("/api/v1/mgmt/order/order")
@Tag(name = "订单管理", description = "B端订单管理接口")
@Slf4j
public class OrderController {

    @Resource
    private IOrderService orderService;

    /**
     * 分页查询订单列表
     * @param req 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询订单列表", description = "B端分页查询所有订单")
    @GetMapping("/page")
    public Result<PageResult<OrderEO>> pageQuery(@RequestBody OrderPageQueryREQ req) {
        log.info("【订单】traceId:{}, 分页查询订单列表：{}", getTraceId(), req);
        return Result.successResult(orderService.pageQuery(req));
    }

    /**
     * 查询订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详情")
    @GetMapping("/{id}")
    public Result<OrderEO> getOrderById(@PathVariable Long id) {
        log.info("【订单】traceId:{}, 查询订单详情：{}", getTraceId(), id);
        return Result.successResult(orderService.getOrderById(id));
    }

    /**
     * 修改订单状态
     * @param req 订单状态修改请求
     */
    @Operation(summary = "修改订单状态", description = "B端修改订单状态")
    @PutMapping("/status")
    public Result<String> updateOrderStatus(@RequestBody OrderStatusREQ req) {
        log.info("【订单】traceId:{}, 修改订单状态：{}", getTraceId(), req);
        orderService.updateOrderStatus(req);
        return Result.successResult();
    }
}