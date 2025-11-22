package top.flowerstardream.hcd.order.ao.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: 花海
 * @Date: 2025/11/21/15:35
 * @Description: 订单支付请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单支付请求参数")
public class OrdersPaymentREQ {

    //订单号
    @Schema(description = "订单号")
    private Long orderId;

    /**
     * 订单状态
     */
    @Schema(description = "订单状态")
    private Integer status;

    /**
     * 订单总价
     */
    @Schema(description = "订单总价")
    private BigDecimal totalPrice;

    /**
     * 订单备注
     */
    @Schema(description = "订单备注")
    private String remarks;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
