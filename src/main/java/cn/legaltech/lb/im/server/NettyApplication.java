package cn.legaltech.lb.im.server;


import cn.legaltech.lb.im.annotation.NettyServer;

/**
 * Netty服务
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/25 14:05
 */
public class NettyApplication {

    /**
     * 启动Netty服务
     *
     * @param annotatedClass 配置类
     * @throws Exception  异常
     */
    public static void startServer(Class<?> annotatedClass) throws Exception {
        // 检查类是否有 @NettyServer 注解
        if (annotatedClass.isAnnotationPresent(NettyServer.class)) {
            NettyServer nettyServer = annotatedClass.getAnnotation(NettyServer.class);
            int port = nettyServer.port();
            // 启动服务器
            new NettyServerStarter(port).start();  // NettyServer 继续封装具体启动逻辑
        } else {
            throw new IllegalArgumentException("Class " + annotatedClass.getName() + " is not annotated with @NettyServer.");
        }
    }
}