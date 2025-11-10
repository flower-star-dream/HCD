package top.flowerstardream.hcd.user.api.v1.mgmt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.user.ao.req.PassengerPageQueryREQ;
import top.flowerstardream.hcd.user.biz.service.IPassengerService;
import top.flowerstardream.hcd.user.bo.eo.PassengerEO;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.getTraceId;

/**
 * @Author: 花海
 * @Date: 2025-11-10
 * @Description: 后管乘车人接口
 */
@RestController("mgmtPassengerController")
@RequestMapping("/api/v1/mgmt/user/passenger")
@Tag(name = "B端乘车人相关接口", description = "B端乘车人相关接口")
@Slf4j
public class PassengerController {

    @Resource
    private IPassengerService passengerService;

    /**
     * 分页查询乘车人列表
     *
     * @param queryREQ 查询条件
     * @return 乘车人列表分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询乘车人列表", description = "根据条件分页查询乘车人信息")
    public Result<PageResult<PassengerEO>> pageQuery(PassengerPageQueryREQ queryREQ) {
        log.info("【用户-乘车人】traceId:{},分页查询乘车人列表，查询条件：{}", getTraceId(), queryREQ);
        PageResult<PassengerEO> result = passengerService.pageQuery(queryREQ);
        return Result.successResult(result);
    }

    /**
     * 根据ID查询乘车人详情
     *
     * @param id 乘车人ID
     * @return 乘车人详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询乘车人详情", description = "根据ID查询乘车人详细信息")
    public Result<PassengerEO> getById(@PathVariable Long id) {
        log.info("【用户-乘车人】traceId:{},查询乘车人详情，ID：{}", getTraceId(), id);
        PassengerEO passenger = passengerService.query(id);
        return Result.successResult(passenger);
    }
}
