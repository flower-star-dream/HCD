package top.flowerstardream.hcd.ticket.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: 花海
 * @Date: 2024/11/11
 * @Description: 车票状态枚举
 */
@Getter
public enum TicketStatusEnum {

    /**
     * 正常状态
     */
    NORMAL(1, "正常"),

    /**
     * 已使用
     */
    USED(2, "已使用"),

    /**
     * 已取消
     */
    CANCELLED(3, "已取消"),

    /**
     * 已改签
     */
    CHANGED(4, "已改签"),

    /**
     * 已退票
     */
    REFUNDED(5, "已退票");

    private final Integer code;
    private final String name;

    private static final Map<Integer, TicketStatusEnum> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(TicketStatusEnum::getCode, Function.identity()));

    TicketStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据状态码获取枚举
     * @param code 状态码
     * @return 枚举
     */
    public static TicketStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        return CODE_MAP.get(code);
    }

    /**
     * 根据状态码获取状态名称
     * @param code 状态码
     * @return 状态名称
     */
    public static String getNameByCode(Integer code) {
        TicketStatusEnum statusEnum = getByCode(code);
        return statusEnum != null ? statusEnum.getName() : "未知状态";
    }
}