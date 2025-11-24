package top.flowerstardream.hcd.trainSeat.biz.service;

import top.flowerstardream.hcd.bo.eo.TrainEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.TrainPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.TrainREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.TrainRES;

import java.util.List;

/**
 * @Author: QAQ
 * @Date: 2025/11/10 16:01
 * @Description: 火车服务
 */
public interface ITrainService {

    /**
     * 新增火车
     * @param trainREQ
     */
    void addTrain(TrainREQ trainREQ);

    /**
     * 批量删除火车
     * @param ids
     */
    void deleteTrain(List<Long> ids);

    /**
     * 修改火车
     * @param trainREQ
     */
    void updateTrain(TrainREQ trainREQ);

    /**
     * 分页查询火车列表（管理）
     *
     * @param trainPageQueryREQ 火车查询条件
     * @return 火车查询分页结果
     */
    PageResult<TrainEO> EmployeePageQuery(TrainPageQueryREQ trainPageQueryREQ);

    /**
     * 分页查询火车列表（用户）
     *
     * @param trainPageQueryREQ 火车查询条件
     * @return 火车查询分页结果
     */
    PageResult<TrainRES> UserPageQuery(TrainPageQueryREQ trainPageQueryREQ);
}
