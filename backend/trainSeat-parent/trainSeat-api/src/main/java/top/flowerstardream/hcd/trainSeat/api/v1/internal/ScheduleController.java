package top.flowerstardream.hcd.trainSeat.api.v1.internal;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IScheduleServiceImpl;

@RestController("internalScheduleController")
@RequestMapping("/api/v1/internal/schedule")
@Tag(name = "班次接口服务")
@Slf4j
public class ScheduleController {

    @Resource
    private IScheduleServiceImpl ScheduleServiceImpl;

    /**
     * 查询余票数量
     * @param scheduleId 班次ID
     * @return 余票数量
     */
    @GetMapping("/trainSeat/remaining-count")
    public Result<Integer> getRemainingTicketCount(@RequestParam("scheduleId") Long scheduleId){
        log.info("【班次接口服务】查询余票数量，班次ID: {}", scheduleId);
        Integer remainingTicketCount = ScheduleServiceImpl.getAvailingTickets(scheduleId);
        return Result.successResult(remainingTicketCount);
    };
}
