package top.flowerstardream.hcd.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: 花海
 * @Date: 2025/10/28/22:39
 * @Description: 业务端枚举
 */
@Getter
@AllArgsConstructor
public enum BizSide {
    ADMIN("admin"),
    USER("user");
    private final String bizSide;
}
