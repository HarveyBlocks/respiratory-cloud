package org.harvey.respiratory.cloud.common.advice.cache;


import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.common.advice.cache.bind.MultipleQueryBind;
import org.harvey.respiratory.cloud.common.advice.cache.executor.MultiCacheExecutor;
import org.harvey.respiratory.cloud.common.advice.cache.executor.QueryBasisHaving;
import org.harvey.respiratory.cloud.common.constants.KeyGenerator;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.utils.Attachment;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 对于查询操作的缓存增强
 * 写操作1. 异步写 2. 写完后删除缓存
 * 怎么设计? 写操作的参数全部都序列化, 然后指定函数?
 * 两边都注册一个
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-22 01:09
 */
@Component
@Slf4j
public class MultiRedisCacheAdvice extends AbstractRedisCacheAdvice {
    private static final String IN_CACHE_ENTITY = "__in__cache__entity__";
    private static final String NOT_IN_CACHE_INDEX = "__not__in__cache__index__";
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private CacheExecutorFactory cacheExecutorFactory;


    public <B extends Serializable, R extends QueryBasisHaving<B>> List<R> adviceOnValue(
            List<B> queryDependency, MultipleQueryBind<B, R> queryBind) {
        KeyGenerator<Integer> lockKeyGenerator = queryBind.getKeyGenerator().warp(queryDependency::get)
                .addPrefix(RedisConstants.LOCK_KEY_PREFIX);
        MultiCacheExecutor<R> cacheExecutor = cacheExecutorFactory.onMultipleValue(
                queryDependency, queryBind);
        try {
            return advice(lockKeyGenerator, cacheExecutor);
        } catch (InterruptedException e) {
            throw new ServerException("Redisson分布式锁被中断", e);
        }
    }

    public <R extends QueryBasisHaving<?>> List<R> advice(
            KeyGenerator<Integer> lockKeyGenerator, MultiCacheExecutor<R> cacheExecutor) throws InterruptedException {
        try {
            return fastQuery(cacheExecutor);
        } catch (HaveToExecuteSlowException haveTo) {
            log.debug("从缓存获取数据失败, 需要从数据库获取");
            Set<Integer> notInCacheIndexes = notInCacheIndexes(haveTo.attachment);
            List<String> valuesInCache = valuesInCache(haveTo.attachment);
            Map<Integer, R> inDbMap = synchronizedTransfer(notInCacheIndexes, lockKeyGenerator, cacheExecutor);
            if (inDbMap == null) {
                // 锁全部失败了
                return toBeanList(valuesInCache, cacheExecutor);
            }
            return rebuildEntities(valuesInCache, inDbMap, cacheExecutor);
        }
    }

    private <T> List<T> fastQuery(MultiCacheExecutor<T> cacheExecutor) {
        Attachment attachment = new Attachment();
        List<String> values = cacheExecutor.fastGet(/*attachment*/);
        // 获取结果是null的索引, 表示在缓存中不存在
        Set<Integer> cacheNullIndex = IntStream.range(0, values.size())
                .filter(i -> values.get(i) == null) // 如果是 RedisConstants.FAKE_DATA_FOR_NULL, 表示假数据, 如果是null, 表示是需要从数据库查
                .boxed()
                .collect(Collectors.toSet());
        // 缓存中不存在的键, 需要从数据库里查
        if (cacheNullIndex.isEmpty()) {
            // 不需要从数据库读
            return toBeanList(values, cacheExecutor);
        }
        log.debug("不得不执行的慢查询");
        attachment.put(NOT_IN_CACHE_INDEX, cacheNullIndex);
        attachment.put(IN_CACHE_ENTITY, values);
        throw new HaveToExecuteSlowException(attachment);

    }

    @NotNull
    private <T> List<T> toBeanList(List<String> values, MultiCacheExecutor<T> cacheExecutor) {
        return values.stream().map(s -> {
            if (s == null) {
                return null;
            } else if (RedisConstants.FAKE_DATA_FOR_NULL.equals(s)) {
                log.warn("查询Redis中存在的假数据");
                return null;
            } else {
                log.debug("从缓存中成功获取数据");
                return cacheExecutor.toBean(s);
            }
        }).collect(Collectors.toList());
    }

    private static Set<Integer> notInCacheIndexes(Attachment haveToAttachment) {
        return (Set<Integer>) haveToAttachment.get(NOT_IN_CACHE_INDEX);
    }

