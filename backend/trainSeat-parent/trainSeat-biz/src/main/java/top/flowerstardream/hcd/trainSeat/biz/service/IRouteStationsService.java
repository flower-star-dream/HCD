package top.flowerstardream.hcd.trainSeat.biz.service;


import top.flowerstardream.hcd.trainSeat.ao.dto.CalcTicketPriceDTO;
import top.flowerstardream.hcd.trainSeat.ao.req.RouteStationsREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RouteStationsRES;
import top.flowerstardream.hcd.trainSeat.bo.RouteStationsEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RouteStationsPageQueryREQ;

import java.math.BigDecimal;
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
    PageResult<RouteStationsRES> EmployeePageQuery(RouteStationsPageQueryREQ routeStationPageQueryREQ);

    /**
     * 计算车票价格
     * @param calcTicketPriceDTO
     * @return
     */
    BigDecimal calcTicketPrice(CalcTicketPriceDTO calcTicketPriceDTO);
}
