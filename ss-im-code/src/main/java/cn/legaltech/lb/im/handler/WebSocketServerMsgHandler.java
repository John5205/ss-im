package cn.legaltech.lb.im.handler;

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
                handleTextMessage((TextWebSocketFrame) frame);
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
     */
    private void handleTextMessage(TextWebSocketFrame frame) {
        // 处理文本消息
        String message = frame.text();
        System.out.println("Received Text Message: " + message);
        // 可以响应客户端
        frame.retain();  // 如果需要在后续的 handler 中使用该消息
        // ctx.writeAndFlush(new TextWebSocketFrame("Your message: " + message));
    }

    /**
     * 处理二进制
     *
     * @param frame
     */
    private void handleBinaryMessage(BinaryWebSocketFrame frame) {
        // 处理二进制消息
        System.out.println("Received Binary Message: " + frame.content().toString(io.netty.util.CharsetUtil.UTF_8));
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
        System.out.println("Received Ping Frame");
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
        System.out.println("Received Pong Frame");
    }

    /**
     * 处理关闭连接
     *
     * @param frame
     * @param ctx
     */
    private void handleClose(CloseWebSocketFrame frame, ChannelHandlerContext ctx) {
        // 处理 Close 帧（关闭连接请求）
        System.out.println("Received Close Frame");
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
