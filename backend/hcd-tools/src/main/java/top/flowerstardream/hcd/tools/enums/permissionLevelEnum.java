package top.flowerstardream.hcd.tools.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: 花海
 * @Date: 2025/10/29/02:31
 * @Description: 权限等级枚举
 */
@Getter
@AllArgsConstructor
public enum permissionLevelEnum {
    USER("USER"),
    ADMIN("ADMIN");

    private final String permissionLevel;
}
