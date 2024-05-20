package com.chanjx.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chanjx.utils.JsonUtils.MapperKey.*;

/**
 * Json对象操作工具
 *
 * @author chanjx
 **/
@Slf4j
public class JsonUtils {

    private static final ObjectMapper MAPPER =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    // 反序列化忽略Json对象在实体类中没有的字段
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(getLocalDateTime2TimestampModule());
    private static final Map<MapperKey, ObjectMapper> JACKSON_MAP =
            Collections.unmodifiableMap(
                    new HashMap<MapperKey, ObjectMapper>() {
                        {
                            // 默认配置Mapper
                            put(DEFAULT_MAPPER, MAPPER);
                            // 为空字段不参与序列化Mapper
                            put(NON_NULL_MAPPER,
                                    MAPPER.copy()
                                            .setSerializationInclusion(JsonInclude.Include.NON_NULL));
                            // （反）序列化Json字符串只包含一个Root节点的Mapper
                            // 如：Java对象 ResultBean(data=null, msg=一切 ok, code=00000, success=true, remark=null)
                            // 将序列化为：{"ResultBean":{"data":null,"msg":"一切 ok","code":"00000","success":true,"remark":null}}
                            put(ROOT_VALUE_MAPPER,
                                    MAPPER.copy()
                                            .configure(SerializationFeature.WRAP_ROOT_VALUE, true)
                                            .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true));
                            // （反）序列化Json字符串只包含一个Root节点，且为空字段不参与序列化的Mapper
                            // 如：Java对象 ResultBean(data=null, msg=一切 ok, code=00000, success=true, remark=null)
                            // 将序列化为：{"ResultBean":{"msg":"一切 ok","code":"00000","success":true}}
                            put(ROOT_VALUE_NON_NULL_MAPPER,
                                    MAPPER.copy()
                                            .configure(SerializationFeature.WRAP_ROOT_VALUE, true)
                                            .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true)
                                            .setSerializationInclusion(JsonInclude.Include.NON_NULL));
                        }
                    });

    private JsonUtils() {
    }

    /**
     * 实体类转json字符串
     *
     * @param obj obj
     * @return jsonStr
     * @throws JsonProcessingException e
     */
    public static String obj2Json(Object obj) throws JsonProcessingException {
        return JACKSON_MAP.get(DEFAULT_MAPPER).writeValueAsString(obj);
    }

    /**
     * 实体类转json字符串，并忽略实体类为空的字段
     *
     * @param obj obj
     * @return jsonBytes
     * @throws JsonProcessingException e
     */
    public static byte[] obj2JsonBytes(Object obj) throws JsonProcessingException {
        return JACKSON_MAP.get(DEFAULT_MAPPER).writeValueAsString(obj).getBytes();
    }

    /**
     * 实体类转json字符串，并忽略实体类为空的字段
     *
     * @param obj obj
     * @return jsonStr
     * @throws JsonProcessingException e
     */
    public static String obj2JsonNonNull(Object obj) throws JsonProcessingException {
        return JACKSON_MAP.get(NON_NULL_MAPPER).writeValueAsString(obj);
    }

    /**
     * 实体类转json字符串，并忽略实体类为空的字段
     *
     * @param obj obj
     * @return jsonBytes
     * @throws JsonProcessingException e
     */
    public static byte[] obj2JsonNonNullBytes(Object obj) throws JsonProcessingException {
        return JACKSON_MAP.get(NON_NULL_MAPPER).writeValueAsString(obj).getBytes();
    }

    /**
     * 实体类转json字符串，将内容包裹成一个Json属性
     *
     * @param obj obj
     * @return jsonStr
     * @throws JsonProcessingException e
     */
    public static String obj2JsonWrapRootValue(Object obj) throws JsonProcessingException {
        return JACKSON_MAP.get(ROOT_VALUE_MAPPER).writeValueAsString(obj);
    }

    /**
     * 实体类转json字符串，将内容包裹成一个Json属性，并忽略实体类为空的字段
     *
     * @param obj obj
     * @return jsonStr
     * @throws JsonProcessingException e
     */
    public static String obj2JsonNonNullWrapRootValue(Object obj) throws JsonProcessingException {
        return JACKSON_MAP.get(ROOT_VALUE_NON_NULL_MAPPER).writeValueAsString(obj);
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
        return JACKSON_MAP.get(DEFAULT_MAPPER).readValue(jsonStr, clazz);
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
        return JACKSON_MAP.get(ROOT_VALUE_MAPPER).readValue(jsonStr, clazz);
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
        return JACKSON_MAP.get(DEFAULT_MAPPER).readValue(jsonStr, javaType);
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
        return JACKSON_MAP.get(ROOT_VALUE_MAPPER).readValue(jsonStr, javaType);
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
        return JACKSON_MAP.get(DEFAULT_MAPPER).readValue(jsonStr, getJavaType(parametrized, parameterClasses));
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
        return JACKSON_MAP.get(ROOT_VALUE_MAPPER).readValue(jsonStr, getJavaType(parametrized, parameterClasses));
    }

    /**
     * 获取JavaType
     * 如：JsonUtils.getJavaType(String.class);
     *
     * @param type class
     * @return {@link JavaType}
     */
    public static JavaType getJavaType(Type type) {
        return JACKSON_MAP.get(DEFAULT_MAPPER).getTypeFactory().constructType(type);
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
        return JACKSON_MAP.get(DEFAULT_MAPPER).getTypeFactory().constructParametricType(parametrized, parameterClasses);
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
        return JACKSON_MAP.get(DEFAULT_MAPPER).getTypeFactory().constructParametricType(rawType, parameterTypes);
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
        return JACKSON_MAP.get(DEFAULT_MAPPER).readValue(jsonStr, TypeFactory.defaultInstance().constructMapType(HashMap.class, k, v));
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
        return JACKSON_MAP
                .get(DEFAULT_MAPPER)
                .readValue(jsonStr, TypeFactory.defaultInstance().constructCollectionType(List.class, t));
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
        return JACKSON_MAP.get(DEFAULT_MAPPER).convertValue(map, clazz);
    }

    /**
     * 将任意对象转换为Map&lt;String, Object&gt;
     *
     * @param obj 任意对象
     * @return Map<String, String>
     */
    public static Map<String, Object> obj2MapSO(Object obj) {
        return JACKSON_MAP
                .get(DEFAULT_MAPPER)
                .convertValue(obj, TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
    }

    /**
     * 将任意对象转换为Map<String, Object>,去除null对象
     *
     * @param obj 任意对象
     * @return Map<String, String>
     */
    public static Map<String, Object> obj2MapSONonNull(Object obj) {
        return JACKSON_MAP
                .get(NON_NULL_MAPPER)
                .convertValue(obj, TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
    }

    /**
     * 将任意对象转换为Map&lt;String, String&gt;
     *
     * @param obj 任意对象
     * @return Map<String, String>
     */
    public static Map<String, String> obj2MapSS(Object obj) {
        return JACKSON_MAP.get(DEFAULT_MAPPER)
                .convertValue(obj, TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, String.class));
    }

    /**
     * 将任意对象转换为Map&lt;String, String&gt;，并忽略实体类为空的字段
     *
     * @param obj 任意对象
     * @return Map<String, String>
     */
    public static Map<String, String> obj2MapSSNonNull(Object obj) {
        return JACKSON_MAP.get(NON_NULL_MAPPER)
                .convertValue(obj, TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, String.class));
    }

    /**
     * 将任意Json字符串并格式化输出
     *
     * @param jsonStr Json字符串
     * @return jsonStr
     * @throws IOException e
     */
    public static String beautiful(String jsonStr) throws IOException {
        final Object obj = json2Obj(jsonStr, Object.class);
        return JACKSON_MAP
                .get(DEFAULT_MAPPER)
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(obj);
    }

    /**
     * 将任意对象转成Json字符串并格式化输出
     *
     * @param obj 任意对象
     * @return jsonStr
     * @throws JsonProcessingException e
     */
    public static String beautiful(Object obj) throws JsonProcessingException {
        return JACKSON_MAP
                .get(DEFAULT_MAPPER)
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(obj);
    }

    /**
     * 判断Json对象是否含有指定key
     *
     * @param jsonStr jsonStr
     * @param key     指定key
     * @return boolean
     * @throws JsonProcessingException e
     */
    public static boolean hasKey(String jsonStr, String key) throws JsonProcessingException {
        final ObjectNode jsonNodes = JACKSON_MAP.get(DEFAULT_MAPPER).readValue(jsonStr, ObjectNode.class);
        return jsonNodes.has(key);
    }

    /**
     * 判断Json对象是否为数组
     *
     * @param jsonStr jsonStr
     * @param index   指定下标
     * @return boolean
     * @throws JsonProcessingException e
     */
    public static boolean hasIndex(String jsonStr, int index) throws JsonProcessingException {
        final ObjectNode jsonNodes = JACKSON_MAP.get(DEFAULT_MAPPER).readValue(jsonStr, ObjectNode.class);
        return jsonNodes.has(index);
    }

    /**
     * 拷贝默认Mapper
     *
     * @return {@link MapperKey#DEFAULT_MAPPER}
     */
    public static ObjectMapper copy() {
        return JACKSON_MAP.get(DEFAULT_MAPPER).copy();
    }

    public static ObjectMapper copy(MapperKey mapperKey) {
        return JACKSON_MAP.get(mapperKey).copy();
    }

    public enum MapperKey {
        DEFAULT_MAPPER,
        NON_NULL_MAPPER,
        ROOT_VALUE_MAPPER,
        ROOT_VALUE_NON_NULL_MAPPER,

    }

    /**
     * 时间戳与LocalDateTime互转
     *
     * @return module
     */
    private static Module getLocalDateTime2TimestampModule() {
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class, LocalDateTime2TimestampSerializer.INSTANCE);
        simpleModule.addDeserializer(LocalDateTime.class, Timestamp2LocalDateTimeSerializer.INSTANCE);
        return simpleModule;
    }

    /**
     * LocalDateTime序列化时间戳实现
     */
    private static class LocalDateTime2TimestampSerializer extends StdSerializer<LocalDateTime> {
        final static LocalDateTime2TimestampSerializer INSTANCE = new LocalDateTime2TimestampSerializer();

        protected LocalDateTime2TimestampSerializer() {
            super(LocalDateTime.class);
        }

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    /**
     * LocalDateTime反序列化时间戳实现
     */
    private static class Timestamp2LocalDateTimeSerializer extends StdDeserializer<LocalDateTime> {
        final static Timestamp2LocalDateTimeSerializer INSTANCE = new Timestamp2LocalDateTimeSerializer();

        protected Timestamp2LocalDateTimeSerializer() {
            super(LocalDateTime.class);
        }

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), ZoneId.systemDefault());
        }
    }
}
