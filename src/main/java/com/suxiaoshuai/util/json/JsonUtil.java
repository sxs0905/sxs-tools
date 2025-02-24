package com.suxiaoshuai.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.suxiaoshuai.constants.DatePatternConstant;
import com.suxiaoshuai.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * json 工具类
 * 使用 Jackson
 */
public class JsonUtil<T> {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    // 日起格式化
    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        // 对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DatePatternConstant.NORM_DATETIME_PATTERN));
        // 忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 对象转Json格式字符串
     *
     * @param obj 实例
     * @return 格式化结果
     */
    public static <T> String toJson(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("obj：{}转成Json异常", obj.getClass().getName(), e);
            return null;
        }
    }

    /**
     * 字符串转换为自定义对象
     *
     * @param str   要转换的字符串
     * @param clazz 自定义对象的class对象
     * @return 解析后结果
     */
    public static <T> T parse(String str, Class<T> clazz) {
        if (StringUtil.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            logger.error("parse json：{} --> obj:{},异常", str, clazz.getName(), e);
            return null;
        }
    }

    /**
     * 方法从给定的JSON内容字符串反序列化JSON内容
     *
     * @param str           json数据
     * @param typeReference typeReference
     * @return 解析后结果
     */
    public static <T> T parse(String str, TypeReference<T> typeReference) {
        if (StringUtil.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            logger.error("parse json：{} --> obj:{},异常", str, typeReference.getType(), e);
            return null;
        }
    }

    /**
     * 解析json串为list
     *
     * @param json         json数据
     * @param elementClass 集合元素类型
     * @return 集合
     */
    public static <T> List<T> toList(String json, Class<T> elementClass) {
        return toList(json, List.class, elementClass);
    }

    /**
     * 解析json串为集合
     *
     * @param json            json数据
     * @param collectionClass 集合类型
     * @param elementClass    元素类型
     * @return 集合数据
     */
    public static <T> List<T> toList(String json, Class<? extends List> collectionClass, Class<T> elementClass) {
        if (StringUtil.isEmpty(json) || elementClass == null) {
            return Collections.emptyList();
        }
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            logger.error("parse json：{} --> list:{},element:{},异常", json, collectionClass.getName(),
                    elementClass.getName(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 解析json数据为Map<Object,Object>
     *
     * @param json json数据
     * @return map
     */
    public static Map<Object, Object> toMap(String json) {
        return toMap(json, Map.class, null, null);
    }

    /**
     * 解析json数据为Map<String,Object>
     *
     * @param json json数据
     * @return map
     */
    public static Map<String, Object> toStrKeyMap(String json) {
        return toMap(json, Map.class, String.class, Object.class);
    }

    /**
     * 解析json数据为Map指定类型
     *
     * @param json       json数据
     * @param mapClass   map类型
     * @param keyClass   key类型
     * @param valueClass value类型
     * @param <K>        key类型
     * @param <V>        value类型
     * @return map
     */
    public static <K, V> Map<K, V> toMap(String json, Class<? extends Map> mapClass, Class<K> keyClass,
                                         Class<V> valueClass) {
        if (StringUtil.isEmpty(json) || mapClass == null) {
            return new HashMap<>();
        }
        try {
            if (keyClass == null) {
                keyClass = (Class<K>) Object.class;
            }
            if (valueClass == null) {
                valueClass = (Class<V>) Object.class;
            }
            JavaType javaType = objectMapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            logger.error("parse json：{} --> map key:{},value:{},异常", json, keyClass.getName(), valueClass.getName(), e);
            return new HashMap<>();
        }
    }
}
