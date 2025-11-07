package top.flowerstardream.hcd.tools.utils;

import reactor.core.publisher.Mono;
import top.flowerstardream.hcd.base.constant.PermissionLevel;

/**
 * @Author: 花海
 * @Date: 2025/11/05/00:07
 * @Description: 用户上下文工具类
 */
public final class ReactiveUserContext {

    public static Mono<Long> getUserId() {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.getOrEmpty("userId")))
                   .cast(Long.class);
    }

    public static Mono<PermissionLevel> getPermissionLevel() {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.getOrEmpty("permissionLevel")))
                   .cast(PermissionLevel.class);
    }

    public static Mono<String> getToken() {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.getOrEmpty("token")))
                   .cast(String.class);
    }

    // 方便在同步代码块里直接“阻塞”拿值（仅用于特殊场景）
    public static Long blockGetUserId() {
        return getUserId().blockOptional().orElse(null);
    }

    public static PermissionLevel blockGetPermissionLevel() {
        return getPermissionLevel().blockOptional().orElse(null);
    }

    public static String blockGetToken() {
        return getToken().blockOptional().orElse(null);
    }
}