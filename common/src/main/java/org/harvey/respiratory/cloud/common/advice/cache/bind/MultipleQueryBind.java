package org.harvey.respiratory.cloud.common.advice.cache.bind;

import lombok.Data;
import org.harvey.respiratory.cloud.common.advice.cache.executor.QueryBasisHaving;
import org.harvey.respiratory.cloud.common.advice.cache.executor.ValueMultiCacheExecutor;
import org.harvey.respiratory.cloud.common.constants.KeyGenerator;

import java.io.Serializable;
import java.util.List;

/**
 * 一个函数操作对应一个Bind
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-31 22:47
 */
@Data
public class MultipleQueryBind<B extends Serializable, R extends QueryBasisHaving<B>> {

    private final KeyGenerator<B> keyGenerator;
    private final ValueMultiCacheExecutor.SlowQuery<B, R> slowQuery;
    private final boolean updateExpire;

    /**
     * @param keyGenerator 由queryDependency转变为缓存的key
     * @param slowQuery    返回值是list, 不得为null, 可以{@link List#isEmpty}
     * @param updateExpire 是否更新
     */
    public MultipleQueryBind(
            KeyGenerator<B> keyGenerator,
            ValueMultiCacheExecutor.SlowQuery<B, R> slowQuery,
            boolean updateExpire) {
        this.keyGenerator = keyGenerator;
        this.slowQuery = slowQuery;
        this.updateExpire = updateExpire;
    }

    public String generateKey(B b) {
        return keyGenerator.generate(b);
    }

    public List<R> querySlowly(List<B> basis) {
        return slowQuery.apply(basis);
    }
}
