package top.flowerstardream.hcd.tools.exception;

import lombok.Getter;
import top.flowerstardream.hcd.tools.result.ResultCode;

/**
 * 业务异常类
 *
 * @author 花海
 * @date 2025-10-14
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}