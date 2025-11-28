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
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RouteStationsPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RouteStationsRES;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IRouteStationsServiceImpl;

@RestController("appRouteStationsController")
@RequestMapping("/api/v1/app/trainSeat/routeStations")
@Tag(name = "小程序端-路线站点管理")
@Slf4j
public class AppRouteStationsController {

    @Resource
    private IRouteStationsServiceImpl routeStationsServiceImpl;

    @GetMapping("/getRouteStations")
    public Result<PageResult<RouteStationsRES>> UserPageQuery(@RequestBody RouteStationsPageQueryREQ routeStationsPageQueryREQ) {
        log.info("【小程序端-路线站点接口服务】查询路线站点，参数: {}", routeStationsPageQueryREQ);
        PageResult<RouteStationsRES> result = routeStationsServiceImpl.UserPageQuery(routeStationsPageQueryREQ);
        return Result.successResult(result);
    }
}
