package top.flowerstardream.hcd.hcdgateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import top.flowerstardream.hcd.tools.properties.MyGatewayProperties;
import top.flowerstardream.hcd.tools.properties.JwtProperties;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.tools.utils.*;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:05
 * @Description: 全局过滤器
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class HcdGlobalFilter implements GlobalFilter, Ordered {
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final MyGatewayProperties myGatewayProperties;
    private final String bizSide = "biz_side";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 白名单 & OPTIONS
        if (WhiteListUtil.shouldSkip(path, myGatewayProperties.getWhiteList())
                || "OPTIONS".equalsIgnoreCase(request.getMethod().name())) {
            return chain.filter(exchange);
        }

        // 2. 统一 JWT 校验
        JwtValidator.ValidateResult vr = JwtValidator.validate(
                request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION),
                request.getHeaders().getFirst(bizSide),
                jwtProperties,
                stringRedisTemplate,
                myGatewayProperties);

        if (!vr.isValid()) {
            return ResponseWriter.write(exchange,
                    HttpStatus.UNAUTHORIZED,
                    Result.successResult(401, vr.getMsg()));
        }

        // 3. 角色鉴权
        String permission = vr.getPermissionLevel().toString();
        String error = PermissionMatcher.check(path, permission, myGatewayProperties.getAuthMatrix());
        if (error != null) {
            return ResponseWriter.write(exchange,
                    HttpStatus.FORBIDDEN,
                    Result.successResult(403, error));
        }

        // 4. 向下游透传必要头
//        ServerHttpRequest newReq = exchange.getRequest().mutate()
//                .header("X-User-Id", String.valueOf(vr.getUserId()))
//                .header("X-Biz-Side", vr.getSide().name())
//                .build();
//        return Mono.defer(() -> chain.filter(exchange.mutate().request(newReq).build()))
//                    .contextWrite(Context.of("userId", vr.getUserId()));
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -999;
    }
}

