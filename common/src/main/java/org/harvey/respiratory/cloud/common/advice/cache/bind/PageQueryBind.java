package org.harvey.respiratory.cloud.common.advice.cache.bind;

import com.fasterxml.jackson.core.type.TypeReference;
import org.harvey.respiratory.cloud.common.constants.KeyGenerator;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.pojo.vo.BasisPage;
import org.harvey.respiratory.cloud.common.pojo.vo.BasisPagePair;
import org.harvey.respiratory.cloud.common.pojo.vo.PageRequest;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-02 15:45
 */
public class PageQueryBind<B extends Serializable> extends MultipleQueryBind<Long, BasisPage<B>> {
    /**
     * @param keyGenerator  由queryDependency转变为缓存的key
     * @param slowQuery     返回值是list, 不得为null, 可以{@link List#isEmpty}
     * @param typeReference 类型
     */
    public PageQueryBind(
            KeyGenerator<Long> keyGenerator,
            SlowQuery<B> slowQuery,
            TypeReference<BasisPage<B>> typeReference) {
        super(keyGenerator, (currents) -> adviceSlowQuery(currents, slowQuery), false, typeReference);
    }

    @FunctionalInterface
    public interface SlowQuery<B extends Serializable> {
        List<? extends BasisPagePair<B>> apply(List<PageRequest> request);
    }

    @NotNull
    private static <B extends Serializable> List<BasisPage<B>> adviceSlowQuery(
            List<Long> currents,
            SlowQuery<B> slowQuery) {
        // 参数准备
        List<PageRequest> pageRequests = currents.stream()
                .map(current -> new PageRequest((current - 1) * RedisConstants.DEFAULT_LIMIT, current,
                        RedisConstants.DEFAULT_LIMIT
                ))
                .collect(Collectors.toList());
        // 真正执行慢查询
        List<? extends BasisPagePair<B>> list = slowQuery.apply(pageRequests);
        // 结果分组
        return list.stream()
                .collect(Collectors.groupingBy(
                        BasisPagePair::getCurrent,
                        Collectors.mapping(BasisPagePair::getId, Collectors.toList())
                ))
                .entrySet()
                .stream()
                .map(e -> new BasisPage<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
