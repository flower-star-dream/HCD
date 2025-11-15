package top.flowerstardream.hcd.order.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.bo.BaseEO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: 花海
 * @Date: 2025/11/11/21:16
 * @Description: 订单实体对象
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_order")
public class OrderEO extends BaseEO implements Serializable {

    // 用户id
    @TableField("user_id")
    private Long userId;

    // 订单状态
    @TableField("status")
    private Integer status;

    // 订单备注
    @TableField("remarks")
    private String remarks;

    // 订单总价
    @TableField("total_price")
    private BigDecimal totalPrice;

    // 支付时间
    @TableField("pay_time")
    private LocalDateTime payTime;
}
