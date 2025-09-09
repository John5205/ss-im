package chat;

import chat.config.NettyConfig;
import xin.harrison.im.server.NettyApplication;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Harrison
 * @version 1.0.0
 */
@Log4j2
@SpringBootApplication(scanBasePackages = {"chat.**"})
public class ChatServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
        try {
            NettyApplication.startServer(NettyConfig.class);
        } catch (Exception e) {
            log.error("启动 Netty 服务失败", e);
        }
    }
}
