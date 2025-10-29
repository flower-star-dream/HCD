package top.flowerstardream.hcd.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置类
 *
 * @Author: 花海
 * @Date: 2025/10/29
 * @Description: SpringDoc OpenAPI 配置
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置OpenAPI信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HCD用户服务API")
                        .version("1.0.0")
                        .description("HCD用户服务RESTful API文档")
                        .contact(new Contact()
                                .name("花海")
                                .email("flowerstardream@top")
                                .url("https://flowerstardream.top"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addServersItem(new Server().url("/").description("本地服务器"));
    }

    /**
     * 用户管理API分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户管理")
                .pathsToMatch("/api/v1/user/**")
                .packagesToScan("top.flowerstardream.hcd.user.api.v1.user")
                .build();
    }

    /**
     * 管理员API分组
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("管理员管理")
                .pathsToMatch("/api/v1/user/admin/**")
                .packagesToScan("top.flowerstardream.hcd.user.api.v1.admin")
                .build();
    }

    /**
     * 默认API分组
     */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .packagesToScan("top.flowerstardream.hcd.user.api.v1")
                .build();
    }
}