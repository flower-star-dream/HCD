package top.flowerstardream.hcd.user.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.flowerstardream.hcd.tools.exception.CustomException;
import top.flowerstardream.hcd.tools.exception.ICustomError;

/**
 * @Author: 花海
 * @Date: 2025/11/11
 * @Description: 用户异常枚举
 */
@Getter
@AllArgsConstructor
public enum UserExceptionEnum implements ICustomError {

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

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public static UserExceptionEnum valueOf(Integer code) {
        for (UserExceptionEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public void throwException() {
        throw new CustomException(UserExceptionEnum.this);
    }
}