package top.flowerstardream.hcd.trainSeat.biz.service;

import top.flowerstardream.hcd.trainSeat.ao.req.StationREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.StationRES;
import top.flowerstardream.hcd.trainSeat.bo.StationEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.StationPageQueryREQ;

import java.util.List;

/**
 * @Author: QAQ
 * @Date: 2025/11/10 16:01
 * @Description: 站点服务
 */

public interface IStationService {

    /**
     * 新增站点
     * @param stationREQ
     */
    void addStation(StationREQ stationREQ);

    /**
     * 批量删除站点
     * @param ids
     */
    void deleteStation(List<Long> ids);

    /**
     * 修改站点
     * @param stationREQ
     */
    void updateStation(StationREQ stationREQ);

    /**
     * 分页查询站点列表（通用）
     *
     * @param stationPageQueryREQ 站点查询条件
     * @return 站点查询分页结果
     */
    PageResult<StationEO> EmployeePageQuery(StationPageQueryREQ stationPageQueryREQ);

    /**
     * 分页查询站点列表（用户）
     *
     * @param stationPageQueryREQ 站点查询条件
     * @return 站点查询分页结果
     */
    PageResult<StationRES> UserPageQuery(StationPageQueryREQ stationPageQueryREQ);

}
