package top.flowerstardream.hcd.tools.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.flowerstardream.hcd.tools.utils.TtlContextHolder;

/**
 * @Author: 花海
 * @Date: 2025/11/07/15:21
 * @Description: 默认feign配置
 */
@Configuration
@Slf4j
public class DefaultFeignConfig {
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return requestTemplate -> {
            Long tenantId = TtlContextHolder.get().getTenantId();
            if (tenantId != null) {
                requestTemplate.header("user-info", tenantId.toString());
            }
        };
    }
}
