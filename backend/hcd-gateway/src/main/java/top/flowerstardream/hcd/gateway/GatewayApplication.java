package top.flowerstardream.hcd.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
@ComponentScan(
    basePackages = {
        "top.flowerstardream.hcd.tools.properties",
        "top.flowerstardream.hcd.tools.result",
        "top.flowerstardream.hcd.tools.utils",
        "top.flowerstardream.hcd.tools.exception",
        "top.flowerstardream.hcd.tools.constant"
    }
)
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
		log.info("gateway server started");
	}

}
