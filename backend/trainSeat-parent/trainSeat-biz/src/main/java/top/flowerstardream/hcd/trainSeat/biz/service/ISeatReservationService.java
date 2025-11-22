package top.flowerstardream.hcd.trainSeat.biz.service;


import top.flowerstardream.hcd.bo.eo.SeatReservationEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.REQ.SeatReservationREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.SeatReservationRES;

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
    void add(SeatReservationREQ seatReservationREQ);

    /**
     * 删除座位预约
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 修改座位预约
     * @param seatReservationREQ
     */
    void update(SeatReservationREQ seatReservationREQ);

    /**
     * 分页查询座位预约列表（通用）
     *
     * @param seatReservationPageQueryREQ 座位预约查询条件
     * @return 座位预约查询分页结果
     */
    PageResult<SeatReservationEO> EmployeePageQuery(SeatReservationREQ seatReservationPageQueryREQ);

    /**
     * 分页查询座位预约列表（用户）
     *
     * @param seatReservationPageQueryREQ 座位预约查询条件
     * @return 座位预约查询分页结果
     */
    PageResult<SeatReservationRES> UserPageQuery(SeatReservationREQ seatReservationPageQueryREQ);
}
