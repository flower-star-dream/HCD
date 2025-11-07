package top.flowerstardream.hcd.tools.interfaces;

import cn.hutool.core.lang.UUID;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.flowerstardream.hcd.base.constant.BizSide;
import top.flowerstardream.hcd.tools.constant.JwtClaimsConstant;
import top.flowerstardream.hcd.tools.context.RequestContext;
import top.flowerstardream.hcd.tools.properties.JwtProperties;
import top.flowerstardream.hcd.tools.utils.JwtUtil;
import top.flowerstardream.hcd.tools.utils.TtlContextHolder;

import java.util.Enumeration;

/**
 * @Author: 花海
 * @Date: 2025/11/07/13:21
 * @Description: TTL上下文拦截器
 */
@Component
@Slf4j
public class TtlContextInterceptor implements HandlerInterceptor {

    private final String bizSide = "biz_side";

    @Resource
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String token = request.getHeader("Authorization");
        String bizSideHeader = request.getHeader(bizSide);
        BizSide side = BizSide.valueOf(bizSideHeader.toUpperCase());
        String secret = BizSide.ADMIN.equals(side) ? jwtProperties.getAdminSecretKey() : jwtProperties.getUserSecretKey();
        if (token != null && token.startsWith("Bearer ")) {
            Claims claims = JwtUtil.getClaimsBody(secret, token.substring(7));
            RequestContext ctx = new RequestContext();
            String tenantId = BizSide.ADMIN.equals(side) ? JwtClaimsConstant.ADMIN_ID : JwtClaimsConstant.USER_ID;
            String traceId = UUID.randomUUID().toString();
            ctx.setTraceId(traceId);
            MDC.put("traceId", traceId);
            if (claims != null) {
                Long userId = claims.get(tenantId, Long.class);
                if (userId != null) {
                    ctx.setTenantId(userId);
                }
            }
            ctx.setToken(token);
            TtlContextHolder.set(ctx);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        TtlContextHolder.clear();   // 必须清理
    }
}