package top.flowerstardream.hcd.tools.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.flowerstardream.hcd.tools.interfaces.TtlContextInterceptor;
import top.flowerstardream.hcd.tools.json.JacksonObjectMapper;

import java.util.List;

/**
 * @Author: 花海
 * @Date: 2025/11/07/13:33
 * @Description: web配置类
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private TtlContextInterceptor ttlContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ttlContextInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 对象转换器，将java对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());
        // 将自己的转换器加入
        converters.add(0,converter);
    }
}