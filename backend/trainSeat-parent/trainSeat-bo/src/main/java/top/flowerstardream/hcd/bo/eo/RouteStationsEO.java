package top.flowerstardream.hcd.bo.eo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_route_stations")
public class RouteStationsEO extends BaseEO {

    // 路线id
    @TableField("route_id")
    private Long routeId;

    // 站点id
    @TableField("station_id")
    private Long stationId;

    // 站点排序
    @TableField("station_sorting")
    private Integer stationSorting;


}
