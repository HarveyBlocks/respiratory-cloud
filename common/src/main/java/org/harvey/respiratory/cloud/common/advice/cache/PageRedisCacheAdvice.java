package org.harvey.respiratory.cloud.common.advice.cache;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.common.advice.cache.bind.PageQueryBind;
import org.harvey.respiratory.cloud.common.advice.cache.executor.QueryBasisHaving;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.pojo.vo.BasisPage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-02 15:40
 */
@Component
@Slf4j
public class PageRedisCacheAdvice {
    @Resource
    private MultiRedisCacheAdvice multiRedisCacheAdvice;

    /**
     * @return 返回查询基准
     */
    public <B extends Serializable> List<B> advice(
            Page<? extends QueryBasisHaving<B>> page,
            PageQueryBind<B> queryBind)  {
        //  分页的缓存
        long current = page.getCurrent();
        long size = page.getSize();
        long start = (current - 1) * size;
        // 需要在缓存中定位的页
        long redisCurrentStart = start / RedisConstants.DEFAULT_LIMIT + 1;
        long redisCurrentEnd = (start + size) / RedisConstants.DEFAULT_LIMIT + 1;

        // 获取到的页面集
        // 需要查询的页码是...
        // 包含两头的
        List<Long> currents = LongStream.range(redisCurrentStart, redisCurrentEnd + 1)
                .boxed()
                .collect(Collectors.toList());
        // 缓存增强
        List<BasisPage<B>> integerIdPagePairList = multiRedisCacheAdvice.adviceOnValue(currents, queryBind);
        // 需要的id
        return integerIdPagePairList.stream()
                .map(BasisPage::getBasis)
                .flatMap(List::stream)
                .skip(start - start / RedisConstants.DEFAULT_LIMIT * RedisConstants.DEFAULT_LIMIT)
                .limit(size)
                .collect(Collectors.toList());
    }
}
