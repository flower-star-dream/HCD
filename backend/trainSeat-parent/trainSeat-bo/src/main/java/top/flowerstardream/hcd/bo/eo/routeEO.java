package top.flowerstardream.hcd.bo.eo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

import java.util.Date;

/**
 * @Author: QAQ
 * @Date: 2025/11/06/19:00
 * @Description: 路线实体对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_route")
public class routeEO extends BaseEO {

    // 路线id
    @TableField("id")
    private Long id;

    // 起点站
    @TableField("start_station")
    private String startingStation;

    // 终点站
    @TableField("end_station")
    private String endStation;

    // 站点数
    @TableField("station_count")
    private Integer stationCount;


}
