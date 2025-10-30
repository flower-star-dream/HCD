package top.flowerstardream.hcd.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Knife4j配置类
 *
 * @Author: 花海
 * @Date: 2025/10/29
 * @Description: Knife4j配置
 */
@Configuration
@EnableKnife4j
public class Knife4jConfig {

    /**
     * 配置swagger-config端点，解决Knife4j 404问题
     */
    @Bean
    public RouterFunction<ServerResponse> swaggerConfigEndpoint() {
        return RouterFunctions.route()
                .GET("/v3/api-docs/swagger-config",
                    request -> {
                        Map<String, Object> config = new HashMap<>();
                        config.put("configUrl", "/v3/api-docs/swagger-config");
                        config.put("urls", new Object[]{
                            Map.of("url", "/v3/api-docs", "name", "HCD用户服务API")
                        });
                        config.put("validatorUrl", "");
                        config.put("oauth2RedirectUrl", "http://localhost:8080/webjars/swagger-ui/oauth2-redirect.html");

                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(config), Map.class);
                    })
                .build();
    }

    /**
     * 配置基础API文档端点
     */
    @Bean
    public RouterFunction<ServerResponse> apiDocsEndpoint() {
        return RouterFunctions.route()
                .GET("/v3/api-docs",
                    request -> {
                        Map<String, Object> apiDoc = new HashMap<>();
                        apiDoc.put("openapi", "3.0.1");
                        apiDoc.put("info", Map.of(
                            "title", "HCD用户服务API",
                            "description", "HCD用户服务RESTful API文档",
                            "version", "1.0.0"
                        ));
                        apiDoc.put("servers", new Object[]{
                            Map.of("url", "/", "description", "本地服务器")
                        });
                        apiDoc.put("paths", new HashMap<>());

                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(apiDoc), Map.class);
                    })
                .build();
    }
}