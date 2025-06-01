package org.harvey.respiratory.cloud.common.utils.identifier;

import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 身份证校验
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-16 11:40
 */
@Getter
public class IdentifierIdPredicate implements Predicate<String> {

    public static final Pattern PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    public static final int[] FACTOR = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    public static final char[] PARITY = new char[]{'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final int LENGTH = 18;
    private final Map<String, ProvinceIdMessage> addressDistinct;
    private final boolean openCheckCode;

    public IdentifierIdPredicate(boolean openCheckCode, AddressOnIdLoader addressOnIdLoader) {
        this.openCheckCode = openCheckCode;
        this.addressDistinct = addressOnIdLoader.get();
    }


    @Override
    public boolean test(String sequence) {
        if (sequence == null) {
            return false;
        }
        if (!checkRegex(sequence)) {
            return false;
        }
        String date = sequence.substring(6, 14);
        if (!checkDate(date)) {
            return false;
        }
        if (!checkCode(sequence)) {
            return false;
        }
        return checkAddress(sequence.substring(0, 6));
    }

    private boolean checkAddress(String address) {
        String province = address.substring(0, 2);
        ProvinceIdMessage provinceIdMessage = addressDistinct.get(province);
        if (provinceIdMessage == null) {
            return false;
        }
        if (address.endsWith("0000")) {
            return true;
        }
        String city = address.substring(2, 4);
        CityIdMessage cityIdMessage = provinceIdMessage.getInner(city);
        if (cityIdMessage == null) {
            return false;
        }
        if (address.endsWith("00")) {
            return true;
        }
        String distinct = address.substring(4, 6);
        DistrictIdMessage districtIdMessage = cityIdMessage.getInner(distinct);
        return districtIdMessage != null;
    }

    private boolean checkRegex(String sequence) {
        if (sequence.length() != LENGTH) {
            return false;
        }
        return PATTERN.matcher(sequence).matches();
    }

    private boolean checkDate(String date) {
        try {
            DATE_FORMAT.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean checkCode(String sequence) {
        if (!openCheckCode) {
            return true;
        }
        char code = sequence.charAt(17);
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Integer.parseInt(sequence.substring(i, i + 1)) * FACTOR[i];
        }
        return PARITY[sum % 11] == Character.toUpperCase(code);
    }
}
