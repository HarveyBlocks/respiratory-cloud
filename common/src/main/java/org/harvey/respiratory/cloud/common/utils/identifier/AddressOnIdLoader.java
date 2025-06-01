package org.harvey.respiratory.cloud.common.utils.identifier;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * 加载地址字典
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-16 13:03
 */
public class AddressOnIdLoader {

    private final ObjectMapper objectMapper;
    private final String citySorted;
    private volatile Map<String, ProvinceIdMessage> dict;

    public AddressOnIdLoader(ObjectMapper objectMapper, String citySorted) {
        this.objectMapper = objectMapper;
        this.citySorted = citySorted;
    }

    /**
     * 高性能损耗
     */
    private Map<String, ProvinceIdMessage> load() {
        try (FileReader reader = new FileReader(citySorted)) {
            return objectMapper.readValue(reader, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, ProvinceIdMessage> get() {
        if (dict != null) {
            return dict;
        }
        synchronized (this) {
            if (dict != null) {
                return dict;
            }
            dict = load();
        }
        return dict;
    }

}
