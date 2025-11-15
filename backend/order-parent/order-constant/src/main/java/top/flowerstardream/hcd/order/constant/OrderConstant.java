package top.flowerstardream.hcd.order.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 花海
 * @Date: 2025/11/11
 * @Description: 订单常量
 */
public class OrderConstant {

    /**
     * 订单状态：待支付
     */
    public static final Integer ORDER_STATUS_PENDING_PAY = 0;

    /**
     * 订单状态：已支付
     */
    public static final Integer ORDER_STATUS_PAID = 1;

    /**
     * 订单状态：已出票
     */
    public static final Integer ORDER_STATUS_TICKETED = 2;

    /**
     * 订单状态：已完成
     */
    public static final Integer ORDER_STATUS_COMPLETED = 3;

    /**
     * 订单状态：已取消
     */
    public static final Integer ORDER_STATUS_CANCELLED = 4;

    /**
     * 订单状态：已退款
     */
    public static final Integer ORDER_STATUS_REFUNDED = 5;

    /**
     * 订单状态映射
     */
    public static final Map<Integer, String> ORDER_STATUS_MAP = new HashMap<Integer, String>() {
        {
            put(ORDER_STATUS_PENDING_PAY, "待支付");
            put(ORDER_STATUS_PAID, "已支付");
            put(ORDER_STATUS_TICKETED, "已出票");
            put(ORDER_STATUS_COMPLETED, "已完成");
            put(ORDER_STATUS_CANCELLED, "已取消");
            put(ORDER_STATUS_REFUNDED, "已退款");
        }
    };

    /**
     * 获取订单状态描述
     * @param status 状态码
     * @return 状态描述
     */
    public static String getStatusDesc(Integer status) {
        return ORDER_STATUS_MAP.getOrDefault(status, "未知状态");
    }
}