package top.flowerstardream.hcd.tools.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hcd.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端管理员生成jwt令牌相关配置
     */
    private String adminSecretKey; // 密钥
    private long adminTtl; // 过期时间
    private String adminTokenName; // 令牌名称
    private long adminRefreshTime; // 刷新时间

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey; // 密钥
    private long userTtl; // 过期时间
    private String userTokenName; // 令牌名称
    private long userRefreshTime; // 刷新时间

}
