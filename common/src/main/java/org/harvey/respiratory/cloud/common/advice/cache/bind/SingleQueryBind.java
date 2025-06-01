package org.harvey.respiratory.cloud.common.advice.cache.bind;

import lombok.Data;

import java.util.function.Supplier;

/**
 * 一个函数操作对应一个Bind
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-31 22:47
 */
@Data
public class SingleQueryBind<T> {
    private final Supplier<T> slowQuery;
    private final boolean updateExpire;
}
