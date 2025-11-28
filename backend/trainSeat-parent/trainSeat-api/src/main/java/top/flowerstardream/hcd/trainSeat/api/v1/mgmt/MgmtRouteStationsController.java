package top.flowerstardream.hcd.trainSeat.api.v1.mgmt;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RouteStationsPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.RouteStationsREQ;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IRouteStationsServiceImpl;
import top.flowerstardream.hcd.trainSeat.bo.RouteStationsEO;

import java.util.List;

@RestController("mgmtRouteStationsController")
@RequestMapping("/api/v1/app/trainSeat/routeStations")
@Tag(name = "后管端-路线站点管理")
@Slf4j
public class MgmtRouteStationsController {

    @Resource
    private IRouteStationsServiceImpl routeStationsServiceImpl;

    @PostMapping("/addRouteStations")
    public Result<Void> addRouteStations(RouteStationsREQ routeStationsREQ) {
        log.info("【后管端-路线站点服务】添加路线站点，参数: {}", routeStationsREQ);
        routeStationsServiceImpl.addRouteStations(routeStationsREQ);
        return Result.successResult();
    }

    @PutMapping("/updateRouteStations")
    public Result<Void> updateRouteStations(RouteStationsREQ routeStationsREQ) {
        log.info("【后管端-路线站点服务】修改路线站点，参数: {}", routeStationsREQ);
        routeStationsServiceImpl.updateRouteStations(routeStationsREQ);
        return Result.successResult();
    }

    @DeleteMapping("/deleteRouteStations")
    public Result<Void> deleteRouteStations(List<Long> ids) {
        log.info("【后管端-路线站点服务】删除路线站点，参数: {}", ids);
        routeStationsServiceImpl.deleteRouteStations(ids);
        return Result.successResult();
    }

    @GetMapping("/getRouteStations")
    public Result<PageResult<RouteStationsEO>> EmployeePageQuery(RouteStationsPageQueryREQ routeStationsPageQueryREQ){
        log.info("【后管端-路线站点服务】获取路线站点列表，参数: {}", routeStationsPageQueryREQ);
        routeStationsServiceImpl.EmployeePageQuery(routeStationsPageQueryREQ);
        return Result.successResult();
    }
}
