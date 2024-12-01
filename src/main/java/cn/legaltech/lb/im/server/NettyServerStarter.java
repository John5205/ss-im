package cn.legaltech.lb.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Netty 服务器启动类
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/25 16:04
 */
public class NettyServerStarter {
    private static final Logger log = LogManager.getLogger(NettyServerStarter.class);
    /**
     * 服务器端口
     */
    private int port;

    /**
     * 构造方法
     *
     * @param port 端口
     */
    public NettyServerStarter(int port) {
        this.port = port;
    }

    /**
     * 启动 Netty 服务器
     *
     * @throws InterruptedException 异常
     */
    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();  // 用于接受连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();// 用于处理连接

        try {
            ServerBootstrap bootstrap = new ServerBootstrap(); // 创建服务器端引导类
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)    // 使用 NIO 的 SocketChannel    、
                    .childHandler(new ChildHandlerServerInitializer())      // 确保在此配置了正确的处理器
                    .option(ChannelOption.SO_BACKLOG, 128)  // TCP缓冲区大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持连接
            ;
            HandlerScanner.scanAndRegisterHandlers("cn.legaltech.lb.h5.ws.handler");
            ChannelFuture f = bootstrap.bind(port).sync(); // 绑定端口，开始接收进来的连接
            log.info("Starting Netty server on port: " + port + " success");
            f.channel().closeFuture().sync(); // 等待服务器监听端口关闭
        } finally {
            bossGroup.shutdownGracefully(); // 释放线程组资源
            workerGroup.shutdownGracefully(); // 释放线程组资源
        }
    }
}