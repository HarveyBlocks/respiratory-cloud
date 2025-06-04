package org.harvey.respiratory.cloud.common.advice.cache.executor;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.advice.cache.CacheSaveUnit;
import org.harvey.respiratory.cloud.common.advice.cache.bind.CacheExecutorBind;
import org.harvey.respiratory.cloud.common.advice.cache.bind.MultipleQueryBind;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.exception.ServerException;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link org.springframework.data.redis.core.RedisTemplate#opsForValue()}
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-31 17:05
 */
@AllArgsConstructor
public class ValueMultiCacheExecutor<B extends Serializable, R extends QueryBasisHaving<B>> implements
        MultiCacheExecutor<R> {
    private final CacheExecutorBind cacheExecutorBind;
    private final MultipleQueryBind<B, R> queryBind;
    private final List<B> queryBasis;

    public interface SlowQuery<B extends Serializable, R extends QueryBasisHaving<B>> extends
            Function<List<B>, List<R>> {
    }

    @NonNull
    @Override
    public List<String> fastGet() {
        List<String> keys = queryBasis.stream().map(queryBind::generateKey).collect(Collectors.toList());
        List<String> inCache = cacheExecutorBind.opsForValue().multiGet(keys);
        if (inCache == null) {
            throw new ServerException("unknown redis exception for result list is null");
        }
        if (queryBind.isUpdateExpire()) {
            updateExpire(keys, inCache);
        }
        return inCache;
    }

    private void updateExpire(List<String> keys, List<String> inCache) {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        Map<String, CacheSaveUnit> map = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = inCache.get(i);
            long time = value == null ? RedisConstants.CACHE_NULL_TTL : RedisConstants.VALUE_CACHE_TTL;
            map.put(key, new CacheSaveUnit(null, time, timeUnit));
        }
        cacheExecutorBind.expire(map);
    }

    @Override
    public Map<Integer, R> slowGet(Set<Integer> haveToSlowQueryIndexes) {
        // 有查询依据的实体
        if (queryBasis.isEmpty()) {
            return Collections.emptyMap();
        }
        List<B> haveToSlowQueryBasis = haveToSlowQueryIndexes.stream()
                .map(queryBasis::get)
                .collect(Collectors.toList());
        // 有查询依据的物体
        Map<B, R> fromDb = queryBind.querySlowly(haveToSlowQueryBasis)
                .stream()
                .collect(Collectors.toMap(QueryBasisHaving::getQueryBasis, r -> r));
        // 没查到的部分要用null填充
        return haveToSlowQueryIndexes.stream().collect(Collectors.toMap(i -> i, i -> fromDb.get(queryBasis.get(i))));
    }


    @Override
    public void saveCache(Map<Integer, CacheSaveUnit> map) {
        Map<String, CacheSaveUnit> keyGeneratedMap = map.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> queryBind.generateKey(queryBasis.get(e.getKey())), Map.Entry::getValue));
        Map<String, String> dataMap = keyGeneratedMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toJsonStr(e.getValue())));
        cacheExecutorBind.opsForValue().multiSet(dataMap);
        // 设置过期时间
        expireTimes(keyGeneratedMap);
    }

    private String toJsonStr(CacheSaveUnit saveUnit) {
        Object value = saveUnit.getValue();
        return cacheExecutorBind.toJsonStr(value);
    }


    @Override
    public R toBean(String json) {
        return cacheExecutorBind.toBean(json, queryBind.getTypeReference());
    }

    private void expireTimes(Map<String, CacheSaveUnit> keyGeneratedMap) {
        cacheExecutorBind.expire(keyGeneratedMap);
    }


}
