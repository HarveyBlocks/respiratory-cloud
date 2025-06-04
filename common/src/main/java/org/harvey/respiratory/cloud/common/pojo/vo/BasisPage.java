package org.harvey.respiratory.cloud.common.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.harvey.respiratory.cloud.common.advice.cache.executor.QueryBasisHaving;

import java.io.Serializable;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-02 15:30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasisPage<B extends Serializable> implements QueryBasisHaving<Long> {
    private Long current;
    private List<B> basis;

    @Override
    public Long getQueryBasis() {
        return current;
    }
}
