package xin.harrison.im.config;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * JWT 配置类
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/12/2
 */
public class JwtConfig {
    
    private static final String DEFAULT_SECRET_KEY = generateSecureKey();
    /**
     * -- GETTER --
     *  获取密钥
     */
    @Getter
    private static String secretKey = DEFAULT_SECRET_KEY;
    /**
     * -- GETTER --
     *  获取过期时间
     */
    @Getter
    private static long expirationTime = 86400000L; // 1天，单位毫秒
    
    /**
     * 生成安全的密钥
     */
    private static String generateSecureKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256位密钥
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }
    
    /**
     * 设置密钥
     */
    public static void setSecretKey(String secretKey) {
        if (secretKey != null && !secretKey.trim().isEmpty()) {
            JwtConfig.secretKey = secretKey;
        }
    }

    /**
     * 获取密钥字节数组
     */
    public static byte[] getSecretKeyBytes() {
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }
    
    /**
     * 设置过期时间
     */
    public static void setExpirationTime(long expirationTime) {
        JwtConfig.expirationTime = expirationTime;
    }

    /**
     * 从环境变量或系统属性获取密钥
     */
    public static void loadFromEnvironment() {
        // 优先从环境变量获取
        String envKey = System.getenv("JWT_SECRET_KEY");
        if (envKey != null && !envKey.trim().isEmpty()) {
            setSecretKey(envKey);
            return;
        }
        
        // 其次从系统属性获取
        String sysKey = System.getProperty("jwt.secret.key");
        if (sysKey != null && !sysKey.trim().isEmpty()) {
            setSecretKey(sysKey);
            return;
        }
        
        // 最后使用默认生成的密钥
        System.out.println("Warning: Using auto-generated JWT secret key. " +
                "For production, please set JWT_SECRET_KEY environment variable or jwt.secret.key system property.");
    }
}
