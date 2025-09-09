package chat.handler;

import xin.harrison.im.annotation.MessageMapping;
import xin.harrison.im.dispatcher.MessageProcessorManager;
import lombok.extern.log4j.Log4j2;

/**
 * 文件消息处理器
 *
 * @author Harrison
 * @version 1.0.0
 */
@Log4j2
public class FileHandler {

    @MessageMapping(value = "file", authRequired = false)
    public static void handleFileMessage(String message, String targetId) {
        log.info("Processing file message: {} to target: {}", message, targetId);
        MessageProcessorManager.handleFileMessage(message, targetId);
    }

    @MessageMapping(value = "image", authRequired = false)
    public static void handleImageMessage(String message, String targetId) {
        log.info("Processing image message: {} to target: {}", message, targetId);
        MessageProcessorManager.handleFileMessage(message, targetId);
    }
}
