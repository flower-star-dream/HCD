package top.flowerstardream.hcd.trainSeat.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;
import top.flowerstardream.hcd.tools.exception.CustomException;
import top.flowerstardream.hcd.tools.exception.ICustomError;

/**
 * @Author: QAQ
 * @Date: 2025/11/21 18:05
 * @Description: 座位异常枚举
 */
@Getter
@AllArgsConstructor
public enum TrainSeatExceptionEnum implements ICustomError {

    ROUTE_IS_USED(20001, "路线已被使用，无法被删除"),
    ROUTE_AlREADY_EXISTS(20002, "路线已存在"),
    SCHEDULE_IS_USED(20003, "班次已被使用，无法被删除"),
    SCHEDULE_ALREADY_EXISTS(20004, "班次已存在");
    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public static TrainSeatExceptionEnum valueOf(Integer code) {
        for (TrainSeatExceptionEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public void throwException() {
        throw new CustomException(TrainSeatExceptionEnum.this);
    }


}
