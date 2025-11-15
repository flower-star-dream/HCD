package top.flowerstardream.hcd.ticket.constant;

import lombok.Getter;
import top.flowerstardream.hcd.tools.exception.CustomException;
import top.flowerstardream.hcd.tools.exception.ICustomError;

/**
 * @Author: 花海
 * @Date: 2024/11/11
 * @Description: 车票异常枚举
 */
@Getter
public enum TicketExceptionEnum implements ICustomError {

    /**
     * 车票不存在
     */
    TICKET_NOT_EXIST(30001, "车票不存在"),

    /**
     * 车票已被使用
     */
    TICKET_ALREADY_USED(30002, "车票已被使用"),

    /**
     * 车票已被取消
     */
    TICKET_ALREADY_CANCELLED(30003, "车票已被取消"),

    /**
     * 无权操作此车票
     */
    TICKET_PERMISSION_DENIED(30004, "无权操作此车票"),

    /**
     * 余票不足
     */
    TICKET_INSUFFICIENT(30005, "余票不足"),

    /**
     * 座位预订失败
     */
    SEAT_RESERVATION_FAILED(30006, "座位预订失败"),

    /**
     * 退票时间已过
     */
    REFUND_TIME_EXPIRED(30007, "退票时间已过"),

    /**
     * 改签时间已过
     */
    CHANGE_TIME_EXPIRED(30008, "改签时间已过");

    private final Integer code;
    private final String message;

    TicketExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 抛出异常
     */
    public void throwException() {
        throw new CustomException(this);
    }
}