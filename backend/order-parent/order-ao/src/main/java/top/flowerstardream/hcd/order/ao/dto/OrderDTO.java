package top.flowerstardream.hcd.order.ao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: 花海
 * @Date: 2025/11/11
 * @Description: 订单数据传输对象
 */
@Data
@Schema(description = "订单数据传输对象")
public class OrderDTO implements Serializable {

    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 订单状态
     */
    @Schema(description = "订单状态")
    private Integer status;

    /**
     * 订单备注
     */
    @Schema(description = "订单备注")
    private String remarks;

    /**
     * 订单总价
     */
    @Schema(description = "订单总价")
    private BigDecimal totalPrice;


}