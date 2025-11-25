package top.flowerstardream.hcd.trainSeat;

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
 * 班次服务启动类
 *
 * @author flowerstardream
 * @date 2025-01-25
 */
@SpringBootApplication(scanBasePackages = {
        "top.flowerstardream.hcd.trainSeat",
        "top.flowerstardream.hcd.tools",
        "top.flowerstardream.hcd.base"
})
@EnableDiscoveryClient //开启服务注册与发现
@EnableFeignClients
@EnableTransactionManagement //开启注解方式的事务管理
@EnableScheduling //开启注解方式的定时任务
@EnableCaching //开启注解方式的缓存管理
@Slf4j
@MapperScan("top.flowerstardream.hcd.trainSeat.biz.mapper")
public class TrainSeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainSeatApplication.class, args);
        log.info("trainSeat server started");
    }
}