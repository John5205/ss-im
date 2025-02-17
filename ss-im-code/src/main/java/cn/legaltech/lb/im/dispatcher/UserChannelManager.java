package cn.legaltech.lb.im.dispatcher;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理所有客户端连接
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/12/2
 */
public class UserChannelManager {
    private static final Map<String, Channel> clientSessions = new ConcurrentHashMap<>();

    /**
     * 添加客户端连接
     *
     * @param sessionId 客户端唯一标识
     * @param channel   通道
     */
    public static void addSession(String sessionId, Channel channel) {
        clientSessions.put(sessionId, channel);
    }

    /**
     * 删除客户端连接
     *
     * @param sessionId 客户端唯一标识
     */
    public static void removeSession(String sessionId) {
        clientSessions.remove(sessionId);
    }

    /**
     * 获取客户端连接
     *
     * @param sessionId 客户端唯一标识
     * @return 返回通道
     */
    public static Channel getSession(String sessionId) {
        return clientSessions.get(sessionId);
    }
}