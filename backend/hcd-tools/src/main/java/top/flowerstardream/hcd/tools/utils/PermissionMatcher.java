package top.flowerstardream.hcd.tools.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.AntPathMatcher;
import top.flowerstardream.hcd.tools.properties.MyGatewayProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: 花海
 * @Date: 2025/11/04/18:18
 * @Description: 权限匹配工具类
 */
@UtilityClass
public class PermissionMatcher {
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    /**
     * 顺序遍历：第一条命中的规则直接生效
     * 返回 null  -> 允许
     * 返回非 null -> 拒绝原因
     */
    public String check(String requestUri,
                        String userPermission,
                        List<MyGatewayProperties.AuthMatrix> matrix) {
        for (MyGatewayProperties.AuthMatrix m : matrix) {
            if (!MATCHER.match(m.getPattern(), requestUri)) {
                continue;          // 不匹配就继续往下找
            }
            // 1. 通配符 "*" 直接放行
            if ("*".equals(m.getRoles())) {
                return null;
            }
            // 2. 角色在允许列表即可
            Set<String> allow = Arrays.stream(m.getRoles().split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
            if (allow.contains(userPermission.toUpperCase())) {
                return null;
            }
            // 3. 命中规则但角色不符 → 直接拒绝，不再看后面规则
            return "Forbidden: 权限 " + userPermission + " 不允许访问";
        }
        // 4. 一条都没匹配到 → 默认拒绝
        return "Forbidden: 无路径匹配";
    }
}

