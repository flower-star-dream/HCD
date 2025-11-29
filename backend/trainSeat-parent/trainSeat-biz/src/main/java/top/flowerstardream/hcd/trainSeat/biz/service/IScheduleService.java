package top.flowerstardream.hcd.trainSeat.biz.service;

import top.flowerstardream.hcd.trainSeat.ao.pqreq.RealTimeSchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.RealTimeScheduleREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.ScheduleREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RealTimeScheduleRES;
import top.flowerstardream.hcd.trainSeat.ao.res.ScheduleRES;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SchedulePageQueryREQ;

import java.util.List;

/**
 * @Author: QAQ
 * @Date: 2025/11/10 16:01
 * @Description: 班次服务
 */
public interface IScheduleService  {

    /**
     * 新增班次
     * @param scheduleREQ
     */
    void addSchedule(ScheduleREQ scheduleREQ);
    /**
     * 批量删除班次
     * @param ids
     */
    void deleteSchedule(List<Long> ids);
    /**
     * 修改班次
     * @param scheduleREQ
     */
    void updateSchedule(ScheduleREQ scheduleREQ);
    /**
     * 分页查询班次列表（管理）
     *
     * @param schedulePageQueryREQ 查询条件
     * @return 班次查询分页结果
     */
    PageResult<ScheduleRES> EmployeePageQuery(SchedulePageQueryREQ schedulePageQueryREQ);


    /**
     * 根据id查询班次
     *
      * @param realTimeScheduleREQ 查询条件
     * @return 班次信息
     */
    RealTimeScheduleRES getSchedule(RealTimeScheduleREQ realTimeScheduleREQ);

    /**
     * 分页查询实时班次列表（小程序）
     *
     * @param realTimeSchedulePageQueryREQ 搜索条件
     * @return 班次查询分页结果
     */
    PageResult<RealTimeScheduleRES> getRealTimeSchedule(RealTimeSchedulePageQueryREQ realTimeSchedulePageQueryREQ);

    /**
     * 查询余票数量
     * @param scheduleId 班次ID
     * @return 余票数量
     */
    Integer getAvailingTickets(Long scheduleId);
}
