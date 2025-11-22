package top.flowerstardream.hcd.user.api.v1.internal;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.user.ao.dto.PassengerDTO;
import top.flowerstardream.hcd.user.ao.res.PassengerRES;
import top.flowerstardream.hcd.user.biz.service.IPassengerService;
import top.flowerstardream.hcd.user.biz.service.IUserService;

import java.util.List;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.*;

/**
 * @Author: 花海
 * @Date: 2025/11/21/20:54
 * @Description: 乘车人服务
 */
@RestController("internalPassengerController")
@RequestMapping("/api/v1/internal/user/passenger")
@Tag(name = "乘车人服务", description = "乘车人服务")
@Slf4j
public class PassengerController {

    @Resource
    private IPassengerService passengerService;

    /**
     * 根据乘车人姓名获取乘车人ID列表
     * @param passengerName 乘车人姓名
     * @return 乘车人ID列表
     */
    @GetMapping("/by-name")
    Result<List<Long>> getPassengerIdsByName(@RequestParam String passengerName){
        log.info("【用户-乘车人】traceId:{}, 根据乘车人姓名获取乘车人ID列表, 乘车人姓名: {}", getTraceId(), passengerName);
        List<Long> passengers = passengerService.getPassengersByName(passengerName);
        return Result.successResult(passengers);
    }

    /**
     * 根据乘车人ID列表获取乘车人信息列表
     * @param passengerIds 乘车人ID列表
     * @return 乘车人信息列表
     */
    @PostMapping("/by-ids")
    Result<List<PassengerDTO>> getPassengerByIds(@RequestParam List<Long> passengerIds){
        log.info("【用户-乘车人】traceId:{}, 根据乘车人ID列表获取乘车人信息列表, 乘车人ID列表: {}", getTraceId(), passengerIds);
        List<PassengerDTO> passengers = passengerService.getPassengersByIds(passengerIds);
        return Result.successResult(passengers);
    }
}
