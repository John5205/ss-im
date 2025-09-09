package chat.config;

import xin.harrison.im.config.JwtConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * JWT 配置初始化器
 *
 * @author Harrison
 * @version 1.0.0
 */
@Component
@Log4j2
public class JwtConfigInitializer implements ApplicationRunner {
    
    @Autowired
    private JwtProperties jwtProperties;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 从 Spring 配置中设置 JWT 配置
        if (jwtProperties.getSecretKey() != null && !jwtProperties.getSecretKey().trim().isEmpty()) {
            JwtConfig.setSecretKey(jwtProperties.getSecretKey());
            log.info("JWT secret key loaded from application configuration");
        } else {
            log.warn("JWT secret key not configured, using auto-generated key");
        }
        
        JwtConfig.setExpirationTime(jwtProperties.getExpirationTime());
        log.info("JWT configuration initialized - expiration time: {} ms", jwtProperties.getExpirationTime());
    }
}
