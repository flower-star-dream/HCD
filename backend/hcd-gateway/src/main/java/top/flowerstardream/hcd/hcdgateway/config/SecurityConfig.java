package top.flowerstardream.hcd.hcdgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // 1. 关闭 CSRF
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // 2. 全部放行——JWT 校验由 Gateway 的 HcdGlobalFilter 做
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                // 3. 保留跨域
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 4. 禁用 Security 自带的登录/登出/缓存过滤器
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .requestCache(ServerHttpSecurity.RequestCacheSpec::disable)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.addAllowedOriginPattern("*");
        cfg.addAllowedMethod("*");
        cfg.addAllowedHeader("*");
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(86400L);
        cfg.setExposedHeaders(List.of(
                "Authorization", "Content-Disposition", "biz_side"));
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}