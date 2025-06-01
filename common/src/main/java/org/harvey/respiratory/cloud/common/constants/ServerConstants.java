package org.harvey.respiratory.cloud.common.constants;


import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * 系统常量类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 15:17
 */
public class ServerConstants {
    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String RESTRICT_REQUEST_TIMES = "7";
    public static final String RESTRICT_REQUEST_TIMES_FOR_TEMP = "5";
    public static final Set<String> ROOT_AUTH_URI = Set.of("/user/create");
    public static final Integer MAX_PAGE_SIZE = 10;
    public static final String TIME_FIELD = "time_field";
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_PAGE_SIZE_MSG = "10";
    public static final long CLEAR_CLICK_HISTORY_WAIT_MILLIONS = 10 * 60 * 1000;

    public static final long CHAT_RECORD_TIME_INTERVAL = 60 * 60 * 1000; // 一小时
    public static final String DEFAULT_DATE_FORMAT_STRING = "yyyy-MM-dd";
    /**
     * yyyy-MM-dd
     */
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_FORMAT_STRING);

    public static final String USER_INFO_HEADER_NAME = "user-info";
}
