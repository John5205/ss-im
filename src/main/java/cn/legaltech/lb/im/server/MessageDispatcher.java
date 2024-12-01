package cn.legaltech.lb.im.server;

import cn.legaltech.lb.im.annotation.MessageMapping;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息分发器
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/25
 */
public class MessageDispatcher {

    private static final Logger log = LogManager.getLogger(HeartbeatHandler.class);

    /**
     * 消息类型与处理方法映射
     */
    private static final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();

    /**
     * 注册带有 @MessageMapping 注解的方法
     *
     * @param handlerInstance 处理器实例
     */
    public static void registerHandlers(Object handlerInstance) {
        Class<?> clazz = handlerInstance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MessageMapping.class)) {
                MessageMapping mapping = method.getAnnotation(MessageMapping.class);
                String messageType = mapping.value();
                handlers.put(messageType, new MessageHandler(handlerInstance, method, mapping));
                log.info("Registered handler for message type: " + messageType);
            }
        }
    }

    /**
     * 分发消息到对应的处理器
     *
     * @param ctx            通道上下文
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    public static void dispatch(ChannelHandlerContext ctx, String messageType, Object messageContent) {
        MessageHandler handler = handlers.get(messageType);
        if (handler == null) {
            log.error("No handler found for message type: " + messageType);
            return;
        }

        // 鉴权逻辑（如果需要）
        if (handler.mapping.authRequired()) {
            if (!isAuthenticated(ctx)) {
                log.error("Unauthorized access for message type: " + messageType);
                ctx.writeAndFlush("Unauthorized");
                return;
            }
        }

        // 异步或同步调用处理方法
        if (handler.mapping.async()) {
            new Thread(() -> invokeHandler(handler, ctx, messageContent)).start();
        } else {
            invokeHandler(handler, ctx, messageContent);
        }
    }

    /**
     * 调用处理方法
     *
     * @param handler        消息处理器
     * @param ctx            通道上下文
     * @param messageContent 消息内容
     */
    private static void invokeHandler(MessageHandler handler, ChannelHandlerContext ctx, Object messageContent) {
        try {
            handler.method.invoke(handler.instance, ctx, messageContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断通道是否已认证
     *
     * @param ctx 通道上下文
     * @return 结果
     */
    private static boolean isAuthenticated(ChannelHandlerContext ctx) {
        // 简单示例：假设 channel 有鉴权标志
        Boolean isAuthenticated = ctx.channel().hasAttr(AttributeKey.valueOf("token"));
        return Boolean.TRUE.equals(isAuthenticated);
    }

    /**
     * 内部类：消息处理器封装
     */
    private static class MessageHandler {
        private final Object instance;
        private final Method method;
        private final MessageMapping mapping;

        public MessageHandler(Object instance, Method method, MessageMapping mapping) {
            this.instance = instance;
            this.method = method;
            this.mapping = mapping;
        }
    }
}