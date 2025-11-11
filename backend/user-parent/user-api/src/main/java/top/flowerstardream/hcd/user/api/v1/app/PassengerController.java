package top.flowerstardream.hcd.user.api.v1.app;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.user.ao.req.PassengerREQ;
import top.flowerstardream.hcd.user.ao.res.PassengerRES;
import top.flowerstardream.hcd.user.biz.service.IPassengerService;

import java.util.List;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.*;

/**
 * 小程序乘车人控制器
 * <p>
 * 处理小程序端的乘车人相关接口
 * </p>
 *
 * @author 花海
 * @date 2025-11-15
 */
@RestController("appPassengerController")
@RequestMapping("/api/v1/app/user/passenger")
@Tag(name = "C端乘车人相关接口", description = "C端乘车人相关接口")
@Slf4j
public class PassengerController {

    @Resource
    private IPassengerService passengerService;

    /**
     * 获取当前用户关联的乘车人列表
     * <p>
     * 根据用户ID获取所有关联的乘车人信息
     * </p>
     *
     * @return 乘车人列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取当前用户关联的乘车人列表", description = "小程序端获取当前用户关联的乘车人列表接口")
    public Result<List<PassengerRES>> getUserPassengers() {
        log.info("【用户-乘车人】traceId:{}, 获取当前用户关联的乘车人列表，用户ID: {}", getTraceId(), getTenantId());
        List<PassengerRES> passengerList = passengerService.getUserPassengers(getTenantId());
        return Result.successResult(passengerList);
    }

    /**
     * 新增乘车人
     * <p>
     * 新增乘车人信息并关联到当前用户，若身份证号存在则仅保存关联关系
     * </p>
     *
     * @param passengerREQ 乘车人信息
     * @return 操作结果
     */
    @PostMapping("/add")
    @Operation(summary = "新增乘车人", description = "小程序端新增乘车人并关联到当前用户接口")
    public Result<Void> addPassenger(@RequestBody PassengerREQ passengerREQ) {
        log.info("【用户-乘车人】traceId:{}, 新增乘车人，用户ID: {}, 乘车人信息: {}", getTraceId(), getTenantId(), passengerREQ);
        passengerService.addPassenger(getTenantId(), passengerREQ);
        return Result.successResult();
    }

    /**
     * 设置默认乘车人
     * <p>
     * 将指定的乘车人设置为当前用户的默认乘车人，要求该乘车人已关联到当前用户
     * </p>
     *
     * @param passengerId 乘车人ID
     * @return 操作结果
     */
    @PutMapping("/default/{id}")
    @Operation(summary = "设置默认乘车人", description = "小程序端设置当前用户默认乘车人接口")
    public Result<Void> setDefaultPassenger(@PathVariable("id") Long passengerId) {
        log.info("【用户-乘车人】traceId:{}, 设置默认乘车人，用户ID: {}, 乘车人ID: {}", getTraceId(), getTenantId(), passengerId);
        passengerService.setDefaultPassenger(getTenantId(), passengerId);
        return Result.successResult();
    }
}
