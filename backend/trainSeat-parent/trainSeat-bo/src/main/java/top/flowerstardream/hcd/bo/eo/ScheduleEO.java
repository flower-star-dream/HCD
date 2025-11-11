package top.flowerstardream.hcd.bo.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_schedule")
public class ScheduleEO extends BaseEO {

    // 列车号
    @TableField("train_id")
    private Long trainId;

    // 路线号
    @TableField("route_id")
    private Long routeId;

    // 列车长
    @TableField("conductor")
    private String conductor;

    // 余票
    @TableField("availing_tickets")
    private Integer availingTickets;

    // 始发站
    @TableField("start_time")
    private String startTime;

    // 终点站
    @TableField("end_time")
    private String endTime;


}
