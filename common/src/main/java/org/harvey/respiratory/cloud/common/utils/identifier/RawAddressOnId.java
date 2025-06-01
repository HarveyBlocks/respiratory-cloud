package org.harvey.respiratory.cloud.common.utils.identifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 从本地文件加载的身份证城市
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-16 12:56
 */
public class RawAddressOnId {
    public int code;
    public String address;


    public static Map<String, ProvinceIdMessage> loadRaw(String cityRawJsonFile) {
        RawAddressOnId[] arr;
        ObjectMapper objectMapper = new ObjectMapper();
        try (FileReader reader = new FileReader(cityRawJsonFile)) {
            arr = objectMapper.readValue(reader, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 找到??0000的, 作为outer, 其他作为inner, ?? 部分相同的放到一起
        // 找到????00的, 作为
        Map<String, ProvinceIdMessage> map = new HashMap<>();
        for (RawAddressOnId addressOnId : arr) {
            if (addressOnId.code % 10000 == 0) {
                // 是??00
                registerProvince(addressOnId, map);
            } else if (addressOnId.code % 100 == 0) {
                // 是????00
                registerCity(addressOnId, map);
            } else {
                // 是??????
                registerDistinct(addressOnId, map);
            }
        }
        return map;
    }

    private static void registerDistinct(RawAddressOnId addressOnId, Map<String, ProvinceIdMessage> map) {
        String key = String.format("%02d", addressOnId.code / 10000);
        ProvinceIdMessage provinceIdMessage = map.get(key);
        if (provinceIdMessage == null) {
            provinceIdMessage = new ProvinceIdMessage();
            map.put(key, provinceIdMessage);
        }

        String cityKey = String.format("%02d", addressOnId.code / 100 % 100);
        CityIdMessage cityIdMessage = provinceIdMessage.inner.get(cityKey);
        if (cityIdMessage == null) {
            cityIdMessage = new CityIdMessage();
            provinceIdMessage.inner.put(cityKey, cityIdMessage);
        }
        String districtKey = String.format("%02d", addressOnId.code % 100);
        DistrictIdMessage districtIdMessage = cityIdMessage.inner.get(districtKey);
        if (districtIdMessage != null) {
            if (districtIdMessage.address != null) {
                throw new RuntimeException("重复的" + addressOnId.address);
            }
            districtIdMessage.address = addressOnId.address;
            districtIdMessage.code = String.format("%06d", addressOnId.code);
        } else {
            cityIdMessage.inner.put(
                    districtKey, new DistrictIdMessage(String.format("%06d", addressOnId.code), addressOnId.address));
        }
    }

    private static void registerCity(RawAddressOnId addressOnId, Map<String, ProvinceIdMessage> map) {
        String key = String.format("%02d", addressOnId.code / 10000);
        ProvinceIdMessage provinceIdMessage = map.get(key);
        if (provinceIdMessage == null) {
            provinceIdMessage = new ProvinceIdMessage();
            map.put(key, provinceIdMessage);
        }
        String cityKey = String.format("%02d", addressOnId.code / 100 % 100);
        CityIdMessage cityIdMessage = provinceIdMessage.inner.get(cityKey);
        if (cityIdMessage != null) {
            if (cityIdMessage.address != null) {
                throw new RuntimeException("重复的" + addressOnId.address);
            }
            cityIdMessage.address = addressOnId.address;
            cityIdMessage.code = String.format("%06d", addressOnId.code);
        } else {
            provinceIdMessage.inner.put(
                    cityKey, new CityIdMessage(String.format("%06d", addressOnId.code), addressOnId.address));
        }
    }

    private static void registerProvince(RawAddressOnId addressOnId, Map<String, ProvinceIdMessage> map) {
        // 是??0000
        String key = String.format("%02d", addressOnId.code / 10000);
        ProvinceIdMessage provinceIdMessage = map.get(key);
        if (provinceIdMessage != null) {
            if (provinceIdMessage.address != null) {
                throw new RuntimeException("重复的" + addressOnId.address);
            }
            provinceIdMessage.address = addressOnId.address;
            provinceIdMessage.code = String.format("%06d", addressOnId.code);
        } else {
            map.put(key, new ProvinceIdMessage(String.format("%06d", addressOnId.code), addressOnId.address));
        }
    }
}
