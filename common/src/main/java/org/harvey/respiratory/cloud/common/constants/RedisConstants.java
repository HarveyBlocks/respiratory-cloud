package org.harvey.respiratory.cloud.common.constants;

/**
 * Redis有关的常量
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 15:17
 */
public class RedisConstants {
    public static final String LOCK_KEY_PREFIX = "lock:";
    public static final String QUERY_KEY_PREFIX = "respiratory:query:";
    public static final String BLACKLIST_PREFIX = "respiratory:blacklist:";
    public static final String LOGIN_CODE_KEY = QUERY_KEY_PREFIX + "login:code:";
    public static final Long LOGIN_CODE_TTL = 3 * 60L;
    public static final String QUERY_USER_KEY = QUERY_KEY_PREFIX + "user:query:token:";
    public static final int QUERY_USER_TTL = 30 * 60;

    public static final int CACHE_NULL_TTL = 2;
    public static final String USER_LOCK_KEY = LOCK_KEY_PREFIX + "user:";
    public static final long LOCK_TTL = 6 * 60L;
    public static final String FAKE_DATA_FOR_NULL = "";
    public static final long VALUE_CACHE_TTL = 5 * 60L;
    public static final long BLACKLIST_TIME = 5L;
    public static final long DEFAULT_LIMIT = 30;


    public static class Disease {
        public static final String PREFIX = "disease:";
        public static final String ON_VISIT_ID = PREFIX + "visit_doctor:";
        public static final String ON_ID = PREFIX + "id:";
        public static final String ON_PAGE = "page:";
    }
}
