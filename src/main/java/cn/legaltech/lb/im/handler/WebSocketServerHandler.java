package cn.legaltech.lb.im.handler;

import cn.legaltech.lb.im.dispatcher.MessageDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 聊天服务处理中心
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/25
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger log = LogManager.getLogger(WebSocketServerHandler.class);

    /**
     * 读取消息
     *
     * @param channelHandlerContext 通道
     * @param webSocketFrame        消息
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) throws Exception {
        messageHandler(channelHandlerContext, webSocketFrame);
    }

    /**
     * 处理消息
     *
     * @param msg 消息
     */
    private void messageHandler(ChannelHandlerContext ctx, WebSocketFrame msg) throws IOException {
        // 如果是字节数据
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
            log.info("Received ByteBuf message: " + textWebSocketFrame);
            String content = textWebSocketFrame.text();
            // 解析消息类型和内容
            String[] parts = content.split(":");
            String messageType = parts[0];  // 消息类型
            String message = parts[1];     // 消息内容
            // 处理消息
            MessageDispatcher.dispatch(ctx, messageType, message);
        } else if (msg instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) msg;
            ByteBuf content = binaryWebSocketFrame.content();
            log.info("Received binary message: " + content.readableBytes());
            // 示例：将二进制数据直接转发或保存到文件
            byte[] data = new byte[content.readableBytes()];
            content.readBytes(data);
            // 保存文件（伪代码）
            saveFile("uploaded_file", data);
            // 回复客户端
            ctx.writeAndFlush(new TextWebSocketFrame("File received, size: " + data.length));
        } else if (msg instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame frame = (CloseWebSocketFrame) msg;
            log.info("Received close frame: " + frame.reasonText());
            ctx.close();
        } else if (msg instanceof PingWebSocketFrame) {
            PingWebSocketFrame frame = (PingWebSocketFrame) msg;
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain())); // 回复 Pong
        } else if (msg instanceof PongWebSocketFrame) {
            log.info("Received pong");
        } else {
            throw new UnsupportedOperationException("Unsupported frame type: " + msg.getClass().getName());
        }
    }

    /**
     * 文件保存逻辑
     *
     * @param fileName 文件名称
     * @param data     数据
     * @throws IOException 异常
     */
    private void saveFile(String fileName, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(data);
        }
    }

    /**
     * 如果是自己实现的 handler，确保没有阻止消息传递
     *
     * @param ctx 通道参数
     * @param msg 消息
     * @throws Exception 异常
     */
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("Received data in custom handler: " + msg);
//        channelRead0(ctx, messageHandler(msg));  // 调用此行以确保消息传递
//    }

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