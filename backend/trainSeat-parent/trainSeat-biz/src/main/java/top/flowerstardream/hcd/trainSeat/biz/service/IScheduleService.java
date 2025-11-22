package top.flowerstardream.hcd.trainSeat.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.flowerstardream.hcd.bo.eo.ScheduleEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.SchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.ScheduleREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.ScheduleRES;

import java.util.List;

/**
 * @Author: QAQ
 * @Date: 2025/11/10 16:01
 * @Description: 排班服务
 */
public interface IScheduleService  {

    /**
     * 新增排班
     * @param scheduleREQ
     */
    void addSchedule(ScheduleREQ scheduleREQ);
    /**
     * 批量删除排班
     * @param ids
     */
    void deleteSchedule(List<Long> ids);
    /**
     * 修改排班
     * @param scheduleREQ
     */
    void updateSchedule(ScheduleREQ scheduleREQ);
    /**
     * 分页查询排班列表（通用）
     *
     * @param schedulePageQueryREQ 查询条件
     * @return 排班查询分页结果
     */
    PageResult<ScheduleEO> PageQuery(SchedulePageQueryREQ schedulePageQueryREQ);
}