    private static List<String> valuesInCache(Attachment haveToAttachment) {
        return (List<String>) haveToAttachment.get(IN_CACHE_ENTITY);
    }

    /**
     * 完成将数据库中的数据读到本服务, 然后加载到缓存
     */
    private <R extends QueryBasisHaving<?>> Map<Integer, R> synchronizedTransfer(
            Set<Integer> notInCacheIndexes,
            KeyGenerator<Integer> lockKeyGenerator,
            MultiCacheExecutor<R> cacheExecutor) throws InterruptedException {
        // 上分布式锁防止击穿
        Map<Integer, RLock> lockGetSuccessfully = tryToGetLocks(notInCacheIndexes, lockKeyGenerator);
        if (lockGetSuccessfully.isEmpty()) {
            // 锁获取失败
            return null;
        }
        try {
            // 只要查询已经加锁的部分就行了
            // 慢查询
            Set<Integer> haveToSlowQueryIndexes = lockGetSuccessfully.keySet();
            Map<Integer, R> inDbMap = cacheExecutor.slowGet(haveToSlowQueryIndexes);
            saveCache(cacheExecutor, inDbMap);
            // 重新构建result, 填补空缺
            return inDbMap;
        } finally {
            releaseLockAll(lockGetSuccessfully);
        }

    }

    /**
     * @return 键是获取锁成功的索引, 值是成功获取到的锁, 值不为null
     */
    private Map<Integer, RLock> tryToGetLocks(
            Set<Integer> nullInCacheIndexes, KeyGenerator<Integer> lockKeyGenerator) throws InterruptedException {
        Map<Integer, RLock> allResourceLock = new HashMap<>();
        try {
            for (int nullInCacheIndex : nullInCacheIndexes) {
                // 要generic key, 需要获取那个Basis, 获取那个Basis需要, 则暴露Basis
                String lockKey = lockKeyGenerator.generate(nullInCacheIndex);
                RLock lock = redissonClient.getLock(lockKey);
                long waitTime = -1L;// 等待时间, 默认-1不等待
                boolean trySucceed = lock.tryLock(waitTime, RedisConstants.LOCK_TTL, TimeUnit.SECONDS);
                // 没有成功获取到锁
                if (trySucceed) {
                    allResourceLock.put(nullInCacheIndex, lock);
                }
            }
            return allResourceLock;
        } catch (Throwable t) {
            // 有异常就全部释放
            releaseLockAll(allResourceLock);
            throw t;
        }
    }

    private static void releaseLockAll(Map<Integer, RLock> allResourceLock) {
        for (RLock lock : allResourceLock.values()) {
            lock.unlock();
        }
    }

    private <R extends QueryBasisHaving<?>> void saveCache(
            MultiCacheExecutor<R> cacheExecutor, Map<Integer, R> inDbMap) {
        long ttl = RedisConstants.VALUE_CACHE_TTL;
        Map<Integer, CacheSaveUnit> needSaveToCache = new HashMap<>();
        inDbMap.forEach((haveToSlowQueryIndex, entityInDb) -> {
            // entityInDb 可能是null的
            // entity 在数据库中还是null, 那就是假数据了
            CacheSaveUnit cacheSaveUnit = buildCacheSaveUnit(ttl, entityInDb);
            needSaveToCache.put(haveToSlowQueryIndex, cacheSaveUnit);
        });
        cacheExecutor.saveCache(needSaveToCache);
    }

    @NotNull
    private <R extends QueryBasisHaving<?>> List<R> rebuildEntities(
            List<String> valuesInCache, Map<Integer, R> inDbMap, MultiCacheExecutor<R> cacheExecutor) {
        List<R> result = new ArrayList<>();
        for (int i = 0; i < valuesInCache.size(); i++) {
            String s = valuesInCache.get(i);
            if (s == null) {
                // 缓存中没有
                if (inDbMap.containsKey(i)) {
                    // 真正进行了慢查询的部分
                    R rInDb = inDbMap.get(i);
                    // 可能为null, 表示在数据库中没有
                    // 这个时候就是假数据了, 不过不再这里处理
                    result.add(rInDb);
                } else {
                    // 被放弃的部分
                    result.add(null);
                }
            } else if (RedisConstants.FAKE_DATA_FOR_NULL.equals(s)) {
                result.add(null);
            } else {
                result.add(cacheExecutor.toBean(s));
            }
        }
        return result;
    }


}
