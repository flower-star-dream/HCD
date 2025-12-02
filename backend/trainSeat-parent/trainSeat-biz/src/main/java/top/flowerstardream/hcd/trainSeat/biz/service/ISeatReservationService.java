package top.flowerstardream.hcd.trainSeat.biz.service;


import top.flowerstardream.hcd.base.ao.res.StatusRES;
import top.flowerstardream.hcd.trainSeat.ao.dto.ReserveSeatDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.ReserveSeatResultDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.SeatReservationDTO;
import top.flowerstardream.hcd.trainSeat.ao.req.SeatReservationChangeStatusREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.SeatReservationREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.SeatReservationRES;
import top.flowerstardream.hcd.trainSeat.bo.SeatReservationEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SeatReservationPageQueryREQ;

import java.util.List;

/**
 * @Author: QAQ
 * @Date: 2025/11/10 16:01
 * @Description: 座位预约服务
 */
public interface ISeatReservationService {

    /**
     * 新增座位预约
     * @param seatReservationREQ
     */
    void addSeatReservation(SeatReservationREQ seatReservationREQ);

    /**
     * 删除座位预约
     * @param ids
     */
    void deleteSeatReservation(List<Long> ids);

    /**
     * 修改座位预约
     * @param seatReservationREQ
     */
    void updateSeatReservation(SeatReservationREQ seatReservationREQ);

    /**
     * 分页查询座位预约列表（后管）
     *
     * @param seatReservationPageQueryREQ 座位预约查询条件
     * @return 座位预约查询分页结果
     */
    PageResult<SeatReservationRES> EmployeePageQuery(SeatReservationPageQueryREQ seatReservationPageQueryREQ);

    /**
     * 根据座位预约ID列表获取座位预约列表
     *
     * @param seatReservationIds 座位预约ID列表
     * @return 座位预约列表
     */
    List<SeatReservationDTO> getSeatReservationByIds(List<Long> seatReservationIds);

    /**
     * 释放座位
     *
     * @param seatReservationIds 座位预约ID列表
     */
    void releaseSeat(List<Long> seatReservationIds);

    /**
     * 预订座位
     *
     * @param reserveSeatDTO 预订座位参数
     * @return 座位预约结果
     */
    ReserveSeatResultDTO reserveSeat(ReserveSeatDTO reserveSeatDTO);

    List<StatusRES> getStatus();

    /**
     * 批量更新座位预约状态
     *
     * @param seatReservationChangeStatusREQ 批量更新座位预约状态参数
     */
    void batchUpdateStatus(SeatReservationChangeStatusREQ seatReservationChangeStatusREQ);
}
