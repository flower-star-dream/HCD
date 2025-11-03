package top.flowerstardream.hcd.tools.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import top.flowerstardream.hcd.tools.properties.ThreadPoolProperties;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 花海
 * @date 2025-10-14
 * @description 线程池配置类
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    @Resource
    private ThreadPoolProperties threadPoolProperties;


    /**
     * 核心业务调度器 - 用于CPU密集型任务
     */
    @Bean("businessScheduler")
    public Scheduler businessScheduler() {
        ThreadPoolTaskExecutor executor = businessExecutor();
        log.info("核心业务调度器初始化完成，线程数: core={}, max={}", 
                 executor.getCorePoolSize(), executor.getMaxPoolSize());
        return Schedulers.fromExecutor(executor);
    }

    /**
     * 通用任务调度器 - 用于IO密集型任务
     */
    @Bean("commonScheduler")
    public Scheduler commonScheduler() {
        ThreadPoolTaskExecutor executor = commonExecutor();
        log.info("通用任务调度器初始化完成，线程数: core={}, max={}", 
                 executor.getCorePoolSize(), executor.getMaxPoolSize());
        return Schedulers.fromExecutor(executor);
    }

    /**
     * 异步消息处理调度器
     */
    @Bean("messageScheduler")
    public Scheduler messageScheduler() {
        ThreadPoolTaskExecutor executor = messageExecutor();
        log.info("异步消息处理调度器初始化完成，线程数: core={}, max={}", 
                 executor.getCorePoolSize(), executor.getMaxPoolSize());
        return Schedulers.fromExecutor(executor);
    }

    /**
     * 核心业务线程池
     */
    @Bean("businessExecutor")
    public ThreadPoolTaskExecutor businessExecutor() {
        ThreadPoolProperties.ThreadPoolConfig businessConfig = threadPoolProperties.getBusiness();
        ThreadPoolTaskExecutor executor = initThreadPoolTaskExecutor(businessConfig);
        executor.initialize();
        log.info("核心业务线程池初始化完成，配置: {}", businessConfig);
        return executor;
    }

    /**
     * 通用任务线程池
     */
    @Bean("commonExecutor")
    public ThreadPoolTaskExecutor commonExecutor() {
        ThreadPoolProperties.ThreadPoolConfig commonConfig = threadPoolProperties.getCommon();
        ThreadPoolTaskExecutor executor = initThreadPoolTaskExecutor(commonConfig);
        executor.initialize();
        log.info("通用任务线程池初始化完成，配置: {}", commonConfig);
        return executor;
    }

    /**
     * 异步消息处理线程池
     */
    @Bean("messageExecutor")
    public ThreadPoolTaskExecutor messageExecutor() {
        ThreadPoolProperties.ThreadPoolConfig messageConfig = threadPoolProperties.getMessage();
        ThreadPoolTaskExecutor executor = initThreadPoolTaskExecutor(messageConfig);
        executor.initialize();
        log.info("异步消息处理线程池初始化完成，配置: {}", messageConfig);
        return executor;
    }

    private ThreadPoolTaskExecutor initThreadPoolTaskExecutor(ThreadPoolProperties.ThreadPoolConfig config) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        executor.setThreadNamePrefix(config.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(createRejectedExecutionHandler(config.getRejectedExecutionHandler()));
        executor.setWaitForTasksToCompleteOnShutdown(config.isWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(config.getAwaitTerminationSeconds());
        return executor;
    }

    private RejectedExecutionHandler createRejectedExecutionHandler(String policyName) {
        RejectedExecutionHandler handler = switch (policyName) {
            case "CallerRunsPolicy" -> new ThreadPoolExecutor.CallerRunsPolicy();
            case "AbortPolicy" -> new ThreadPoolExecutor.AbortPolicy();
            case "DiscardPolicy" -> new ThreadPoolExecutor.DiscardPolicy();
            case "DiscardOldestPolicy" -> new ThreadPoolExecutor.DiscardOldestPolicy();
            default -> {
                log.warn("未知的拒绝策略: {}，使用默认策略 CallerRunsPolicy", policyName);
                yield new ThreadPoolExecutor.CallerRunsPolicy();
            }
        };
        log.debug("创建拒绝策略: {}", policyName);
        return handler;
    }

}