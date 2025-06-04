package org.harvey.respiratory.cloud.common.advice.cache;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.common.advice.cache.executor.SingleCacheExecutor;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.utils.Attachment;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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
public class RedisCacheAdvice extends AbstractRedisCacheAdvice {
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CacheExecutorFactory cacheExecutorFactory;

    public RedisCacheAdvice() {

    }


    public <T> T adviceOnValue(String queryKey, Supplier<T> slowQuery,
            TypeReference<T> typeReference, boolean updateExpire) {
        String lockKey = RedisConstants.LOCK_KEY_PREFIX + queryKey;
        return advice(lockKey, cacheExecutorFactory.onValue(queryKey, slowQuery,typeReference, updateExpire));
    }

    public <T> T advice(
            String lockKey, SingleCacheExecutor<T> cacheExecutor) {
        // 击穿->数据有一瞬间失效, 一瞬间大量请求打击数据库
        // 穿透->假数据
        // 雪崩->随机过期时间
        // 还得看需求
        try {
            return fastQuery(cacheExecutor);
        } catch (AbstractRedisCacheAdvice.HaveToExecuteSlowException haveTo) {
            log.debug("从缓存获取数据失败, 需要从数据库获取");
            return synchronizedTransfer(lockKey, cacheExecutor);
        }

    }

    private <T> T synchronizedTransfer(
            String lockKey, SingleCacheExecutor<T> cacheExecutor)  {
        // 上分布式锁防止击穿
        RLock lock = redissonClient.getLock(lockKey);
        long waitTime = -1L;// 等待时间, 默认-1不等待
        boolean trySucceed;
        try {
            trySucceed = lock.tryLock(waitTime, RedisConstants.LOCK_TTL, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ServerException("Redisson分布式锁被中断, 应该不可能, 因为不等待", e);
        }
        if (!trySucceed) {
            // 没有成功获取到锁
            return null;
        }
        // 上完锁之后再检查一下? 不需要, 因为快速失败不会导致有重新尝试的比哟啊
        try {
            T entity = cacheExecutor.slowGet();
            CacheSaveUnit unit = buildCacheSaveUnit(RedisConstants.VALUE_CACHE_TTL, entity);
            cacheExecutor.saveCache(unit);
            return entity;
        } finally {
            lock.unlock();
        }
    }


    private <T> T fastQuery(SingleCacheExecutor<T> cacheExecutor) {
        Attachment attachment = new Attachment();
        String value = cacheExecutor.fastGet(/*attachment*/);
        if (value == null) {
            log.debug("不得不执行的慢查询");
            throw new AbstractRedisCacheAdvice.HaveToExecuteSlowException(attachment);
        }
        if (RedisConstants.FAKE_DATA_FOR_NULL.equals(value)) {
            log.warn("Redis中存在的假数据");
            return null;
        } else {
            log.debug("从缓存中成功获取数据");
            return cacheExecutor.toBean(value);
        }
    }

}
