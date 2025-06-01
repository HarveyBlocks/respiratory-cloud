package org.harvey.respiratory.cloud.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 对`东八区`宝宝更友好的时间工具
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-30 22:19
 */
public class TimeUtil {

    private static final ZoneOffset ZONE = ZoneOffset.of("+8");

    public static Long toMillion(LocalDateTime time) {
        return time == null ? null :
                time.toInstant(ZONE).toEpochMilli();
    }

    public static LocalDateTime toTime(Long timestamp) {
        return timestamp == null ? null :
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZONE);
    }
}
