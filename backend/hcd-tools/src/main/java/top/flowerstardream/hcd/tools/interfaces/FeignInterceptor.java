package top.flowerstardream.hcd.tools.interfaces;

import cn.hutool.core.lang.UUID;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import top.flowerstardream.hcd.base.constant.BizSide;
import top.flowerstardream.hcd.tools.context.RequestContext;
import top.flowerstardream.hcd.tools.utils.TtlContextHolder;

import static top.flowerstardream.hcd.base.constant.CommonConstant.*;

/**
 * @Author: 花海
 * @Date: 2025/11/12/17:05
 * @Description: Feign拦截器
 */
@Component
@Slf4j
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        RequestContext ctx = TtlContextHolder.get();
        String traceId = ctx == null ? MDC.get("traceId") : ctx.getTraceId();
        if (traceId == null) {
            traceId = UUID.randomUUID().toString(true);
        }

        /* 1. 必须带的 */
        template.header(TRACE_ID, traceId);

        /* 2. 你想额外塞的任意字段 */
        template.header(TENANT_ID, ctx == null ? "" : ctx.getTenantId().toString());
        template.header(TENANT_NAME, ctx == null ? "" : ctx.getTenantName());
        template.header(BIZ_SIDE, BizSide.SYSTEM.toString());

        log.debug("【Feign 发送请求】url={}, headers={}", template.url(), template.headers());
    }
}