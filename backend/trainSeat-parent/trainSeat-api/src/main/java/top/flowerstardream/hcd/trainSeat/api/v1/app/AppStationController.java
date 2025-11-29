package top.flowerstardream.hcd.trainSeat.api.v1.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.StationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.StationRES;
import top.flowerstardream.hcd.trainSeat.biz.service.IStationService;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IStationServiceImpl;

@RestController("appStationController")
@RequestMapping("/api/v1/app/trainSeat/station")
@Slf4j
public class AppStationController {

    @Resource
    private IStationService StationService;

    @GetMapping("/getStations")
    public Result<PageResult<StationRES>> userPageQuery(StationPageQueryREQ stationPageQueryREQ) {
        log.info("【小程序端-站点服务】查询站点，参数: {}", stationPageQueryREQ);
        PageResult<StationRES> result = StationService.UserPageQuery(stationPageQueryREQ);
        return Result.successResult(result);
    }
}
