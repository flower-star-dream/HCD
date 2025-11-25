package top.flowerstardream.hcd.trainSeat.biz.service;

import top.flowerstardream.hcd.trainSeat.ao.req.ScheduleREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.ScheduleRES;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SchedulePageQueryREQ;

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
     * 分页查询排班列表（管理）
     *
     * @param schedulePageQueryREQ 查询条件
     * @return 排班查询分页结果
     */
    PageResult<ScheduleEO> EmployeePageQuery(SchedulePageQueryREQ schedulePageQueryREQ);

    /**
     * 分页查询排班列表（用户）
     *
     * @param schedulePageQueryREQ 查询条件
     * @return 排班查询分页结果
     */
    PageResult<ScheduleRES> UserPageQuery(SchedulePageQueryREQ schedulePageQueryREQ);
}
