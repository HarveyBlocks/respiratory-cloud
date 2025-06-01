package org.harvey.respiratory.cloud.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json工具类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-08 12:12
 */
public class JacksonUtil {
    public static final PrintStream OUT = System.out;
    private final ObjectMapper mapper;

    public JacksonUtil(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 对所有调用次方法之后的转化都生效, 不会报错了
     */
    public static void ignoreUnknownFieldInJson(ObjectMapper mapper) {
        // 对于Json中的未知字段选择忽略
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 对所有调用次方法之后的转化都生效, 继续报错
     */
    public void emphasisUnknownFieldInJson() {
        // 对于Json中的未知字段选择重视
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    public String toJsonStr(Object bean) {
        try {
            return mapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJsonStr(HashMap<String, Object> map) {
        //map<String,String>转json
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T toBean(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public <T> T toBean(String json) {
        try {
            return mapper.readValue(json, new TypeReference<T>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T[] toBeanArray(String arrayJson, Class<T[]> type) {
        try {
            return mapper.readValue(arrayJson, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> toBeanList(String listJson) {
        try {
            return mapper.readValue(listJson, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> toMap(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> toStringMap(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Map<?, ?> toMap(Object bean) {
        return mapper.convertValue(bean, Map.class);
    }

    public <T> T toBean(Map<String, ?> map, Class<T> type) {
        return mapper.convertValue(map, type);
    }

    public String pretty(Object bean) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void printPretty(Object bean) {
        OUT.println(pretty(bean));
    }

    public byte[] toBytes(Object bean) {
        try {
            return mapper.writeValueAsBytes(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
