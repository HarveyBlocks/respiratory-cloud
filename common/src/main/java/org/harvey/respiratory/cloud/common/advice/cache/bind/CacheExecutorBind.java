package org.harvey.respiratory.cloud.common.advice.cache.bind;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.respiratory.cloud.common.advice.cache.CacheSaveUnit;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.harvey.respiratory.cloud.common.utils.RandomUtil;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 14:47
 */
@AllArgsConstructor
@Getter
public class CacheExecutorBind {
    private final StringRedisTemplate stringRedisTemplate;
    private final JacksonUtil jacksonUtil;
    private final RandomUtil randomUtil;

    public long ttlRandom(long origin) {
        // 随机ttl防止雪崩
        if (origin <= 1) {
            return origin;
        }
        int right = (int) (origin / Math.log1p(origin));
        return origin + randomUtil.uniform(-right, right);
    }

    public <R> R toBean(String json) {
        return jacksonUtil.toBean(json);
    }

    public ValueOperations<String, String> opsForValue() {
        return stringRedisTemplate.opsForValue();
    }

    public String toJsonStr(Object value) {
        return jacksonUtil.toJsonStr(value);
    }

    public void expire(String key, long time, TimeUnit timeUnit) {
        stringRedisTemplate.expire(key, time > 0 ? ttlRandom(time) : time, timeUnit);
    }

    public void expire(Map<String, CacheSaveUnit> keyGeneratedMap) {
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keyGeneratedMap.forEach((k, v) -> {
                byte[] kBytes = k.getBytes();
                TimeUnit timeUnit = v.getTimeUnit();
                long time = ttlRandom(v.getTime());
                connection.expire(kBytes, timeUnit == null ? time : timeUnit.toSeconds(time));
            });
            return null;
        });
    }
}
