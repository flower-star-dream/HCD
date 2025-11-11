package top.flowerstardream.hcd.trainSeat.biz.service;


import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.RoutePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.RouteREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.RouteRES;

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
    void add(RouteREQ routeREQ);
    /**
     * 批量删除路线
     * @param ids
     */
    void delete(List<Long> ids);
    /**
     * 修改路线
     * @param routeREQ
     */
    void update(RouteREQ routeREQ);


    /**
     * 分页查询路线列表（通用）
     *
     * @param routePageQueryREQ 查询条件
     * @return 路线查询分页结果
     */
    PageResult<RouteRES> list(RoutePageQueryREQ routePageQueryREQ);

    


}
