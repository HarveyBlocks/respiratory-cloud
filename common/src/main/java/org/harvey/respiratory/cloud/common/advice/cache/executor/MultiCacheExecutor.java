package org.harvey.respiratory.cloud.common.advice.cache.executor;


import lombok.NonNull;
import org.harvey.respiratory.cloud.common.advice.cache.CacheSaveUnit;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 查询多个数据, 批量查询
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-31 16:02
 */
public interface MultiCacheExecutor<R> extends CacheExecutor<R> {
    /**
     * @return entity 为null, 表示没有在缓存中, 全查
     */
    @NonNull
    List<String> fastGet();

    /**
     * @param haveToSlowQueryIndexes 在缓存里是null的index,
     *                               即{@link #fastGet()}的返回值中, 元素为null的所在位置的索引
     *                               需求是吧这些索引对应的dependency的实体, 进行慢查询
     * @return Key 是 nullInCacheIndex 的元素, Value 为null, 表示没有在DAO中
     */
    Map<Integer, R> slowGet(Set<Integer> haveToSlowQueryIndexes);

    /**
     * @param map key是nullInCacheIndex
     *            需求是吧这些索引对应的dependency的实体, 将这些写入缓存
     *            value 不可能为null, 假数据就是{@link  RedisConstants#FAKE_DATA_FOR_NULL}空字符串
     */
    void saveCache(Map<Integer, CacheSaveUnit> map);



}
