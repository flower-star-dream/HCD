package top.flowerstardream.hcd.ticket.biz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.flowerstardream.hcd.ticket.ao.dto.PassengerDTO;
import top.flowerstardream.hcd.tools.result.Result;

import java.util.List;

/**
 * @Author: 花海
 * @Date: 2024/11/11
 * @Description: 用户服务Feign客户端
 */
@FeignClient(name = "hcd-user")
@RequestMapping("/api/v1/internal/user")
public interface UserClient {

    /**
     * 根据乘车人姓名获取乘车人ID列表
     * @param passengerName 乘车人姓名
     * @return 乘车人ID列表
     */
    @GetMapping("/passenger/by-name")
    Result<List<Long>> getPassengerIdsByName(String passengerName);

    @PostMapping("/passenger/by-ids")
    Result<List<PassengerDTO>> getPassengerByIds(@RequestParam List<Long> passengerIds);
}