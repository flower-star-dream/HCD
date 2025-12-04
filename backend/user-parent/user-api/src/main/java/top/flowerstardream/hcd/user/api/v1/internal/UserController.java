package top.flowerstardream.hcd.user.api.v1.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.user.ao.dto.UserDTO;
import top.flowerstardream.hcd.user.biz.service.IUserService;
import top.flowerstardream.hcd.user.bo.eo.UserEO;

import java.util.ArrayList;
import java.util.List;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.getTraceId;

/**
 * @Author: 花海
 * @Date: 2025/11/21/14:07
 * @Description: 用户服务相关接口
 */
@RestController("internalUserController")
@RequestMapping("/api/v1/internal/user/user")
@Tag(name = "用户服务", description = "用户服务")
@Slf4j
public class UserController {
    @Resource
    private IUserService userService;

    @PostMapping("/")
    @Operation(summary = "获取用户信息", description = "获取用户信息")
    Result<List<UserDTO>> getUserById(@RequestParam("userIds") List<Long> userIds){
        log.info("【用户-服务】traceId:{}, 获取用户信息, 用户id：{}", getTraceId(), userIds);
        List<UserEO> userEO = userService.getUserInfo(userIds);
        List<UserDTO> userDTOs = new ArrayList<>();
        for (UserEO user : userEO) {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            userDTOs.add(userDTO);
        }
        BeanUtils.copyProperties(userEO, userDTOs);
        return Result.successResult(userDTOs);
    }

    @PostMapping("/by-name")
    Result<List<Long>> getUserIdsByName(@RequestParam("name") String name){
        log.info("【用户-服务】traceId:{}, 获取用户id, 用户名：{}", getTraceId(), name);
        List<Long> userIds = userService.getUserIdsByName(name);
        return Result.successResult(userIds);
    }
}
