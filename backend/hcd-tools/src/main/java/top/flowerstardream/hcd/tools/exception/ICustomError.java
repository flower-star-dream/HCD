package top.flowerstardream.hcd.tools.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: 花海
 * @Date: 2025/11/11/22:08
 * @Description: 自定义异常接口
 */
public interface ICustomError {

    // 创建一个自定义异常
    @Getter
    @AllArgsConstructor
    class SimpleCustomError implements ICustomError {
        private Integer code;
        private String message;
    }

    static ICustomError of(Integer code, String message) {
        return new SimpleCustomError(code, message);
    }

    Integer getCode();
    String getMessage();
}
