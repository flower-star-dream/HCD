package top.flowerstardream.hcd.user.api.v1.mgmt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.base.ao.req.StatusChangeREQ;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.user.ao.req.*;
import top.flowerstardream.hcd.user.ao.res.LoginRES;
import top.flowerstardream.hcd.user.biz.service.IEmployeeService;
import top.flowerstardream.hcd.user.bo.eo.EmployeeEO;

import java.util.List;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.*;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:43
 * @Description: 后管员工接口
 */
@RestController
@RequestMapping("/api/v1/mgmt/user/employee")
@Tag(name = "B端员工相关接口", description = "B端员工相关接口")
@Slf4j
public class EmployeeController {

    @Resource
    private IEmployeeService employeeService;

    /**
     * 登录
     *
     * @param loginREQ
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "员工登录", description = "员工登录")
    public Result<LoginRES> login(@RequestBody LoginREQ loginREQ) {
        log.info("【用户-员工】traceId:{}, 员工登录：{}", getTraceId(), loginREQ);
        LoginRES loginRES = employeeService.login(loginREQ);
        return Result.successResult(loginRES);
    }

    /**
     * 获取当前登录员工信息
     *
     * @return
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前登录员工信息", description = "获取当前登录员工信息")
    public Result<EmployeeEO> getInfo() {
        log.info("【用户-员工】traceId:{}, 获取当前登录员工信息:{}", getTraceId(), getTenantId());
        EmployeeEO employeeEO = employeeService.getInfo(getTenantId());
        return Result.successResult(employeeEO);
    }

    /**
     * 更新当前登录员工信息
     *
     * @return
     */
    @PutMapping("/info")
    @Operation(summary = "更新当前登录员工信息", description = "更新当前登录员工信息")
    public Result<EmployeeEO> updateInfo(@RequestBody EmployeeInfoREQ employeeInfoREQ) {
        log.info("【用户-员工】traceId:{}, 更新当前登录员工信息：{}", getTraceId(), employeeInfoREQ);
        employeeService.updateInfo(employeeInfoREQ);
        return Result.successResult();
    }

    /**
     * 获取员工列表
     *
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "获取员工列表", description = "获取员工列表")
    public Result<PageResult<EmployeeEO>> list(EmployeePageQueryREQ employeePageQueryREQ) {
        log.info("【用户-员工】traceId:{}, 获取员工列表", getTraceId());
        return Result.successResult(employeeService.list(employeePageQueryREQ));
    }

    /**
     * 新增员工账号
     * @param
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增员工账号", description = "新增员工账号")
    public Result<String> add(@RequestBody EmployeeREQ employeeREQ) {
        log.info("【用户-员工】traceId:{}, 新增员工账号：{}", getTraceId(), employeeREQ);
        employeeService.add(employeeREQ);
        return Result.successResult();
    }

    /**
     * 修改员工账号
     * @param
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改员工账号", description = "修改员工账号")
    public Result<String> update(@RequestBody EmployeeREQ employeeREQ) {
        log.info("【用户-员工】traceId:{}, 修改员工账号：{}", getTraceId(), employeeREQ);
        log.info("实际注入的Service实现类={}", employeeService.getClass().getName());
        employeeService.update(employeeREQ);
        return Result.successResult();
    }

    /**
     * 批量删除员工账号
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除员工账号", description = "删除员工账号")
    public Result<String> delete(@PathVariable List<Long> ids){
        log.info("【用户-员工】traceId:{}, 删除员工账号：{}", getTraceId(), ids);
        employeeService.delete(ids);
        return Result.successResult();
    }


    /**
     * 启用禁用员工账号
     * @param statusChangeREQ
     * @return
     */
    @PostMapping("/status")
    @Operation(summary = "启用禁用员工账号", description = "启用禁用员工账号")
    public Result<String> startOrStop(@RequestBody StatusChangeREQ statusChangeREQ){
        log.info("【用户-员工】traceId:{}, 启用禁用员工账号：{}，{}", getTraceId(), statusChangeREQ.getStatus(), statusChangeREQ.getId());
        employeeService.startOrStop(statusChangeREQ.getStatus(), statusChangeREQ.getId());
        return Result.successResult();
    }

    /**
     * 重置密码
     * @param
     * @return
     */
    @PostMapping("/resetPwd")
    @Operation(summary = "重置密码", description = "重置密码")
    public Result<String> resetPassword(@RequestBody ResetPwdREQ resetPwdREQ){
        log.info("【用户-员工】traceId:{}, 重置密码：{}", getTraceId(), resetPwdREQ);
        employeeService.resetPassword(resetPwdREQ);
        return Result.successResult();
    }



    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "员工登出", description = "员工登出")
    public Result<Void> logout() {
        log.info("【用户-员工】traceId:{}, 员工登出", getTraceId());
        employeeService.logout();
        return Result.successResult();
    }

}
