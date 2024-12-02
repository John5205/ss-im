package cn.legaltech.lb.im.dispatcher;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息处理器管理器
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/12/2
 */
public class MessageProcessorManager {

    private static final Logger log = LogManager.getLogger(MessageProcessorManager.class);

    /**
     * 消息处理器的集合，key 为消息类型，value 为对应的处理器
     */
    private static final Map<String, MessageProcessor> MESSAGE_PROCESSORS = new HashMap<>();

    /**
     * 静态代码块，用于初始化消息处理器
     */
    static {
        MESSAGE_PROCESSORS.put("text", MessageProcessorManager::handleChatMessage);
        MESSAGE_PROCESSORS.put("file", MessageProcessorManager::handleFileMessage);
        MESSAGE_PROCESSORS.put("audio", MessageProcessorManager::handleAudioMessage);
        MESSAGE_PROCESSORS.put("video", MessageProcessorManager::handleVideoMessage);
    }

    /**
     * 获取所有的消息处理器
     *
     * @return 返回所有的消息处理器
     */
    public static Map<String, MessageProcessor> getMessageProcessors() {
        return MESSAGE_PROCESSORS;
    }

    /**
     * 处理聊天消息
     *
     * @param content  消息内容
     * @param targetId 目标用户 ID
     */
    public static void handleChatMessage(String content, String targetId) {
        // 处理聊天消息的逻辑
        log.info("Processing chat message: {}", content);
        Channel session = UserChannelManager.getSession(targetId);
        if (session != null) {
            session.writeAndFlush(new TextWebSocketFrame(content));
        }
    }

    /**
     * 处理文件消息
     *
     * @param content  消息内容
     * @param targetId 目标用户 ID
     */
    public static void handleFileMessage(String content, String targetId) {
        // 处理文件消息的逻辑
        log.info("Processing file message: {}", content);
        // 文件传输的逻辑
    }

    /**
     * 处理音频消息
     *
     * @param content  消息内容
     * @param targetId 目标用户 ID
     */
    public static void handleVideoMessage(String content, String targetId) {
    }

    /**
     * 处理视频消息
     *
     * @param content  消息内容
     * @param targetId 目标用户 ID
     */
    public static void handleAudioMessage(String content, String targetId) {

    }

}
