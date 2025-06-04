package org.harvey.respiratory.cloud.common.advice.cache.executor;


/**
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 01:21
 */
public interface CacheExecutor<T> {


    /**
     * @param json 从缓存中查出的字符串
     */
    T toBean(String json);
}
