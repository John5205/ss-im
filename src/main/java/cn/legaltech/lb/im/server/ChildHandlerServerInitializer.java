package cn.legaltech.lb.im.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Netty 服务器端初始化器
 * <p>
 * 注意：initChannel 只会在有新的连接建立时被调用。
 * 如果没有客户端连接到服务器，initChannel 是不会被触发的。
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/25
 */
public class ChildHandlerServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger log = LogManager.getLogger(ChildHandlerServerInitializer.class);

    /**
     * 初始化通道
     *
     * @param ch socket通道
     * @throws Exception 异常
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("Initializing pipeline...");
        ChannelPipeline pipeline = ch.pipeline();
        if (pipeline == null) {
            throw new IllegalStateException("Pipeline is null!");
        }
        // 配置服务器端处理器
        pipeline.addLast(new HttpServerCodec());  // HTTP 解码器
        pipeline.addLast(new HttpObjectAggregator(65536));  // HTTP 聚合器
        pipeline.addLast(new WebSocketServerProtocolHandler("/lb"));  // WebSocket 协议处理器
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));// 日志
        // 心跳检测（读、写、读写空闲时间分别为 60s, 0s, 0s）
        pipeline.addLast(new IdleStateHandler(60, 0, 0));
        pipeline.addLast(new HeartbeatHandler());//心跳检测

        // 如果是 childChannel (即每个连接)，我们设置子通道的处理器
        pipeline.addLast(new WebSocketServerHandler());  // 自定义 WebSocket 处理器
    }
}