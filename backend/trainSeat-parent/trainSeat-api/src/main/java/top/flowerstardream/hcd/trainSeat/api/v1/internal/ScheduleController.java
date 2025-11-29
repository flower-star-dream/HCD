package top.flowerstardream.hcd.trainSeat.api.v1.internal;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RealTimeSchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RealTimeScheduleRES;
import top.flowerstardream.hcd.trainSeat.biz.service.IScheduleService;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IScheduleServiceImpl;

@RestController("internalScheduleController")
@RequestMapping("/api/v1/internal/trainSeat/schedule")
@Tag(name = "班次接口服务")
@Slf4j
public class ScheduleController {

    @Resource
    private IScheduleService ScheduleService;

    /**
     * 查询余票数量
     * @param scheduleId 班次ID
     * @return 余票数量
     */
    @GetMapping("/remaining-count")
    public Result<Integer> getRemainingTicketCount(@RequestParam("scheduleId") Long scheduleId){
        log.info("【班次接口服务】查询余票数量，班次ID: {}", scheduleId);
        Integer remainingTicketCount = ScheduleService.getAvailingTickets(scheduleId);
        return Result.successResult(remainingTicketCount);
    };
}
