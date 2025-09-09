package chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 *
 * @author Harrison
 * @version 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    private String secretKey;
    private long expirationTime = 86400000L; // 默认1天
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    
    public long getExpirationTime() {
        return expirationTime;
    }
    
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
