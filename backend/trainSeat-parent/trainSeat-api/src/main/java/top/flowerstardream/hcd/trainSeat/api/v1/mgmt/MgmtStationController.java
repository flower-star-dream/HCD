package top.flowerstardream.hcd.trainSeat.api.v1.mgmt;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.StationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.StationREQ;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IStationServiceImpl;
import top.flowerstardream.hcd.trainSeat.bo.StationEO;

import java.util.List;

@RestController("mgmtStationController")
@RequestMapping("/api/v1/mgmt/trainSeat/station")
@Tag(name = "管理端-站点管理")
@Slf4j
public class MgmtStationController {

    @Resource
    private IStationServiceImpl stationServiceImpl;

    @PostMapping("/addStation")
    public Result<Void> addStation(StationREQ stationREQ) {
        log.info("【管理端-添加站点】参数: {}", stationREQ);
        stationServiceImpl.addStation(stationREQ);
        return Result.successResult();
    }

    @PutMapping("/updateStation")
    public Result<Void> updateStation(StationREQ stationREQ) {
        log.info("【管理端-修改站点】参数: {}", stationREQ);
        stationServiceImpl.updateStation(stationREQ);
        return Result.successResult();
    }
    @DeleteMapping("/deleteStation")
    public Result<Void> deleteStation(@RequestBody List<Long> ids) {
        log.info("【管理端-删除站点】参数: {}", ids);
        stationServiceImpl.deleteStation(ids);
        return Result.successResult();
    }
    @GetMapping("/getStation")
    public Result<PageResult<StationEO>> EmployeePageQuery(StationPageQueryREQ stationPageQueryREQ) {
        log.info("【管理端-查询站点】参数: {}", stationPageQueryREQ);
        PageResult<StationEO> result = stationServiceImpl.EmployeePageQuery(stationPageQueryREQ);
        return Result.successResult(result);
    }

}
