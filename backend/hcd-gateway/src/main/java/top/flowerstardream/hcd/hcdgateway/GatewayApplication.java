package top.flowerstardream.hcd.hcdgateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import top.flowerstardream.hcd.tools.config.MyBatisPlusConfig;

@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        ReactiveSecurityAutoConfiguration.class
})
@EnableDiscoveryClient
@Slf4j
@ComponentScan(
    basePackages = {
        "top.flowerstardream.hcd.tools.config",
        "top.flowerstardream.hcd.tools.properties",
        "top.flowerstardream.hcd.tools.result",
        "top.flowerstardream.hcd.tools.utils",
        "top.flowerstardream.hcd.tools.exception",
        "top.flowerstardream.hcd.tools.constant"
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MyBatisPlusConfig.class)
    }
)
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
		log.info("gateway server started");
	}

}
