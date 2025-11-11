package top.flowerstardream.hcd.user.api.v1.mgmt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.base.ao.req.StartOrStopREQ;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.user.ao.req.UserPageQueryREQ;
import top.flowerstardream.hcd.user.biz.service.IUserService;
import top.flowerstardream.hcd.user.bo.eo.UserEO;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.getTraceId;

/**
 * 后管用户控制器
 * <p>
 * 处理后管端的用户相关接口，包括用户列表查询和状态管理
 * </p>
 *
 * @author 花海
 * @date 2025-11-15
 */
@Slf4j
@RestController("mgmtUserController")
@RequestMapping("/api/v1/mgmt/user/user")
@Tag(name = "B端用户管理接口", description = "后管端用户管理相关接口")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 分页查询用户列表
     * <p>
     * 后管端查询用户列表，支持按用户名、手机号、状态进行筛选
     * </p>
     *
     * @param queryREQ 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询用户列表", description = "后管端分页查询用户列表接口")
    public Result<PageResult<UserEO>> list(UserPageQueryREQ queryREQ) {
        log.info("【用户-后管】traceId:{}, 分页查询用户列表，查询条件: {}", getTraceId(), queryREQ);
        PageResult<UserEO> pageResult = userService.list(queryREQ);
        return Result.successResult(pageResult);
    }

    /**
     * 更新用户状态
     * <p>
     * 后管端冻结/解冻用户账号
     * </p>
     *
     * @param startOrStopREQ 状态更新请求参数
     * @return 更新结果
     */
    @PostMapping("/status")
    @Operation(summary = "启用禁用用户账号", description = "后管端冻结/解冻用户账号接口")
    public Result<Void> startOrStop(@RequestBody StartOrStopREQ startOrStopREQ) {
        log.info("【用户-后管】traceId:{}, 更新用户状态：状态={}, 用户ID={}", 
                getTraceId(), startOrStopREQ.getStatus(), startOrStopREQ.getId());
        userService.updateUserStatus(startOrStopREQ.getStatus(), startOrStopREQ.getId());
        return Result.successResult();
    }
}
