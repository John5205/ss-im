package cn.legaltech.lb.im.server;

import cn.legaltech.lb.im.annotation.MessageMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描包路径下的类，并自动注册带有 @MessageMapping 的方法
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/26
 */
public class HandlerScanner {

    private static final Logger log = LogManager.getLogger(HeartbeatHandler.class);

    /**
     * 扫描包路径下的类，并自动注册带有 @MessageMapping 的方法
     *
     * @param basePackage 包名，例如 "com.example.myapp"
     */
    public static void scanAndRegisterHandlers(String basePackage) {
        try {
            List<Class<?>> classes = getClasses(basePackage);
            for (Class<?> clazz : classes) {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(MessageMapping.class)) {
                        MessageDispatcher.registerHandlers(instance);
                        break; // 一个类只需注册一次
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取包下所有类
     *
     * @param packageName 包名
     * @return List<Class < ?>>
     */
    private static List<Class<?>> getClasses(String packageName) throws Exception {
        String packagePath = packageName.replace(".", "/");
        URL root = Thread.currentThread().getContextClassLoader().getResource(packagePath);
        if (root == null) {
            throw new IllegalArgumentException("Package not found: " + packageName);
        }

        List<Class<?>> classes = new ArrayList<>();
        File directory = new File(root.getFile());
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".class")) {
                        String className = packageName + '.' + file.getName().replace(".class", "");
                        classes.add(Class.forName(className));
                    }
                }
            }
        }
        return classes;
    }
}