package top.flowerstardream.hcd.trainSeat.api.v1.mgmt;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.StationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.StationREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.StationMgmtRES;
import top.flowerstardream.hcd.trainSeat.ao.res.StationRES;
import top.flowerstardream.hcd.trainSeat.biz.service.IStationService;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IStationServiceImpl;
import top.flowerstardream.hcd.trainSeat.bo.StationEO;

import java.util.List;

@RestController("mgmtStationController")
@RequestMapping("/api/v1/mgmt/trainSeat/station")
@Tag(name = "管理端-站点管理")
@Slf4j
public class MgmtStationController {

    @Resource
    private IStationService stationService;

    @PostMapping("/addStation")
    public Result<Void> addStation(@RequestBody StationREQ stationREQ) {
        log.info("【管理端-添加站点】参数: {}", stationREQ);
        stationService.addStation(stationREQ);
        return Result.successResult();
    }

    @PutMapping("/updateStation")
    public Result<Void> updateStation(@RequestBody StationREQ stationREQ) {
        log.info("【管理端-修改站点】参数: {}", stationREQ);
        stationService.updateStation(stationREQ);
        return Result.successResult();
    }
    @DeleteMapping("/deleteStation")
    public Result<Void> deleteStation(@RequestBody List<Long> ids) {
        log.info("【管理端-删除站点】参数: {}", ids);
        stationService.deleteStation(ids);
        return Result.successResult();
    }
    @GetMapping("/getStation")
    public Result<PageResult<StationMgmtRES>> EmployeePageQuery(StationPageQueryREQ stationPageQueryREQ) {
        log.info("【管理端-查询站点】参数: {}", stationPageQueryREQ);
        PageResult<StationMgmtRES> result = stationService.EmployeePageQuery(stationPageQueryREQ);
        return Result.successResult(result);
    }

}
