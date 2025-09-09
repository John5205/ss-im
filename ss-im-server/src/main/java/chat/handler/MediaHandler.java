package chat.handler;

import xin.harrison.im.annotation.MessageMapping;
import xin.harrison.im.dispatcher.MessageProcessorManager;
import lombok.extern.log4j.Log4j2;

/**
 * 媒体消息处理器（音频、视频）
 *
 * @author Harrison
 * @version 1.0.0
 */
@Log4j2
public class MediaHandler {

    @MessageMapping(value = "audio", authRequired = false)
    public static void handleAudioMessage(String message, String targetId) {
        log.info("Processing audio message: {} to target: {}", message, targetId);
        MessageProcessorManager.handleAudioMessage(message, targetId);
    }

    @MessageMapping(value = "video", authRequired = false)
    public static void handleVideoMessage(String message, String targetId) {
        log.info("Processing video message: {} to target: {}", message, targetId);
        MessageProcessorManager.handleVideoMessage(message, targetId);
    }
}
