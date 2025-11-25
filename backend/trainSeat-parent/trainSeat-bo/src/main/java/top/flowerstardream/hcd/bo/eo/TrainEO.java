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
@TableName("hcd_train")
public class TrainEO extends BaseEO implements Serializable {

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
