package chat.handler;

import chat.service.UserConnectionService;
import xin.harrison.im.annotation.MessageMapping;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 私聊消息处理器
 *
 * @author Harrison
 * @version 1.0.0
 */
@Component
@Log4j2
public class PrivateChatHandler {
    
    @Autowired
    private UserConnectionService userConnectionService;
    
    @MessageMapping(value = "private_message", authRequired = false)
    public void handlePrivateMessage(ChannelHandlerContext ctx, String message) {
        log.info("Processing private message: {}", message);
        
        try {
            // 解析消息格式: toUserId:content
            String[] parts = message.split(":", 2);
            if (parts.length != 2) {
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Invalid message format. Expected: toUserId:content\"}"));
                return;
            }
            
            String toUserId = parts[0];
            String content = parts[1];
            String fromUserId = userConnectionService.getUserId(ctx.channel());
            
            if (fromUserId == null) {
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"User not logged in\"}"));
                return;
            }
            
            // 发送私聊消息
            boolean success = userConnectionService.sendPrivateMessage(fromUserId, toUserId, content);
            
            if (success) {
                // 发送成功确认给发送者
                String confirmMessage = String.format(
                    "{\"type\":\"message_sent\",\"toUserId\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                    toUserId, content, System.currentTimeMillis()
                );
                ctx.writeAndFlush(new TextWebSocketFrame(confirmMessage));
            } else {
                // 发送失败通知
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"message_failed\",\"toUserId\":\"" + toUserId + "\",\"message\":\"User is offline\"}"));
            }
            
        } catch (Exception e) {
            log.error("Error processing private message", e);
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Internal server error\"}"));
        }
    }
    
    @MessageMapping(value = "login", authRequired = false)
    public void handleLogin(ChannelHandlerContext ctx, String message) {
        log.info("Processing login message: {}", message);
        
        try {
            // 简单的登录逻辑，实际项目中应该验证用户名密码
            String userId = message.trim();
            if (userId.isEmpty()) {
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"User ID cannot be empty\"}"));
                return;
            }
            
            // 用户上线
            userConnectionService.userOnline(userId, ctx.channel());
            
        } catch (Exception e) {
            log.error("Error processing login", e);
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Login failed\"}"));
        }
    }
    
    @MessageMapping(value = "logout", authRequired = false)
    public void handleLogout(ChannelHandlerContext ctx, String message) {
        log.info("Processing logout message: {}", message);
        
        try {
            String userId = userConnectionService.getUserId(ctx.channel());
            if (userId != null) {
                userConnectionService.userOffline(userId);
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"logout_success\",\"message\":\"Logged out successfully\"}"));
            }
        } catch (Exception e) {
            log.error("Error processing logout", e);
        }
    }
    
    @MessageMapping(value = "get_online_users", authRequired = false)
    public void handleGetOnlineUsers(ChannelHandlerContext ctx, String message) {
        log.info("Processing get online users request");
        
        try {
            String[] onlineUsers = userConnectionService.getOnlineUserIds();
            String jsonMessage = String.format(
                "{\"type\":\"online_users\",\"users\":[%s],\"count\":%d}",
                String.join(",", java.util.Arrays.stream(onlineUsers)
                    .map(userId -> "\"" + userId + "\"")
                    .toArray(String[]::new)),
                onlineUsers.length
            );
            ctx.writeAndFlush(new TextWebSocketFrame(jsonMessage));
        } catch (Exception e) {
            log.error("Error getting online users", e);
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Failed to get online users\"}"));
        }
    }
    
    @MessageMapping(value = "broadcast", authRequired = false)
    public void handleBroadcast(ChannelHandlerContext ctx, String message) {
        log.info("Processing broadcast message: {}", message);
        
        try {
            String fromUserId = userConnectionService.getUserId(ctx.channel());
            if (fromUserId == null) {
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"User not logged in\"}"));
                return;
            }
            
            userConnectionService.broadcastMessage(fromUserId, message);
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"broadcast_sent\",\"message\":\"Broadcast sent successfully\"}"));
            
        } catch (Exception e) {
            log.error("Error processing broadcast", e);
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Broadcast failed\"}"));
        }
    }
}
