package cn.kmdckj.base.config;

import cn.kmdckj.base.util.TokenUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 * 从application.yml读取JWT相关配置并初始化TokenUtil
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT密钥
     */
    private String secretKey;

    /**
     * Token过期时间（毫秒）
     */
    private Long expiration;

    /**
     * 初始化TokenUtil配置
     */
    @PostConstruct
    public void init() {
        if (secretKey != null && !secretKey.isEmpty()) {
            TokenUtil.setSecretKey(secretKey);
        }
        if (expiration != null && expiration > 0) {
            TokenUtil.setExpirationTime(expiration);
        }
    }
}
