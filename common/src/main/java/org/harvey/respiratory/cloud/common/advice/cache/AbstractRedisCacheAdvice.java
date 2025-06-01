package org.harvey.respiratory.cloud.common.advice.cache;


import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.utils.Attachment;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.harvey.respiratory.cloud.common.utils.RandomUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

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
@Slf4j
public abstract class AbstractRedisCacheAdvice {



    @NotNull
    protected CacheSaveUnit buildCacheSaveUnit(long ttl, Object entity) {
        CacheSaveUnit unit;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        if (entity == null) {
            // 防止穿透
            log.warn("没有查到数据, 制造假数据到缓存");
            // key是假数据
            unit = new CacheSaveUnit(RedisConstants.FAKE_DATA_FOR_NULL, RedisConstants.CACHE_NULL_TTL, timeUnit);
        } else {
            log.warn("将查到数据, 放到缓存");
            unit = new CacheSaveUnit(entity, ttl, timeUnit);
        }
        return unit;
    }



    protected static class HaveToExecuteSlowException extends ServerException {
        public final Attachment attachment;

        public HaveToExecuteSlowException() {
            this(new Attachment());
        }

        public HaveToExecuteSlowException(Attachment attachment) {
            this.attachment = attachment;
        }
    }
}
