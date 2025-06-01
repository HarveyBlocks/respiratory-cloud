package org.harvey.respiratory.cloud.common.utils.identifier;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 身份证中的省
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-16 12:59
 */
@Getter
public class ProvinceIdMessage {
    Map<String, CityIdMessage> inner = new HashMap<>();
    String code;
    String address;

    public ProvinceIdMessage(String code, String address) {
        this.code = code;
        this.address = address;
    }

    public ProvinceIdMessage() {
    }

    public CityIdMessage getInner(String partCode) {
        return inner.get(partCode);
    }
}
