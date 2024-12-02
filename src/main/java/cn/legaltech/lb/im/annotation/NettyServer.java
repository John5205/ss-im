package cn.legaltech.lb.im.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NettyServer
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NettyServer {

    /**
     * 服务端口
     *
     * @return 返回服务端口
     */
    int port() default 8086;

    /**
     * 扫描包名
     *
     * @return 返回包名
     */
    String packageName();
}