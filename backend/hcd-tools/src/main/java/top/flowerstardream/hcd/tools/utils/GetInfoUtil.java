package top.flowerstardream.hcd.tools.utils;

import top.flowerstardream.hcd.tools.context.RequestContext;

/**
 * @Author: 花海
 * @Date: 2025/11/09/15:26
 * @Description: 获取信息工具类
 */
public final class GetInfoUtil {
    public static String getTraceId() {
        RequestContext ctx = TtlContextHolder.get();
        return ctx.getTraceId();
    }

    public static Long getTenantId() {
        RequestContext ctx = TtlContextHolder.get();
        return ctx.getTenantId();
    }

    public static String getTenantName() {
        RequestContext ctx = TtlContextHolder.get();
        return ctx.getTenantName();
    }

    public static String getToken() {
        RequestContext ctx = TtlContextHolder.get();
        return ctx.getToken();
    }
}
