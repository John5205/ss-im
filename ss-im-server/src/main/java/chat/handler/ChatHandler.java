package chat.handler;

import xin.harrison.im.annotation.MessageMapping;
import xin.harrison.im.dispatcher.MessageProcessorManager;
import lombok.extern.log4j.Log4j2;

/**
 * 聊天消息处理器
 *
 * @author Harrison
 * @version 1.0.0
 */
@Log4j2
public class ChatHandler {

    @MessageMapping(value = "chat", authRequired = false)
    public static void handleChatMessage(String message, String targetId) {
        log.info("Processing chat message: {} to target: {}", message, targetId);
        MessageProcessorManager.handleChatMessage(message, targetId);
    }

    @MessageMapping(value = "text", authRequired = false)
    public static void handleTextMessage(String message, String targetId) {
        log.info("Processing text message: {} to target: {}", message, targetId);
        MessageProcessorManager.handleChatMessage(message, targetId);
    }
}
