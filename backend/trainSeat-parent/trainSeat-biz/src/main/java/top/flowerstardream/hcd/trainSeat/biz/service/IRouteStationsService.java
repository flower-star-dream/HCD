package top.flowerstardream.hcd.trainSeat.biz.service;


import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.RouteStationsPageQueryERQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.RouteStationsREQ;

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
    void add(RouteStationsREQ routeStationsREQ);
    /**
     * 删除路线站点
     * @param ids
     */
    void delete(List<Long> ids);
    /**
     * 修改路线站点
     * @param routeStationsREQ
     */
    void update(RouteStationsREQ routeStationsREQ);
    /**
     * 分页查询路线站点列表（通用）
     * @param routeStationPageQueryREQ 查询条件
     * @return 路线查询分页结果
     */
    PageResult<RouteStationsPageQueryERQ> list(RouteStationsPageQueryERQ routeStationPageQueryREQ);
}
