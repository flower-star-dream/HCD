package top.flowerstardream.hcd.tools.filter;


import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.flowerstardream.hcd.tools.constant.JwtClaimsConstant;
import top.flowerstardream.hcd.tools.properties.JwtProperties;
import top.flowerstardream.hcd.tools.utils.JwtUtil;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:05
 * @Description: 鉴权过滤器
 */

@Component
@Slf4j
public class AuthorizeFilter extends AbstractGatewayFilterFactory<AuthorizeFilter.Config> implements Ordered, GlobalFilter {

    public static class Config {
        // 配置类，用于定义过滤器参数
    }

    private static final String HEADER_TOKEN = "Authorization"; // 定义token请求头名称
    private static final String HEADER_BIZ_SIDE = "biz_side";

    @Resource
    private JwtProperties jwtProperties;

    private boolean pathChick(String path){
        log.info("path:{}",path);
        if (path.contains("/login") ||
                path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.contains("/webjars") ||
                path.contains("/swagger-resources") ||
                path.contains("/doc.html") ||
                path.contains("/favicon.ico") ||
                path.contains("/actuator") ||
                path.contains("/knife4j")) {
            return true;
        }
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        
        //2.判断是否是预检请求
        if (request.getMethod().name().equals("OPTIONS")) {
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        //3.判断是否是登录或swagger/knife4j相关路径
        String path = request.getURI().getPath();
        if (pathChick(path)) {
            return chain.filter(exchange);
        }


        //3.获取token
        String token = request.getHeaders().getFirst(HEADER_TOKEN);

        //4.判断token是否存在，并移除Bearer前缀（如果有）
        if(StringUtils.isBlank(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 移除Bearer前缀（如果存在）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        //5.判断biz_side属于哪一个端
        String bizSide = request.getHeaders().getFirst(HEADER_BIZ_SIDE);
        String secretKey;
        long refreshTime;

        if (StringUtils.isNotBlank(bizSide)) {
            if (BizSide.ADMIN.getBizSide().equals(bizSide)) {
                secretKey = jwtProperties.getAdminSecretKey();
                refreshTime = jwtProperties.getAdminRefreshTime();
            } else if (BizSide.USER.getBizSide().equals(bizSide)) {
                secretKey = jwtProperties.getUserSecretKey();
                refreshTime = jwtProperties.getUserRefreshTime();
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        } else {
            // 如果没有提供biz_side头部，则直接返回未认证
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //6.判断token是否有效
        try {
            log.info("开始验证token，bizSide: {}, token长度: {}", bizSide, token.length());
            Claims claimsBody = JwtUtil.getClaimsBody(secretKey, token);
            if (claimsBody == null) {
                log.error("token解析失败，无法获取claims");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            log.info("token解析成功，获取到claims");
            //是否是过期
            int result = JwtUtil.verifyToken(claimsBody, refreshTime);
            log.info("token验证结果: {}", result);
            if(result == 1 || result  == 2){
                log.error("token验证失败，结果码: {}", result);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            log.info("token验证成功，允许访问");
        }catch (Exception e){
            log.error("token验证过程中发生异常:", e);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //6.放行
        return chain.filter(exchange);
    }

    /**
     * @param config
     * @return
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            //1.获取request和response对象
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 2.判断是否是登录或swagger/knife4j相关路径
            String path = request.getPath().toString();
            if (pathChick(path)) {
                return chain.filter(exchange);
            }
            String token = request.getHeaders().getFirst(HEADER_TOKEN);
            
            // 判断token是否存在，并移除Bearer前缀（如果有）
            if(StringUtils.isBlank(token)){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            // 移除Bearer前缀（如果存在）
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            //3.判断biz_side属于哪一个端
            String bizSide = request.getHeaders().getFirst(HEADER_BIZ_SIDE);
            String secretKey;

            if (StringUtils.isNotBlank(bizSide)) {
                if (BizSide.ADMIN.getBizSide().equals(bizSide)) {
                    secretKey = jwtProperties.getAdminSecretKey();
                } else if (BizSide.USER.getBizSide().equals(bizSide)) {
                    secretKey = jwtProperties.getUserSecretKey();
                } else {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }
            } else {
                // 如果没有提供biz_side头部，则直接返回未认证
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            //4.获取claims
            Claims claimsBody = JwtUtil.getClaimsBody(secretKey, token);
            String permissionLevel = null;
            if (claimsBody != null) {
                Object permissionObj = claimsBody.get(JwtClaimsConstant.PERMISSION_LEVEL);
                if (permissionObj != null) {
                    permissionLevel = permissionObj.toString();
                }
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            //5. 权限校验逻辑
//            if (path.contains("/user/info") && !token.equals("valid-token-for-user")) {
//                return unauthorizedResponse(exchange);
//            }
//            if (path.contains("/order/details") && !token.equals("valid-token-for-order")) {
//                return unauthorizedResponse(exchange);
//            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        byte[] bytes = "Unauthorized".getBytes();
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     * 优先级设置  值越小  优先级越高
     * @return -101设置先于security 执行
     */
    @Override
    public int getOrder() {
        return -101;
    }
}
