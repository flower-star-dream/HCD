package top.flowerstardream.hcd.trainSeat.biz.service;


import top.flowerstardream.hcd.trainSeat.ao.req.RouteREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RouteRES;
import top.flowerstardream.hcd.trainSeat.bo.RouteEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RoutePageQueryREQ;


import java.util.List;

/**
 * @Author: QAQ
 * @Date: 2025/11/10 16:01
 * @Description: 路线服务
 */
public interface IRouteService  {

    /**
     * 新增路线
     * @param routeREQ
     */
    void addRoute(RouteREQ routeREQ);
    /**
     * 批量删除路线
     * @param ids
     */
    void deleteRoute(List<Long> ids);
    /**
     * 修改路线
     * @param routeREQ
     */
    void updateRoute(RouteREQ routeREQ);


    /**
     * 分页查询路线列表（管理）
     *
     * @param routePageQueryREQ 查询条件
     * @return 路线查询分页结果
     */
    PageResult<RouteEO> EmployeePageQuery(RoutePageQueryREQ routePageQueryREQ);

    /**
     * 分页查询路线列表（用户）
     *
     * @param routePageQueryREQ 站点查询条件
     * @return 站点查询分页结果
     */
    PageResult<RouteRES> UserPageQuery(RoutePageQueryREQ routePageQueryREQ);
    


}
