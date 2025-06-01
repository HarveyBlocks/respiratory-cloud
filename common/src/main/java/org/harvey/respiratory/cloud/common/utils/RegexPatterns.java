package org.harvey.respiratory.cloud.common.utils;

import java.util.regex.Pattern;

/**
 * 正则表达式
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 15:17
 */
public abstract class RegexPatterns {
    /**
     * 手机号正则
     */
    public static final Pattern PHONE_REGEX = Pattern.compile(
            "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$");
    /**
     * 密码正则。4~32位的字母、数字、下划线
     */
    public static final Pattern PASSWORD_REGEX = Pattern.compile("^\\w{4,32}$");


}
