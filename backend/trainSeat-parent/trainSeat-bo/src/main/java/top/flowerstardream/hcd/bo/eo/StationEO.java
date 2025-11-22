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
@TableName("hcd_station")
public class StationEO extends BaseEO implements Serializable {

    // 站点名称
    @TableField("station_name")
    private String stationName;

    // 站点地址
    @TableField("address")
    private String address;


}
