package chat.service;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户连接管理服务
 *
 * @author Harrison
 * @version 1.0.0
 */
@Service
@Log4j2
public class UserConnectionService {
    
    /**
     * 用户ID -> Channel 映射
     */
    private final Map<String, Channel> userChannels = new ConcurrentHashMap<>();
    
    /**
     * Channel -> 用户ID 映射
     */
    private final Map<Channel, String> channelUsers = new ConcurrentHashMap<>();
    
    /**
     * 用户上线
     */
    public void userOnline(String userId, Channel channel) {
        // 如果用户已在线，先下线旧连接
        if (userChannels.containsKey(userId)) {
            Channel oldChannel = userChannels.get(userId);
            if (oldChannel != null && oldChannel.isActive()) {
                oldChannel.writeAndFlush(new TextWebSocketFrame("{\"type\":\"force_logout\",\"message\":\"账号在其他地方登录\"}"));
                oldChannel.close();
            }
        }
        
        userChannels.put(userId, channel);
        channelUsers.put(channel, userId);
        
        log.info("User {} is now online, total online users: {}", userId, userChannels.size());
        
        // 通知用户上线成功
        channel.writeAndFlush(new TextWebSocketFrame("{\"type\":\"login_success\",\"userId\":\"" + userId + "\"}"));
    }
    
    /**
     * 用户下线
     */
    public void userOffline(Channel channel) {
        String userId = channelUsers.remove(channel);
        if (userId != null) {
            userChannels.remove(userId);
            log.info("User {} is now offline, total online users: {}", userId, userChannels.size());
        }
    }
    
    /**
     * 用户下线
     */
    public void userOffline(String userId) {
        Channel channel = userChannels.remove(userId);
        if (channel != null) {
            channelUsers.remove(channel);
            log.info("User {} is now offline, total online users: {}", userId, userChannels.size());
        }
    }
    
    /**
     * 获取用户连接
     */
    public Channel getUserChannel(String userId) {
        return userChannels.get(userId);
    }
    
    /**
     * 获取用户ID
     */
    public String getUserId(Channel channel) {
        return channelUsers.get(channel);
    }
    
    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(String userId) {
        Channel channel = userChannels.get(userId);
        return channel != null && channel.isActive();
    }
    
    /**
     * 发送私聊消息
     */
    public boolean sendPrivateMessage(String fromUserId, String toUserId, String message) {
        Channel targetChannel = userChannels.get(toUserId);
        if (targetChannel == null || !targetChannel.isActive()) {
            log.warn("User {} is not online, cannot send message to {}", toUserId, fromUserId);
            return false;
        }
        
        String jsonMessage = String.format(
            "{\"type\":\"private_message\",\"fromUserId\":\"%s\",\"toUserId\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
            fromUserId, toUserId, message, System.currentTimeMillis()
        );
        
        targetChannel.writeAndFlush(new TextWebSocketFrame(jsonMessage));
        log.info("Private message sent from {} to {}: {}", fromUserId, toUserId, message);
        return true;
    }
    
    /**
     * 发送系统消息
     */
    public void sendSystemMessage(String userId, String message) {
        Channel channel = userChannels.get(userId);
        if (channel != null && channel.isActive()) {
            String jsonMessage = String.format(
                "{\"type\":\"system_message\",\"content\":\"%s\",\"timestamp\":%d}",
                message, System.currentTimeMillis()
            );
            channel.writeAndFlush(new TextWebSocketFrame(jsonMessage));
        }
    }
    
    /**
     * 广播消息给所有在线用户
     */
    public void broadcastMessage(String fromUserId, String message) {
        String jsonMessage = String.format(
            "{\"type\":\"broadcast_message\",\"fromUserId\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
            fromUserId, message, System.currentTimeMillis()
        );
        
        userChannels.values().forEach(channel -> {
            if (channel.isActive()) {
                channel.writeAndFlush(new TextWebSocketFrame(jsonMessage));
            }
        });
        
        log.info("Broadcast message from {}: {}", fromUserId, message);
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return userChannels.size();
    }
    
    /**
     * 获取所有在线用户ID
     */
    public String[] getOnlineUserIds() {
        return userChannels.keySet().toArray(new String[0]);
    }
}
