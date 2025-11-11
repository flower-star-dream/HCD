package top.flowerstardream.hcd.trainSeat.biz.service;


import top.flowerstardream.hcd.bo.eo.RouteEO;
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
     * 获取路线信息
     * @param id
     * @return
     */
    RouteEO get(Long id);

    /**
     * 获取路线信息
     * @param id
     * @return
     */
    RouteRES getInfo(Long id);

    PageResult<RouteEO> list(RoutePageQueryREQ routePageQueryREQ);

    PageResult<RouteRES> list();

}
