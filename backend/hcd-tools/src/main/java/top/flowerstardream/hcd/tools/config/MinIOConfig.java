package top.flowerstardream.hcd.tools.config;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.flowerstardream.hcd.tools.properties.MinioProperties;
import top.flowerstardream.hcd.tools.service.FileStorageService;


@Data
@Configuration
@EnableConfigurationProperties({MinioProperties.class})
//当引入FileStorageService接口时
@ConditionalOnClass(FileStorageService.class)
public class MinIOConfig {

    @Resource
    private MinioProperties minioProperties;

    @Bean
    public MinioClient buildMinioClient() {
        return MinioClient
                .builder()
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .endpoint(minioProperties.getEndpoint())
                .build();
    }
}