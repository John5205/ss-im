package chat.service;

import xin.harrison.im.bean.Message;
import xin.harrison.im.utils.JsonUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息路由服务
 * 负责根据消息类型路由到相应的处理器
 *
 * @author Harrison
 * @version 1.0.0
 */
@Service
@Log4j2
public class MessageRoutingService {
    
    @Autowired
    private UserConnectionService userConnectionService;
    
    /**
     * 路由消息
     */
    public void routeMessage(ChannelHandlerContext ctx, String messageJson) {
        try {
            Message message = JsonUtils.fromJson(messageJson, Message.class);
            if (message == null || message.getType() == null) {
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Invalid message format\"}"));
                return;
            }
            
            String messageType = message.getType();
            String content = message.getContent();
            String senderId = message.getSenderId();
            String targetId = message.getTargetId();
            
            log.info("Routing message type: {} from: {} to: {}", messageType, senderId, targetId);
            
            switch (messageType) {
                case "login":
                    handleLogin(ctx, content);
                    break;
                case "logout":
                    handleLogout(ctx, content);
                    break;
                case "private_message":
                    handlePrivateMessage(ctx, targetId, content, senderId);
                    break;
                case "broadcast":
                    handleBroadcast(ctx, content, senderId);
                    break;
                case "get_online_users":
                    handleGetOnlineUsers(ctx);
                    break;
                case "ping":
                    handlePing(ctx, content);
                    break;
                default:
                    log.warn("Unknown message type: {}", messageType);
                    ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Unknown message type: " + messageType + "\"}"));
            }
            
        } catch (Exception e) {
            log.error("Error routing message", e);
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Message routing failed\"}"));
        }
    }
    
    private void handleLogin(ChannelHandlerContext ctx, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"User ID cannot be empty\"}"));
            return;
        }
        
        userConnectionService.userOnline(userId.trim(), ctx.channel());
    }
    
    private void handleLogout(ChannelHandlerContext ctx, String content) {
        String userId = userConnectionService.getUserId(ctx.channel());
        if (userId != null) {
            userConnectionService.userOffline(userId);
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"logout_success\",\"message\":\"Logged out successfully\"}"));
        }
    }
    
    private void handlePrivateMessage(ChannelHandlerContext ctx, String targetId, String content, String senderId) {
        if (targetId == null || targetId.trim().isEmpty()) {
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Target user ID cannot be empty\"}"));
            return;
        }
        
        if (senderId == null) {
            senderId = userConnectionService.getUserId(ctx.channel());
            if (senderId == null) {
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"User not logged in\"}"));
                return;
            }
        }
        
        boolean success = userConnectionService.sendPrivateMessage(senderId, targetId, content);
        
        if (success) {
            String confirmMessage = String.format(
                "{\"type\":\"message_sent\",\"toUserId\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                targetId, content, System.currentTimeMillis()
            );
            ctx.writeAndFlush(new TextWebSocketFrame(confirmMessage));
        } else {
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"message_failed\",\"toUserId\":\"" + targetId + "\",\"message\":\"User is offline\"}"));
        }
    }
    
    private void handleBroadcast(ChannelHandlerContext ctx, String content, String senderId) {
        if (senderId == null) {
            senderId = userConnectionService.getUserId(ctx.channel());
            if (senderId == null) {
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"User not logged in\"}"));
                return;
            }
        }
        
        userConnectionService.broadcastMessage(senderId, content);
        ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"broadcast_sent\",\"message\":\"Broadcast sent successfully\"}"));
    }
    
    private void handleGetOnlineUsers(ChannelHandlerContext ctx) {
        String[] onlineUsers = userConnectionService.getOnlineUserIds();
        String jsonMessage = String.format(
            "{\"type\":\"online_users\",\"users\":[%s],\"count\":%d}",
            String.join(",", java.util.Arrays.stream(onlineUsers)
                .map(userId -> "\"" + userId + "\"")
                .toArray(String[]::new)),
            onlineUsers.length
        );
        ctx.writeAndFlush(new TextWebSocketFrame(jsonMessage));
    }
    
    private void handlePing(ChannelHandlerContext ctx, String content) {
        ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"pong\",\"timestamp\":" + System.currentTimeMillis() + "}"));
    }
}
