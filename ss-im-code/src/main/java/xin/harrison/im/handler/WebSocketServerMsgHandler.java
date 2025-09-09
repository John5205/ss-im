package xin.harrison.im.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.log4j.Log4j2;

/**
 * WebSocket 服务器消息处理程序
 *
 * @author Harrison
 * @version 1.0.0
 */
@Log4j2
public class WebSocketServerMsgHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            // 根据不同的 WebSocket 帧类型判断并处理
            if (frame instanceof TextWebSocketFrame) {
                // 处理文本消息
                handleTextMessage((TextWebSocketFrame) frame, ctx);
            } else if (frame instanceof BinaryWebSocketFrame) {
                // 处理二进制消息
                handleBinaryMessage((BinaryWebSocketFrame) frame);
            } else if (frame instanceof PingWebSocketFrame) {
                // 处理 Ping 帧
                handlePing((PingWebSocketFrame) frame, ctx);
            } else if (frame instanceof PongWebSocketFrame) {
                // 处理 Pong 帧
                handlePong((PongWebSocketFrame) frame);
            } else if (frame instanceof CloseWebSocketFrame) {
                // 处理关闭连接请求
                handleClose((CloseWebSocketFrame) frame, ctx);
            }
        }
    }

    /**
     * 处理文本
     *
     * @param frame
     * @param ctx
     */
    private void handleTextMessage(TextWebSocketFrame frame, ChannelHandlerContext ctx) {
        // 处理文本消息
        String message = frame.text();
        log.info("Received Text Message: " + message);
        
        // 这里可以添加消息路由逻辑
        // 例如：解析 JSON 消息并路由到相应的处理器
        try {
            // 简单的消息处理示例
            if (message.startsWith("ping")) {
                ctx.writeAndFlush(new TextWebSocketFrame("pong"));
            } else if (message.startsWith("login:")) {
                String userId = message.substring(6);
                ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"login_success\",\"userId\":\"" + userId + "\"}"));
            } else {
                // 默认回显
                ctx.writeAndFlush(new TextWebSocketFrame("Echo: " + message));
            }
        } catch (Exception e) {
            log.error("Error processing text message: " + e.getMessage());
            ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"error\",\"message\":\"Message processing failed\"}"));
        }
        
        // 可以响应客户端
        frame.retain();  // 如果需要在后续的 handler 中使用该消息
    }

    /**
     * 处理二进制
     *
     * @param frame
     */
    private void handleBinaryMessage(BinaryWebSocketFrame frame) {
        // 处理二进制消息
        log.info("Received Binary Message: " + frame.content().toString(io.netty.util.CharsetUtil.UTF_8));
        // 可根据需要转化为其他格式
    }

    /**
     * 处理 Ping
     *
     * @param frame
     * @param ctx
     */
    private void handlePing(PingWebSocketFrame frame, ChannelHandlerContext ctx) {
        // 处理 Ping 帧（心跳请求）
        log.info("Received Ping Frame");
        // 发送 Pong 帧响应
        ctx.writeAndFlush(new PongWebSocketFrame(frame.content()));
    }

    /**
     * 处理 Pong
     *
     * @param frame
     */
    private void handlePong(PongWebSocketFrame frame) {
        // 处理 Pong 帧（心跳响应）
        log.info("Received Pong Frame");
    }

    /**
     * 处理关闭连接
     *
     * @param frame
     * @param ctx
     */
    private void handleClose(CloseWebSocketFrame frame, ChannelHandlerContext ctx) {
        // 处理 Close 帧（关闭连接请求）
        log.info("Received Close Frame");
        // 响应关闭连接
        ctx.writeAndFlush(new CloseWebSocketFrame());
        ctx.close();
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}


