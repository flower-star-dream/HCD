package top.flowerstardream.hcd.trainSeat.api.v1.app;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RealTimeSchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.RealTimeScheduleREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RealTimeScheduleRES;
import top.flowerstardream.hcd.trainSeat.ao.res.ScheduleRES;
import top.flowerstardream.hcd.trainSeat.biz.service.IScheduleService;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IScheduleServiceImpl;

@RestController("appScheduleController")
@RequestMapping("/api/v1/app/trainSeat/schedule")
@Tag(name = "小程序端-班次管理")
@Slf4j
public class AppScheduleController {

    @Resource
    private IScheduleService scheduleService;

    @GetMapping("/getSchedule")
    public Result<RealTimeScheduleRES> getSchedule(RealTimeScheduleREQ realTimeScheduleREQ) {
        log.info("【小程序端-班次服务】查询班次，参数: {}", realTimeScheduleREQ);
        RealTimeScheduleRES result = scheduleService.getSchedule(realTimeScheduleREQ);
        return Result.successResult(result);
    }

    @GetMapping("/realTimeSchedule")
    public Result<PageResult<RealTimeScheduleRES>> getRealTimeSchedule(RealTimeSchedulePageQueryREQ realTimeSchedulePageQueryREQ){
        log.info("【小程序端-班次服务】查询实时班次，参数: {}", realTimeSchedulePageQueryREQ);
        PageResult<RealTimeScheduleRES> realTimeScheduleRes = scheduleService.getRealTimeSchedule(realTimeSchedulePageQueryREQ);
        return Result.successResult(realTimeScheduleRes);
    };

}
