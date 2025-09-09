package xin.harrison.im.dispatcher;

/**
 * 消息处理器
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/12/2
 */
public interface MessageProcessor {

    /**
     * 消息处理
     *
     * @param messageContent 消息内容
     * @param targetId       接收方ID
     */
    void process(String messageContent, String targetId);
}


