package top.flowerstardream.hcd.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: 花海
 * @Date: 2025/11/04/18:23
 * @Description: 权限等级枚举
 */
@Getter
@AllArgsConstructor
public enum PermissionLevel {
    USER("成员"),
    ADMIN("管理员"),
    SUPER_ADMIN("超级管理员");
    private final String permission;

    public static PermissionLevel getByPermission(String permission) {
        for (PermissionLevel level : values()) {
            if (level.getPermission().equals(permission)) {
                return level;
            }
        }
        return null;
    }
}
