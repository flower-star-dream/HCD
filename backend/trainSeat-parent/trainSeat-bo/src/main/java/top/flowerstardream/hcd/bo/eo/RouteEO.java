package top.flowerstardream.hcd.bo.eo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.bo.BaseEO;

import java.io.Serializable;

/**
 * @Author: QAQ
 * @Date: 2025/11/06/19:00
 * @Description: 路线实体对象
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_route")
public class RouteEO extends BaseEO implements Serializable {

    // 路线名称
    @TableField("route_name")
    private String routeName;

    // 起点站
    @TableField("start_station")
    private String startStation;

    // 终点站
    @TableField("end_station")
    private String endStation;

    // 站点数
    @TableField("station_count")
    private Integer stationCount;


}
