package top.flowerstardream.hcd.trainSeat.biz.service;


import top.flowerstardream.hcd.bo.eo.RouteStationsEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.RouteStationsPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.RouteStationsREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.RouteStationsRES;

import java.util.List;

/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 路线站点服务
 */
public interface IRouteStationsService {

    /**
     * 新增路线站点
     * @param routeStationsREQ
     */
    void addRouteStations(RouteStationsREQ routeStationsREQ);
    /**
     * 删除路线站点
     * @param ids
     */
    void deleteRouteStations(List<Long> ids);
    /**
     * 修改路线站点
     * @param routeStationsREQ
     */
    void updateRouteStations(RouteStationsREQ routeStationsREQ);
    /**
     * 分页查询路线站点列表（管理）
     * @param routeStationPageQueryREQ 查询条件
     * @return 路线查询分页结果
     */
    PageResult<RouteStationsEO> EmployeePageQuery(RouteStationsPageQueryREQ routeStationPageQueryREQ);

    /**
     * 分页查询路线站点列表（用户）
     * @param routeStationPageQueryREQ 站点查询条件
     * @return 站点查询分页结果
     */
    PageResult<RouteStationsRES> UserPageQuery(RouteStationsPageQueryREQ routeStationPageQueryREQ);
}
