package top.flowerstardream.hcd.tools.interfaces;

import cn.hutool.core.lang.UUID;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.flowerstardream.hcd.base.constant.BizSide;
import top.flowerstardream.hcd.tools.constant.JwtClaimsConstant;
import top.flowerstardream.hcd.tools.context.RequestContext;
import top.flowerstardream.hcd.tools.properties.JwtProperties;
import top.flowerstardream.hcd.tools.properties.MyGatewayProperties;
import top.flowerstardream.hcd.tools.utils.JwtUtil;
import top.flowerstardream.hcd.tools.utils.TtlContextHolder;
import top.flowerstardream.hcd.tools.utils.WhiteListUtil;

import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.getTraceId;

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

    @Resource
    private MyGatewayProperties myGatewayProperties;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // 1. 创建TTL上下文, 设置traceId
        RequestContext ctx = new RequestContext();
        String traceId = request.getHeader("X-Trace-Id");
        log.info("【TTL拦截器】traceId: {}", traceId);
        if (traceId == null) {
            log.warn("【TTL拦截器】traceId为空, 创建traceId");
            traceId = UUID.randomUUID().toString();
        }
        ctx.setTraceId(traceId);
        MDC.put("traceId", traceId);
        // 2. 跳过白名单
        String path = request.getRequestURI();
        if (WhiteListUtil.shouldSkip(path, myGatewayProperties.getWhiteList())
                || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            TtlContextHolder.set(ctx);
            return true;
        }

        // 3. 获取token
        String token = request.getHeader("Authorization");
        String bizSideHeader = request.getHeader(bizSide);
        BizSide side = BizSide.valueOf(bizSideHeader.toUpperCase());
        String secret = BizSide.ADMIN.equals(side) ? jwtProperties.getEmployeeSecretKey() : jwtProperties.getUserSecretKey();
        // 4. 解析token
        if (token != null && token.startsWith("Bearer ")) {
            Claims claims = JwtUtil.getClaimsBody(secret, token.substring(7));
            // 5. 封装TTL上下文
            String tenantId = BizSide.ADMIN.equals(side) ? JwtClaimsConstant.EMPLOYEE_ID : JwtClaimsConstant.USER_ID;
            String tenantName = BizSide.ADMIN.equals(side) ? JwtClaimsConstant.EMPLOYEE_NAME : JwtClaimsConstant.USER_NAME;
            if (claims != null) {
                Long userId = claims.get(tenantId, Long.class);
                String userName = claims.get(tenantName, String.class);
                if (userId != null) {
                    ctx.setTenantId(userId);
                } else {
                    log.warn("userId为空");
                }
                if (userName != null) {
                    ctx.setTenantName(userName);
                } else {
                    log.warn("userName为空");
                }
            }
            ctx.setToken(token);
            TtlContextHolder.set(ctx);
        }
        // 5. 放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        log.info("【TTL拦截器】afterCompletion执行完毕，上下文已清理");
        TtlContextHolder.clear();   // 必须清理
    }
}