package org.harvey.respiratory.cloud.common.utils.identifier;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.ParseException;
import java.util.Date;

/**
 * 身份证的解析对象
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-16 16:42
 */
@AllArgsConstructor
@Getter
public class IdentifierCardId {
    private final String provinceAddress;
    private final String cityAddress;
    private final String distinctAddress;
    private final Date brithDate;
    private final String raw;

    public static IdentifierCardId phase(AddressOnIdLoader addressOnIdLoader, String id) {
        // 日期
        Date date;
        try {
            date = IdentifierIdPredicate.DATE_FORMAT.parse(id.substring(6, 14));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // 地址
        String address = id.substring(0, 6);
        String province = address.substring(0, 2);
        ProvinceIdMessage provinceIdMessage = addressOnIdLoader.get().get(province);
        if (address.endsWith("0000")) {
            return new IdentifierCardId(provinceIdMessage.getAddress(), "", "", date, id);
        }
        String city = address.substring(2, 4);
        CityIdMessage cityIdMessage = provinceIdMessage.getInner(city);
        if (address.endsWith("00")) {
            return new IdentifierCardId(provinceIdMessage.getAddress(), cityIdMessage.getAddress(), "", date, id);
        }
        String distinct = address.substring(4, 6);
        DistrictIdMessage districtIdMessage = cityIdMessage.getInner(distinct);
        return new IdentifierCardId(
                provinceIdMessage.getAddress(), cityIdMessage.getAddress(), districtIdMessage.getAddress(), date, id);
    }
}
