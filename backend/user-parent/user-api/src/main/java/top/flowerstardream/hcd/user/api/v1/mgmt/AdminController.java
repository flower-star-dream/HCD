package top.flowerstardream.hcd.user.api.v1.mgmt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.base.ao.req.StartOrStopREQ;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.user.ao.req.LoginREQ;
import top.flowerstardream.hcd.user.ao.req.AdminInfoREQ;
import top.flowerstardream.hcd.user.ao.res.LoginRES;
import top.flowerstardream.hcd.user.biz.service.IAdminService;
import top.flowerstardream.hcd.user.bo.eo.AdminEO;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:43
 * @Description: 后管管理员接口
 */
@RestController
@RequestMapping("/api/v1/mgmt/user")
@Tag(name = "B端管理员相关接口", description = "B端管理员相关接口")
@Slf4j
public class AdminController {

    @Resource
    private IAdminService IAdminService;

    /**
     * 登录
     *
     * @param loginREQ
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员登录")
    public Result<LoginRES> login(@RequestBody LoginREQ loginREQ) {
        log.info("管理员登录：{}", loginREQ);
        LoginRES loginRES = IAdminService.login(loginREQ);
        return Result.successResult(loginRES);
    }

    /**
     * 获取当前登录管理员信息
     *
     * @return
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前登录管理员信息", description = "获取当前登录管理员信息")
    public Result<AdminEO> getInfo() {
        log.info("获取当前登录管理员信息");
        AdminEO adminEO = IAdminService.getInfo();
        return Result.successResult(adminEO);
    }

    /**
     * 更新当前登录管理员信息
     *
     * @return
     */
    @PutMapping("/info")
    @Operation(summary = "更新当前登录管理员信息", description = "更新当前登录管理员信息")
    public Result<AdminEO> updateInfo(@RequestBody AdminInfoREQ adminInfoREQ) {
        IAdminService.updateInfo(adminInfoREQ);
        return Result.successResult();
    }

    /**
     * 启用禁用管理员账号
     * @param startOrStopREQ
     * @return
     */
    @PostMapping("/status")
    @Operation(summary = "启用禁用管理员账号", description = "启用禁用管理员账号")
    public Result<String> startOrStop(@RequestBody StartOrStopREQ startOrStopREQ){
        log.info("启用禁用管理员账号：{}，{}", startOrStopREQ.getStatus(), startOrStopREQ.getId());
        IAdminService.startOrStop(startOrStopREQ.getStatus(), startOrStopREQ.getId());
        return Result.successResult();
    }


    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员登出", description = "管理员登出")
    public Result<Void> logout() {
        log.info("管理员登出");
        IAdminService.logout();
        return Result.successResult();
    }

}
