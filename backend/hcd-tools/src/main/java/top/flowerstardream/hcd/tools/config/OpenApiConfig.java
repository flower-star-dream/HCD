package top.flowerstardream.hcd.tools.config;

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
 * @Date: 2025/10/30
 * @Description: SpringDoc OpenAPI 公共配置
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
                        .title("火车订票系统API")
                        .version("1.0.0")
                        .description("火车订票系统文档")
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
     * 默认API分组 - 包含所有接口
     */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .build();
    }

    /**
     * 用户管理API分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户管理")
                .pathsToMatch("/api/v1/*/user/**")
                .packagesToScan("top.flowerstardream.hcd.user.api.v1")
                .build();
    }

    /**
     * 列车座位管理API分组
     */
    @Bean
    public GroupedOpenApi trainSeatApi() {
        return GroupedOpenApi.builder()
                .group("列车座位管理")
                .pathsToMatch("/api/v1/*/trainSeat/**")
                .packagesToScan("top.flowerstardream.hcd.trainSeat.api.v1")
                .build();
    }
    /**
     * 订单管理API分组
     */
    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("订单管理")
                .pathsToMatch("/api/v1/*/order/**")
                .packagesToScan("top.flowerstardream.hcd.order.api.v1")
                .build();
    }
    /**
     * 车票管理API分组
     */
    @Bean
    public GroupedOpenApi ticketApi() {
        return GroupedOpenApi.builder()
                .group("车票管理")
                .pathsToMatch("/api/v1/*/ticket/**")
                .packagesToScan("top.flowerstardream.hcd.ticket.api.v1")
                .build();
    }
    /**
     * 系统配置API分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("系统配置管理")
                .pathsToMatch("/api/v1/system/**")
                .packagesToScan("top.flowerstardream.hcd.system.api.v1")
                .build();
    }
}