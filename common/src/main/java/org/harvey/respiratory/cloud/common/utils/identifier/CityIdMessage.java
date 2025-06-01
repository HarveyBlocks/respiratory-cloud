package org.harvey.respiratory.cloud.common.utils.identifier;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 身份证中的市
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-16 13:00
 */
@Getter
public class CityIdMessage {

    Map<String, DistrictIdMessage> inner = new HashMap<>();
    String code;
    String address;

    public CityIdMessage(String code, String address) {
        this.code = code;
        this.address = address;
    }

    public CityIdMessage() {
    }

    public DistrictIdMessage getInner(String partCode) {
        return inner.get(partCode);
    }
}
