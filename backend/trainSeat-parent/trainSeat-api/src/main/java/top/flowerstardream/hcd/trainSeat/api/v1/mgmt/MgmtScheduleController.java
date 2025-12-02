package top.flowerstardream.hcd.trainSeat.api.v1.mgmt;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.ScheduleREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.ScheduleRES;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IScheduleServiceImpl;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;

import java.util.List;

@RestController("MgmtScheduleController")
@RequestMapping("/api/v1/mgmt/trainSeat/schedule")
@Tag(name = "后管端-班次管理")
@Slf4j
public class MgmtScheduleController {

    @Resource
    private IScheduleServiceImpl scheduleServiceImpl;

    @PostMapping("/addSchedule")
    public Result<Void> addSchedule(@RequestBody ScheduleREQ scheduleREQ) {
        log.info("【后管端-班次服务】添加班次，参数: {}", scheduleREQ);
        scheduleServiceImpl.addSchedule(scheduleREQ);
        return Result.successResult();
    }
    @PutMapping("/updateSchedule")
    public Result<Void> updateSchedule(@RequestBody ScheduleREQ scheduleREQ) {
        log.info("【后管端-班次服务】修改班次，参数: {}", scheduleREQ);
        scheduleServiceImpl.updateSchedule(scheduleREQ);
        return Result.successResult();
    }
    @DeleteMapping("/deleteSchedule")
    public Result<Void> deleteSchedule(@RequestBody List<Long> ids) {
        log.info("【后管端-班次服务】删除班次，参数: {}", ids);
        scheduleServiceImpl.deleteSchedule(ids);
        return Result.successResult();
    }
    @GetMapping("/getSchedules")
    public Result<PageResult<ScheduleRES>> EmployeePageQuery(SchedulePageQueryREQ schedulePageQueryREQ) {
        log.info("【后管端-班次服务】查询班次，参数: {}", schedulePageQueryREQ);
        PageResult<ScheduleRES> pageResult = scheduleServiceImpl.EmployeePageQuery(schedulePageQueryREQ);
        return Result.successResult(pageResult);
    }
}
