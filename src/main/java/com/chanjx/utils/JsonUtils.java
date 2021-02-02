package com.chanjx.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json对象操作工具
 *
 * @author 陈俊雄
 **/
@Slf4j
public abstract class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            // 反序列化忽略Json对象在实体类中没有的字段
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * 实体类转json字符串
     *
     * @param obj obj
     * @return jsonStr
     * @throws JsonProcessingException e
     */
    public static String obj2Json(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    /**
     * 实体类转json字符串，并忽略实体类为空的字段
     *
     * @param obj obj
     * @return jsonBytes
     * @throws JsonProcessingException e
     */
    public static byte[] obj2JsonBytes(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj).getBytes();
    }

    /**
     * 实体类转json字符串，并忽略实体类为空的字段
     *
     * @param obj obj
     * @return jsonStr
     * @throws JsonProcessingException e
     */
    public static String obj2JsonNonNull(Object obj) throws JsonProcessingException {
        final ObjectMapper copy = MAPPER.copy()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return copy.writeValueAsString(obj);
    }

    /**
     * 实体类转json字符串，并忽略实体类为空的字段
     *
     * @param obj obj
     * @return jsonBytes
     * @throws JsonProcessingException e
     */
    public static byte[] obj2JsonNonNullBytes(Object obj) throws JsonProcessingException {
        final ObjectMapper copy = MAPPER.copy()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return copy.writeValueAsString(obj).getBytes();
    }

    /**
     * 实体类转json字符串，将内容包裹成一个Json属性
     *
     * @param obj obj
     * @return jsonStr
     * @throws JsonProcessingException e
     */
    public static String obj2JsonWrapRootValue(Object obj) throws JsonProcessingException {
        final ObjectMapper copy = MAPPER.copy()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        return copy.writeValueAsString(obj);
    }

    /**
     * 实体类转json字符串，将内容包裹成一个Json属性，并忽略实体类为空的字段
     *
     * @param obj obj
     * @return jsonStr
     * @throws JsonProcessingException e
     */
    public static String obj2JsonNonNullWrapRootValue(Object obj) throws JsonProcessingException {
        final ObjectMapper copy = MAPPER.copy()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return copy.writeValueAsString(obj);
    }

    /**
     * json字符串转实体类
     *
     * @param jsonStr jsonStr
     * @param clazz   Class
     * @param <T>     T
     * @return T
     * @throws JsonProcessingException e
     */
    public static <T> T json2Obj(String jsonStr, Class<T> clazz) throws JsonProcessingException {
        return MAPPER.readValue(jsonStr, clazz);
    }

    /**
     * 带根值的json字符串转实体类
     *
     * @param jsonStr jsonStr
     * @param clazz   Class
     * @param <T>     T
     * @return T
     * @throws IOException e
     */
    public static <T> T json2ObjUnwrapRootValue(String jsonStr, Class<T> clazz) throws IOException {
        final ObjectMapper copy = MAPPER.copy()
                .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        return copy.readValue(jsonStr, clazz);
    }

    /**
     * json字符串转实体类
     *
     * @param jsonStr  jsonStr
     * @param javaType javaType可通过{@link #getJavaType}获取
     * @param <T>      T
     * @return T
     * @throws IOException e
     */
    public static <T> T json2Obj(String jsonStr, JavaType javaType) throws IOException {
        return MAPPER.readValue(jsonStr, javaType);
    }

    /**
     * 带根值的json字符串转实体类
     *
     * @param jsonStr  jsonStr
     * @param javaType Class
     * @param <T>      T
     * @return T
     * @throws IOException e
     */
    public static <T> T json2ObjUnwrapRootValue(String jsonStr, JavaType javaType) throws IOException {
        final ObjectMapper copy = MAPPER.copy()
                .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        return copy.readValue(jsonStr, javaType);
    }

    /**
     * json字符串转实体类
     * {@link #getJavaType(Class, Class[])}
     *
     * @param jsonStr          jsonStr
     * @param parametrized     class
     * @param parameterClasses class[]
     * @param <T>              T
     * @return T
     * @throws IOException e
     */
    public static <T> T json2Obj(String jsonStr, Class<?> parametrized, Class<?>... parameterClasses) throws IOException {
        return MAPPER.readValue(jsonStr, getJavaType(parametrized, parameterClasses));
    }

    /**
     * json字符串转实体类
     * {@link #getJavaType(Class, JavaType...)}
     *
     * @param jsonStr          jsonStr
     * @param parametrized     class
     * @param parameterClasses class[]
     * @param <T>              T
     * @return T
     * @throws IOException e
     */
    public static <T> T json2ObjUnwrapRootValue(String jsonStr, Class<?> parametrized, Class<?>... parameterClasses) throws IOException {
        final ObjectMapper copy = MAPPER.copy()
                .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        return copy.readValue(jsonStr, getJavaType(parametrized, parameterClasses));
    }

    /**
     * 获取JavaType
     * 如：JsonUtils.getJavaType(String.class);
     *
     * @param type class
     * @return {@link JavaType}
     */
    public static JavaType getJavaType(Type type) {
        return MAPPER.getTypeFactory().constructType(type);
    }

    /**
     * 获取JavaType，常用于复杂对象
     * 如json转Map：
     * JsonUtils.getJavaType(Map.class, String.class, Long.class);
     *
     * @param parametrized     class
     * @param parameterClasses class[]
     * @return {@link JavaType}
     */
    public static JavaType getJavaType(Class<?> parametrized, Class<?>... parameterClasses) {
        return MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    /**
     * 获取JavaType，常用于复杂对象
     * 参考{@link #getJavaType(Class, Class[])}
     *
     * @param rawType        class
     * @param parameterTypes javaType[]
     * @return {@link JavaType}
     */
    public static JavaType getJavaType(Class<?> rawType, JavaType... parameterTypes) {
        return MAPPER.getTypeFactory().constructParametricType(rawType, parameterTypes);
    }

    /**
     * json字符串转Map
     *
     * @param jsonStr jsonStr
     * @param k       key
     * @param v       value
     * @param <K>     key class
     * @param <V>     value class
     * @return map
     * @throws IOException e
     */
    public static <K, V> HashMap<K, V> json2HashMap(String jsonStr, Class<K> k, Class<V> v) throws IOException {
        return MAPPER.readValue(jsonStr, TypeFactory.defaultInstance().constructMapType(HashMap.class, k, v));
    }

    /**
     * json字符串转list
     *
     * @param jsonStr jsonStr
     * @param t       class
     * @param <T>     T
     * @return list
     * @throws IOException e
     */
    public static <T> List<T> json2List(String jsonStr, Class<T> t) throws IOException {
        return MAPPER.readValue(jsonStr, TypeFactory.defaultInstance().constructCollectionType(List.class, t));
    }

    /**
     * map等对象转实体类，通过映射方式
     *
     * @param map   map
     * @param clazz class
     * @param <T>   T
     * @return T
     */
    public static <T> T map2Obj(Map<?, ?> map, Class<T> clazz) {
        return MAPPER.convertValue(map, clazz);
    }

    public static Map<String, Object> obj2MapSO(Object obj) {
        return MAPPER.convertValue(
                obj,
                TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
    }

    public static Map<String, String> obj2MapSS(Object obj) {
        return MAPPER.convertValue(
                obj,
                TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, String.class));
    }

    public static String beautiful(String jsonStr) throws IOException {
        final Object obj = json2Obj(jsonStr, Object.class);
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    public static String beautiful(Object obj) throws JsonProcessingException {
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
