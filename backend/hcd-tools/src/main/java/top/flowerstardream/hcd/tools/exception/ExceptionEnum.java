package top.flowerstardream.hcd.tools.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: 花海
 * @Date: 2025/10/29/00:41
 * @Description: 异常枚举
 */
@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    CONFLICT(409, "资源冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    USER_BUSINESS_EXCEPTION(10000, "用户业务异常"),
    TRAINSEAT_BUSINESS_EXCEPTION(20000, "列车座位业务异常"),
    TICKET_BUSINESS_EXCEPTION(30000, "车票业务异常"),
    ORDER_BUSINESS_EXCEPTION(40000, "订单业务异常"),
    SYSTEM_BUSINESS_EXCEPTION(50000, "系统业务异常"),
    USER_NOT_FOUND(10001, "用户不存在"),
    USER_PASSWORD_ERROR(10002, "用户密码错误"),
    USER_NOT_LOGIN(10003, "用户未登录"),
    USER_NOT_PERMISSION(10004, "用户无权限"),
    USER_NOT_AUTHENTICATED(10005, "用户未认证"),
    USER_NOT_EXIST(10007, "用户不存在");

    private final Integer code;
    private final String message;

    public static ExceptionEnum valueOf(Integer code) {
        for (ExceptionEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public void throwException() {
        throw new CustomException(ExceptionEnum.this);
    }
}
