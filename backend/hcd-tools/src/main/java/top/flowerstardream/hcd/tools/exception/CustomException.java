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

    private final ExceptionEnum exceptionEnum;

    public CustomException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.exceptionEnum = exceptionEnum;
    }

}