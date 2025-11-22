package top.flowerstardream.hcd.trainSeat.biz.service;

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
    void add(TrainREQ trainREQ);

    void delete(List<Long> ids);

    void update(TrainREQ trainREQ);

    PageResult<TrainRES> PageQuery(TrainPageQueryREQ trainPageQueryREQ);

}
