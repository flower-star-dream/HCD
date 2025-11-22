package top.flowerstardream.hcd.order.biz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.flowerstardream.hcd.order.ao.dto.UserDTO;
import top.flowerstardream.hcd.tools.result.Result;

import java.awt.image.RenderedImage;
import java.util.List;

/**
 * @Author: 花海
 * @Date: 2025/11/14/18:21
 * @Description: 用户服务客户端
 */
@FeignClient("hcd-user")
@RequestMapping("/api/v1/internal/user")
public interface UserClient {

    @PostMapping("/user/")
    Result<List<UserDTO>> getUserByIds(@RequestParam List<Long> userIds);
}
