package top.flowerstardream.hcd.tools.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.flowerstardream.hcd.tools.interfaces.TtlContextInterceptor;

/**
 * @Author: 花海
 * @Date: 2025/11/07/13:33
 * @Description: web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private TtlContextInterceptor ttlContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ttlContextInterceptor)
                .addPathPatterns("/api/**");
    }
}