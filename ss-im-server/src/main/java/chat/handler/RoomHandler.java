package chat.handler;

import xin.harrison.im.annotation.MessageMapping;
import xin.harrison.im.dispatcher.RoomManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;

/**
 * 房间管理处理器
 *
 * @author Harrison
 * @version 1.0.0
 */
@Log4j2
public class RoomHandler {

    @MessageMapping(value = "join_room", authRequired = false)
    public static void handleJoinRoom(ChannelHandlerContext ctx, String message) {
        log.info("Processing join room message: {}", message);
        
        try {
            // 解析消息格式: roomId:userId
            String[] parts = message.split(":");
            if (parts.length != 2) {
                ctx.writeAndFlush(new TextWebSocketFrame("join_room_error:Invalid format"));
                return;
            }
            
            String roomId = parts[0];
            String userId = parts[1];
            
            RoomManager.joinRoom(roomId, userId);
            ctx.writeAndFlush(new TextWebSocketFrame("join_room_success:" + roomId));
            log.info("User {} joined room {}", userId, roomId);
            
        } catch (Exception e) {
            log.error("Join room error", e);
            ctx.writeAndFlush(new TextWebSocketFrame("join_room_error:Internal error"));
        }
    }

    @MessageMapping(value = "leave_room", authRequired = false)
    public static void handleLeaveRoom(ChannelHandlerContext ctx, String message) {
        log.info("Processing leave room message: {}", message);
        
        try {
            // 解析消息格式: roomId:userId
            String[] parts = message.split(":");
            if (parts.length != 2) {
                ctx.writeAndFlush(new TextWebSocketFrame("leave_room_error:Invalid format"));
                return;
            }
            
            String roomId = parts[0];
            String userId = parts[1];
            
            RoomManager.leaveRoom(roomId, userId);
            ctx.writeAndFlush(new TextWebSocketFrame("leave_room_success:" + roomId));
            log.info("User {} left room {}", userId, roomId);
            
        } catch (Exception e) {
            log.error("Leave room error", e);
            ctx.writeAndFlush(new TextWebSocketFrame("leave_room_error:Internal error"));
        }
    }

    @MessageMapping(value = "broadcast_room", authRequired = false)
    public static void handleBroadcastRoom(ChannelHandlerContext ctx, String message) {
        log.info("Processing broadcast room message: {}", message);
        
        try {
            // 解析消息格式: roomId:content:senderId
            String[] parts = message.split(":", 3);
            if (parts.length != 3) {
                ctx.writeAndFlush(new TextWebSocketFrame("broadcast_room_error:Invalid format"));
                return;
            }
            
            String roomId = parts[0];
            String content = parts[1];
            String senderId = parts[2];
            
            RoomManager.broadcastToRoom(roomId, content, senderId, false);
            ctx.writeAndFlush(new TextWebSocketFrame("broadcast_room_success:" + roomId));
            log.info("Broadcast message to room {} from user {}", roomId, senderId);
            
        } catch (Exception e) {
            log.error("Broadcast room error", e);
            ctx.writeAndFlush(new TextWebSocketFrame("broadcast_room_error:Internal error"));
        }
    }
}
