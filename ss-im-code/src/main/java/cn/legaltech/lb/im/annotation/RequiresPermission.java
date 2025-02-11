package cn.legaltech.lb.im.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequiresPermission {

    /**
     * 必须具备的权限
     *
     * @return 返回权限数组
     */
    String[] value();
}