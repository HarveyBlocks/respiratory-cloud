package org.harvey.respiratory.cloud.common.utils;

import java.util.regex.Pattern;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 14:09
 */
public class RegexUtils {
    /**
     * 是否是有效手机格式
     *
     * @param phone 要校验的手机号
     * @return true:有效，false：无效
     */
    public static boolean isPhoneEffective(String phone) {
        return match(phone, RegexPatterns.PHONE_REGEX);
    }

    /**
     * 是否是可行的密码格式
     *
     * @param password 要校验的密码
     * @return true:有效，false：无效
     */
    public static boolean isPasswordEffective(String password) {
        return match(password, RegexPatterns.PASSWORD_REGEX);
    }

    // 校验是否复合正则格式
    private static boolean match(String str, Pattern regex) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return regex.matcher(str).matches();
    }


}
