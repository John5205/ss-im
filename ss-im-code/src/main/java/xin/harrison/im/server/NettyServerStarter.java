package xin.harrison.im.server;

import xin.harrison.im.handler.ScannerHandler;
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
    private final int port;

    /**
     * 包名
     */
    private final String packageName;

    /**
     * websocket 路径
     */
    private final String wsPath;

    /**
     * 构造方法
     *
     * @param port 端口
     */
    public NettyServerStarter(int port, String wsPath, String packageName) {
        this.port = port;
        this.packageName = packageName;
        this.wsPath = wsPath;
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
                    .childHandler(new ChildHandlerServerInitializer(wsPath))      // 确保在此配置了正确的处理器
                    .option(ChannelOption.SO_BACKLOG, 128)  // TCP缓冲区大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持连接
            ;
            ScannerHandler.scanAndRegisterHandlers(packageName);
            ChannelFuture f = bootstrap.bind(port).sync(); // 绑定端口，开始接收进来的连接
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    log.info("Shutdown hook triggered, closing server channel...");
                    f.channel().close().sync();
                } catch (InterruptedException ignored) {
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }));
            log.info("Starting Netty server on port: " + port + " success");
            f.channel().closeFuture().sync(); // 等待服务器监听端口关闭
        } finally {
            bossGroup.shutdownGracefully(); // 释放线程组资源
            workerGroup.shutdownGracefully(); // 释放线程组资源
        }
    }
}


