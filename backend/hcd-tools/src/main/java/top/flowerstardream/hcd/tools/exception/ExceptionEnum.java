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
    THE_QUERY_PARAMETER_CANNOT_BE_EMPTY(400, "查询参数不能为空"),
    UNAUTHORIZED(401, "登录状态已失效，请重新登录"),
    THE_JWT_TOKEN_HAS_EXPIRED(401, "JWT令牌过期，请重新登录"),
    FORBIDDEN(403, "当前用户无权限"),
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
    DATABASE_EXCEPTION(60000, "数据库异常"),
    CLOCK_ROLLED_BACK(60001, "时钟回拨，拒绝生成 ID"),
    WORKER_ID_OUT_OF_BOUNDS(60002, "workerId 越界"),
    INSERTION_FAILED(60003, "新增失败"),
    DELETION_FAILED(60004, "删除失败"),
    MODIFICATION_FAILED(60005, "修改失败"),
    QUERY_FAILED(60006, "查询失败"),
    FILE_SYSTEM_EXCEPTION(70000, "文件系统异常"),
    FAILED_FILE_UPLOAD(70001, "文件上传失败"),
    FAILED_FILE_DOWNLOAD(70002, "文件下载失败"),
    FAILED_FILE_DELETE(70003, "文件删除失败"),
    THE_CONTENT_OF_THE_FILE_IS_EMPTY(70004, "文件内容为空"),
    USER_NOT_EXIST(10001, "用户不存在"),
    USER_ALREADY_EXISTS(10002, "用户已存在"),
    USER_PASSWORD_ERROR(10003, "用户名或密码错误"),
    USER_STATUS_ENABLE(10004, "启用状态用户禁止删除"),
    THE_OLD_PASSWORD_CANNOT_EQUALS_NEW_ONE(10005, "新密码不能与旧密码相同"),
    THE_NEW_PASSWORD_IS_INCONSISTENT_WITH_THE_CONFIRMED_PASSWORD(10006, "新密码与确认密码不一致"),
    THE_ACCOUNT_HAS_BEEN_DISABLED(10007, "账户已禁用"),
    THE_OLD_PASSWORD_IS_INCORRECT(10008, "旧密码错误"),
    OPENID_CANNOT_BE_EMPTY(10009, "openid不可为空"),
    DATA_DOES_NOT_EXIST(10010, "数据不存在"),
    THIRD_PARTY_DATA_DOES_NOT_EXIST(10011, "第三方数据不存在");


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
