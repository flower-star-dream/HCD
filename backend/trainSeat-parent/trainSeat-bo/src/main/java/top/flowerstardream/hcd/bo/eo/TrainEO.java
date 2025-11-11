package top.flowerstardream.hcd.bo.eo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_train")
public class TrainEO extends BaseEO {

    // 列车id
    @TableField("id")
    private Long id;

    // 列车名称
    @TableField("train_name")
    private String trainName;

    // 列车型号
    @TableField("train_model")
    private String trainModel;

    // 座位数
    @TableField("seat_num")
    private Integer seatNum;

    // 服务年数
    @TableField("service_years")
    private Integer serviceYears;

}
