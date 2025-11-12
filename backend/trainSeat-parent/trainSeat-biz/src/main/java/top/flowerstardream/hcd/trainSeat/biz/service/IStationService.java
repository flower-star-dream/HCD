package top.flowerstardream.hcd.trainSeat.biz.service;

import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.StationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.StationREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.StationRES;

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
    void add(StationREQ stationREQ);

    /**
     * 批量删除站点
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 修改站点
     * @param stationREQ
     */
    void update(StationREQ stationREQ);

    /**
     * 分页查询站点列表（通用）
     *
     * @param stationPageQueryREQ 站点查询条件
     * @return 站点查询分页结果
     */
    PageResult<StationRES> list(StationPageQueryREQ stationPageQueryREQ);



}
