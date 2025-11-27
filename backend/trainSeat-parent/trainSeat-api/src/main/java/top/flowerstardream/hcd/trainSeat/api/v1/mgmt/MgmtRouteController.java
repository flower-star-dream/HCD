package top.flowerstardream.hcd.trainSeat.api.v1.mgmt;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: QAQ
 * @Date: 2025/11/10 16:00
 * @Description: 用户接口
 */
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RoutePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.RouteREQ;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IRouteServiceImpl;
import top.flowerstardream.hcd.trainSeat.bo.RouteEO;

import java.util.List;


@RestController("mgmtRouteController")
@RequestMapping("/api/v1/app/trainSeat/route")
@Tag(name = "后管端-路线管理")
@Slf4j
public class MgmtRouteController {

    @Resource
    private IRouteServiceImpl routeServiceImpl;

    @PostMapping("/addRoute")
    public Result<Void> addRoute(RouteREQ routeREQ) {
        log.info("【管理端-路线服务】添加路线，参数: {}", routeREQ);
        routeServiceImpl.addRoute(routeREQ);
        return Result.successResult();
    }

    @DeleteMapping("/deleteRoute")
    public Result<Void> deleteRoute(List<Long> ids) {
        log.info("【管理端-路线服务】删除路线，参数: {}", ids);
        routeServiceImpl.deleteRoute(ids);
        return Result.successResult();
    }

    @PutMapping("/updateRoute")
    public Result<Void> updateRoute(RouteREQ routeREQ) {
        log.info("【管理端-路线服务】修改路线，参数: {}", routeREQ);
        routeServiceImpl.updateRoute(routeREQ);
        return Result.successResult();
    }

    @GetMapping("/EmployeePageQuery")
    public Result<PageResult<RouteEO>> EmployeePageQuery(RoutePageQueryREQ routePageQueryREQ) {
        log.info("【管理端-路线服务】查询路线，参数: {}", routePageQueryREQ);
        PageResult<RouteEO> result = routeServiceImpl.EmployeePageQuery(routePageQueryREQ);
        return Result.successResult(result);
    }


}
