package top.flowerstardream.hcd.bo.eo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_station")
public class stationEO extends BaseEO {

    // 站点id
    @TableField("id")
    private Long id;

    // 站点名称
    @TableField("name")
    private String name;

    // 站点地址
    @TableField("address")
    private String address;


}
