package top.flowerstardream.hcd.tools.exception;

import lombok.Getter;

/**
 * 业务异常类
 *
 * @author 花海
 * @date 2025-10-14
 */
@Getter
public class CustomException extends RuntimeException {

    private final ICustomError iCustomError;

    public CustomException(ICustomError iCustomError) {
        super(iCustomError.getMessage());
        this.iCustomError = iCustomError;
    }
    public CustomException(Integer code, String message) {
        super(message);
        this.iCustomError = ICustomError.of(code, message);
    }

}