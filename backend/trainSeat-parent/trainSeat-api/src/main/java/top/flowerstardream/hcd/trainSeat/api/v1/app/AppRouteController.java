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
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RoutePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RouteRES;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IRouteServiceImpl;

@RestController("appRouteController")
@RequestMapping("/api/v1/app/trainSeat/route")
@Tag(name = "小程序端-路线管理")
@Slf4j
public class AppRouteController {

    @Resource
    private IRouteServiceImpl routeServiceImpl;

    @GetMapping("/getRoutes")
    public Result<PageResult<RouteRES>> UserPageQuery(@RequestBody RoutePageQueryREQ routePageQueryREQ) {
        log.info("【小程序端-路线服务】查询路线，参数: {}", routePageQueryREQ);
        PageResult<RouteRES> result = routeServiceImpl.UserPageQuery(routePageQueryREQ);
        return Result.successResult(result);
    }
}
