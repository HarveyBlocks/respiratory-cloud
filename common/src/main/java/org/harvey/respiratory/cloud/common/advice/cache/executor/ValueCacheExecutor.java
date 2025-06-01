package org.harvey.respiratory.cloud.common.advice.cache.executor;

import lombok.AllArgsConstructor;
import org.harvey.respiratory.cloud.common.advice.cache.CacheSaveUnit;
import org.harvey.respiratory.cloud.common.advice.cache.bind.CacheExecutorBind;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * {@link org.springframework.data.redis.core.RedisTemplate#opsForValue()}
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-31 17:03
 */
@AllArgsConstructor
public class ValueCacheExecutor<T> implements SingleCacheExecutor<T> {
    private final CacheExecutorBind cacheExecutorBind;
    private final Supplier<T> slowQuery;
    private final boolean updateExpire;
    private final String key;

    @Override
    public String fastGet() {
        // key 实质上是作为字段了
        String value = cacheExecutorBind.opsForValue().get(key);
        if (value == null || !updateExpire) {
            return value;
        }
        long time = RedisConstants.FAKE_DATA_FOR_NULL.equals(value) ? RedisConstants.CACHE_NULL_TTL :
                RedisConstants.VALUE_CACHE_TTL;
        cacheExecutorBind.expire(key, time, TimeUnit.SECONDS);
        return value;
    }

    @Override
    public T slowGet() {
        return slowQuery.get();
    }



    @Override
    public void saveCache(CacheSaveUnit unit) {
        Object value = unit.getValue();
        cacheExecutorBind.opsForValue().set(key, cacheExecutorBind.toJsonStr(value));
        cacheExecutorBind.expire(key, unit.getTime(), unit.getTimeUnit());
    }

    @Override
    public T toBean(String json) {
        return cacheExecutorBind.toBean(json);
    }

}
