package cn.legaltech.lb.im.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息映射注解
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageMapping {

    /**
     * 消息类型标识
     *
     * @return 消息类型标识
     */
    String value();

    /**
     * 是否异步处理
     *
     * @return 是否异步处理
     */
    boolean async() default false;

    /**
     * 消息优先级（默认 0，数字越小优先级越高）
     *
     * @return 消息优先级
     */
    int priority() default 0;

    /**
     * 是否需要鉴权
     *
     * @return 默认不需要鉴权
     */
    boolean authRequired() default false;
}