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
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.ScheduleRES;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IScheduleServiceImpl;

@RestController("appScheduleController")
@RequestMapping("/api/v1/app/trainSeat/schedule")
@Tag(name = "小程序端-班次管理")
@Slf4j
public class AppScheduleController {

    @Resource
    private IScheduleServiceImpl scheduleServiceImpl;

    @GetMapping("/getSchedules")
    public Result<PageResult<ScheduleRES>> UserPageQuery(@RequestBody SchedulePageQueryREQ schedulePageQueryREQ) {
        log.info("【小程序端-班次服务】查询班次，参数: {}", schedulePageQueryREQ);
        PageResult<ScheduleRES> result = scheduleServiceImpl.UserPageQuery(schedulePageQueryREQ);
        return Result.successResult(result);
    }

}
