package top.flowerstardream.hcd.tools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * @Author: 花海
 * @Date: 2025/11/02/18:15
 * @Description: Spring WebFlux Security 配置类
 * 包含安全配置和跨域配置
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    
    /**
     * 配置WebFlux安全过滤器链
     * 包含认证规则和跨域配置
     * 
     * @param http ServerHttpSecurity配置对象
     * @return SecurityWebFilterChain 安全过滤器链
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            // 配置跨域支持
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 配置授权规则
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/v1/*/user/login").permitAll()
                .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/doc.html", "/swagger-resources/**", "/webjars/**", "/actuator/**", "/knife4j/**", "/favicon.ico").permitAll() // 允许Swagger及其他公共路径访问
                .pathMatchers("OPTIONS").permitAll() // 允许预检请求
                .anyExchange().authenticated()
            )
            // 暂时禁用CSRF，根据需要可以启用
            .csrf(csrf -> csrf.disable())
            // 确保预检请求能正确处理
            .requestCache(cache -> cache.disable());
            
        return http.build();
    }
    
    /**
     * 配置CORS源
     * 定义允许的域、方法和请求头
     * 
     * @return CorsConfigurationSource 跨域配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许所有域名访问
        configuration.addAllowedOriginPattern("*");
        // 明确指定允许的HTTP方法，包括预检请求的OPTIONS方法
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("OPTIONS");
        // 允许所有请求头，包括自定义的biz_side头部
        configuration.addAllowedHeader("*");
        // 允许携带凭证（如cookies）
        configuration.setAllowCredentials(true);
        // 增加预检请求的缓存时间（秒）
        configuration.setMaxAge(86400L); // 24小时
        // 暴露更多的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Disposition", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept", 
            "Origin", 
            "Access-Control-Allow-Origin",
            "biz_side"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用CORS配置
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}