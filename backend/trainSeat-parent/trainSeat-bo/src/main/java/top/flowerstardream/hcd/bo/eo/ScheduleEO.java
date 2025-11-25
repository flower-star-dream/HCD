package top.flowerstardream.hcd.bo.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.bo.BaseEO;

import java.io.Serializable;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_schedule")
public class ScheduleEO extends BaseEO implements Serializable {

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
