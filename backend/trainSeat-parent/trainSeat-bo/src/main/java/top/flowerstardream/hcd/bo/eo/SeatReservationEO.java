package top.flowerstardream.hcd.bo.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_seat_reservation")
public class SeatReservationEO extends BaseEO {


    // 班次号
    @TableField("schedule_id")
    private Long scheduleId;

    // 座位号
    @TableField("seat_id")
    private Long seatId;

    // 预订状态
    @TableField("booking_status")
    private String bookingStatus;


}
