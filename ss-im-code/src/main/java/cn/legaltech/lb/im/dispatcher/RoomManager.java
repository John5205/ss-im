package cn.legaltech.lb.im.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 房间管理
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/12/2
 */
public class RoomManager {

    /**
     * 房间用户列表
     */
    private static final ConcurrentHashMap<String, List<String>> roomUsers = new ConcurrentHashMap<>();

    /**
     * 用户加入房间
     *
     * @param roomId 房间ID
     * @param userId 用户ID
     */
    public static void joinRoom(String roomId, String userId) {
        roomUsers.computeIfAbsent(roomId, key -> new CopyOnWriteArrayList<>()).add(userId);
    }

    /**
     * 用户离开房间
     *
     * @param roomId 房间ID
     * @param userId 用户ID
     */
    public static void leaveRoom(String roomId, String userId) {
        List<String> users = roomUsers.get(roomId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                roomUsers.remove(roomId);
            }
        }
    }

    /**
     * 获取房间用户列表
     *
     * @param roomId 房间ID
     * @return 返回房间用户列表
     */
    public static List<String> getRoomUsers(String roomId) {
        return roomUsers.getOrDefault(roomId, new ArrayList<String>());
    }

    /**
     * 广播消息到房间内所有用户
     *
     * @param roomId        房间ID
     * @param message       消息
     * @param senderId      发送者ID
     * @param senderVisible 是否发送者可见
     */
    public static void broadcastToRoom(String roomId, String message, String senderId, boolean senderVisible) {
        List<String> users = getRoomUsers(roomId);
        for (String userId : users) {
            if (!senderVisible && userId.equals(senderId)) {
                continue;
            }
            MessageProcessorManager.handleChatMessage(userId, message);
        }
    }
}