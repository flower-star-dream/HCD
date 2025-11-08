package top.flowerstardream.hcd.bo.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_schedule")
public class scheduleEO extends BaseEO {

    // 班次号
    @TableField("id")
    private Long id;

    // 列车号
    @TableField("train_id")
    private Long trainId;

    // 路线号
    @TableField("route_id")
    private Long routeId;

    // 列车长
    @TableField("conductor")
    private String conductor;



}
