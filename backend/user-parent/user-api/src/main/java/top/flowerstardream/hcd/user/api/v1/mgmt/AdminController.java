package top.flowerstardream.hcd.user.api.v1.mgmt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.user.ao.req.LoginREQ;
import top.flowerstardream.hcd.user.ao.res.LoginRES;
import top.flowerstardream.hcd.user.biz.service.IAdminService;

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
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员登出", description = "管理员登出")
    public Result<String> logout() {
        return Result.successResult();
    }


}
