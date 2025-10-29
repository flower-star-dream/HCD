package top.flowerstardream.hcd.tools.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import top.flowerstardream.hcd.tools.result.Result;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author 花海
 * @date 2025-10-14
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理全局异常
     */
    @ExceptionHandler
    public ResponseEntity<Result<Void>> handleBusinessException(Exception e) {
        ExceptionEnum exceptionEnum = ExceptionEnum.INTERNAL_SERVER_ERROR;
        Result<Void> result;
        if (e instanceof CustomException be) {
            log.error("业务异常: {}", be.getMessage(), be);
            HttpStatus httpStatus = exceptionEnum2HttpStatus(be.getExceptionEnum());
            result = Result.errorResult(be.getExceptionEnum(), be.getMessage());
            return ResponseEntity.status(httpStatus).body(result);
        } else if (e instanceof ErrorResponseException errorResponseException){
            log.error("业务异常: {}", errorResponseException.getMessage(), errorResponseException);
            int errorCode = errorResponseException.getStatusCode().value();
            exceptionEnum = ExceptionEnum.valueOf(errorCode);
            if (exceptionEnum == null) {
                exceptionEnum = ExceptionEnum.INTERNAL_SERVER_ERROR;
            }
            HttpStatus httpStatus = exceptionEnum2HttpStatus(exceptionEnum);
            result = Result.errorResult(exceptionEnum, exceptionEnum.getMessage());
            return ResponseEntity.status(httpStatus).body(result);
        }
        log.error("系统异常: {}", e.getMessage(), e);
        HttpStatus httpStatus = exceptionEnum2HttpStatus(exceptionEnum);
        result = Result.errorResult(exceptionEnum, exceptionEnum.getMessage());
        return ResponseEntity.status(httpStatus).body(result);
    }

    private static HttpStatus exceptionEnum2HttpStatus(ExceptionEnum exceptionEnum) {
        if (exceptionEnum == null) {
            exceptionEnum = ExceptionEnum.INTERNAL_SERVER_ERROR;
        }
        HttpStatus httpStatus = null;
        try {
            httpStatus = HttpStatus.valueOf(exceptionEnum.getCode());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return httpStatus;
    }

}