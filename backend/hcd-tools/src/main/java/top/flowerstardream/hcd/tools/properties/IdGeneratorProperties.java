package top.flowerstardream.hcd.tools.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: 花海
 * @Date: 2025/10/31/18:47
 * @Description: ID 生成器属性
 */
@Component
@ConfigurationProperties(prefix = "hcd.id-generator")
@Data
public class IdGeneratorProperties {
    private long twepoch = 1672531200000L; // 时间戳
    private long workerId = 0L; // 机器号
    private long workerIdBits = 10L; // 机器号位数
    private long sequenceBits = 12L; // 序列号位数
}
