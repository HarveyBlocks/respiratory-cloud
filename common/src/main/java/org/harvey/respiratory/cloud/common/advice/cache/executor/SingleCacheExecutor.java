package org.harvey.respiratory.cloud.common.advice.cache.executor;

import org.harvey.respiratory.cloud.common.advice.cache.CacheSaveUnit;
import org.springframework.data.redis.core.RedisOperations;

/**
 * 缓存写增强的执行者
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-22 12:11
 */
public interface SingleCacheExecutor<T> extends CacheExecutor<T> {


    String fastGet();

    T slowGet();


    void saveCache(CacheSaveUnit unit);

}
