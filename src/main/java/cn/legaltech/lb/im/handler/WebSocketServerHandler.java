package cn.legaltech.lb.im.handler;

import cn.legaltech.lb.im.dispatcher.MessageDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 聊天服务处理中心
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/25
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger log = LogManager.getLogger(WebSocketServerHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        log.info("Received message: " + msg);
        ByteBuf content = msg.content();
        String text = content.toString(CharsetUtil.UTF_8);
        // 解析消息类型和内容
        String[] parts = text.split(":");
        String messageType = parts[0];  // 消息类型
        String message = parts[1];     // 消息内容
        // 处理消息
        MessageDispatcher.dispatch(ctx, messageType, message);
    }

    /**
     * 处理消息
     *
     * @param msg 消息
     * @return 返回转换的消息体
     */
    private TextWebSocketFrame messageHandler(Object msg) {
        // 如果是字节数据
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
            log.info("Received ByteBuf message: " + textWebSocketFrame);
            return textWebSocketFrame;
        }
        return null;
    }

    /**
     * 如果是自己实现的 handler，确保没有阻止消息传递
     *
     * @param ctx 通道参数
     * @param msg 消息
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Received data in custom handler: " + msg);
        channelRead0(ctx, messageHandler(msg));  // 调用此行以确保消息传递
    }

    /**
     * 客户端连接成功
     *
     * @param ctx 通道参数
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client connected: " + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    /**
     * 客户端断开连接
     *
     * @param ctx 通道参数
     * @throws Exception 异常
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client disconnected: " + ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    /**
     * 异常处理
     *
     * @param ctx   通道参数
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}