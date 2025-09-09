package xin.harrison.im.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json 格式工具类
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/24
 */
public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 将 json 字符串转为对象
     *
     * @param json  json字符串
     * @param clazz 转换的类
     * @param <T>   泛型
     * @return 返回转换的结果
     * @throws Exception 异常
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return mapper.readValue(json, clazz);
    }

    /**
     * 将对象转为 json 字符串
     *
     * @param obj 对象
     * @return 返回字符串
     * @throws Exception 异常
     */
    public static String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }
}


