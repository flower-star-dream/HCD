package top.flowerstardream.hcd.ticket;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: 花海
 * @Date: 2025/11/10/01:57
 * @Description: 票务服务启动入口
 */
@SpringBootApplication(scanBasePackages = {
        "top.flowerstardream.hcd.ticket",
        "top.flowerstardream.hcd.tools",
        "top.flowerstardream.hcd.base"
})
@EnableDiscoveryClient //开启服务注册与发现
@EnableFeignClients
@EnableTransactionManagement //开启注解方式的事务管理
@EnableScheduling //开启注解方式的定时任务
@EnableCaching //开启注解方式的缓存管理
@Slf4j
@MapperScan("top.flowerstardream.hcd.ticket.biz.mapper")
public class TicketApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketApplication.class, args);
        log.info("ticket server started");
    }
}
