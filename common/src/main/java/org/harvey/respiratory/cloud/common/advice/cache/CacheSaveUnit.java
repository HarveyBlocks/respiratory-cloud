package org.harvey.respiratory.cloud.common.advice.cache;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-31 16:41
 */
@Data
public class CacheSaveUnit {
    private final Object value;
    private final long time;
    private final TimeUnit timeUnit;
}
