package com.chanjx.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 陈俊雄
 * @since 2020/11/17
 **/
public abstract class XmlUtils {

    private static final XmlMapper MAPPER = new XmlMapper();

    static {
        // 反序列化忽略Json对象在实体类中没有的字段
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String obj2Xml(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    public static byte[] obj2XmlBytes(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsBytes(obj);
    }

    public static String obj2XmlBytesNonNull(Object obj) throws JsonProcessingException {
        final XmlMapper copy = MAPPER.copy();
        copy.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return copy.writeValueAsString(obj);
    }

    public static byte[] obj2XmlBytesNonNullBytes(Object obj) throws JsonProcessingException {
        final XmlMapper copy = MAPPER.copy();
        copy.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return copy.writeValueAsBytes(obj);
    }

    public static <T> T xml2Obj(String xmlStr,Class<T> clazz ) throws JsonProcessingException {
        return MAPPER.readValue(xmlStr, clazz);
    }

    public static <T> T xml2Obj(String xmlStr, JavaType javaType) throws JsonProcessingException {
        return MAPPER.readValue(xmlStr, javaType);
    }

    public static <T> T xml2Obj(String xmlStr, Class<?> parametrized, Class<?>... parameterClasses) throws IOException {
        return MAPPER.readValue(xmlStr, getJavaType(parametrized, parameterClasses));
    }

    /**
     * xml字符串转Map
     *
     * @param xmlStr xmlStr
     * @param k        key
     * @param v        value
     * @param <K>      key class
     * @param <V>      value class
     * @return map
     * @throws IOException e
     */
    public static <K, V> HashMap<K, V> json2HashMap(String xmlStr, Class<K> k, Class<V> v) throws IOException {
        return MAPPER.readValue(xmlStr, TypeFactory.defaultInstance().constructMapType(HashMap.class, k, v));
    }

    /**
     * xml字符串转list
     *
     * @param xmlStr xmlStr
     * @param t        class
     * @param <T>      T
     * @return list
     * @throws IOException e
     */
    public static <T> List<T> xml2List(String xmlStr, Class<T> t) throws IOException {
        return MAPPER.readValue(xmlStr, TypeFactory.defaultInstance().constructCollectionType(List.class, t));
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
        final Object obj = xml2Obj(jsonStr, Object.class);
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    public static String beautiful(Object obj) throws JsonProcessingException {
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
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
}
