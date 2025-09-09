package chat.handler;

import xin.harrison.im.annotation.MessageMapping;
import xin.harrison.im.utils.JwtUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;

/**
 * 认证处理器
 *
 * @author Harrison
 * @version 1.0.0
 */
@Log4j2
public class AuthHandler {

    @MessageMapping(value = "auth", authRequired = false)
    public static void handleAuthMessage(ChannelHandlerContext ctx, String message) {
        log.info("Processing auth message: {}", message);
        
        try {
            // 简单的认证逻辑，实际项目中应该从数据库验证
            if ("valid_token".equals(message)) {
                // 生成JWT token
                String token = JwtUtil.generateToken("user123", "harrison");
                ctx.writeAndFlush(new TextWebSocketFrame("auth_success:" + token));
                log.info("Authentication successful for user: harrison");
            } else {
                ctx.writeAndFlush(new TextWebSocketFrame("auth_failed:Invalid token"));
                log.warn("Authentication failed for token: {}", message);
            }
        } catch (Exception e) {
            log.error("Authentication error", e);
            ctx.writeAndFlush(new TextWebSocketFrame("auth_error:Internal error"));
        }
    }

    @MessageMapping(value = "logout", authRequired = true)
    public static void handleLogoutMessage(ChannelHandlerContext ctx, String message) {
        log.info("Processing logout message: {}", message);
        ctx.writeAndFlush(new TextWebSocketFrame("logout_success"));
        log.info("User logged out successfully");
    }
}
