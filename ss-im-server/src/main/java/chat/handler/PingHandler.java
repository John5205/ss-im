package chat.handler;

import cn.legaltech.lb.im.annotation.MessageMapping;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;

/**
 * 检测心跳
 *
 * @author Harrison
 * @version 1.0.0
 */
@Log4j2
public class PingHandler {

    @MessageMapping(value = "ping", async = true)
    public static void handleChatMessage(ChannelHandlerContext ctx, String message) {
        // 处理聊天消息
        log.info("Received ping message: " + message);
        TextWebSocketFrame frame = new TextWebSocketFrame("pong");
        ctx.writeAndFlush(frame);
    }
}
