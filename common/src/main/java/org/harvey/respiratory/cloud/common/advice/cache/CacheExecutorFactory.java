package org.harvey.respiratory.cloud.common.advice.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import org.harvey.respiratory.cloud.common.advice.cache.bind.CacheExecutorBind;
import org.harvey.respiratory.cloud.common.advice.cache.bind.MultipleQueryBind;
import org.harvey.respiratory.cloud.common.advice.cache.executor.*;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.harvey.respiratory.cloud.common.utils.RandomUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.function.Supplier;

/**
 * 缓存写增强的执行者的生产工程
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-22 12:38
 */
@Component
public class CacheExecutorFactory {


    private final CacheExecutorBind cacheExecutorBind;

    public CacheExecutorFactory(
            StringRedisTemplate stringRedisTemplate, JacksonUtil jacksonUtil, RandomUtil randomUtil) {
        this.cacheExecutorBind = new CacheExecutorBind(stringRedisTemplate, jacksonUtil, randomUtil);
    }


    public <T> SingleCacheExecutor<T> onValue(
            String key, Supplier<T> slowSupplier, TypeReference<T> typeReference, boolean updateExpire) {
        return new ValueCacheExecutor<>(cacheExecutorBind, slowSupplier, typeReference, updateExpire, key);
    }


    /**
     * @param queryDependency 由于希望结果也是有序的, 故key也是有序的, 到时候会以key的形式
     */
    public <B extends Serializable, R extends QueryBasisHaving<B>> MultiCacheExecutor<R> onMultipleValue(
            List<B> queryDependency, MultipleQueryBind<B, R> queryBind) {
        return new ValueMultiCacheExecutor<>(cacheExecutorBind, queryBind, queryDependency);
    }
}
