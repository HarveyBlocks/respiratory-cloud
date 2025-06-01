package org.harvey.respiratory.cloud.common.advice.cache.executor;


import java.io.Serializable;

/**
 * 有主键的实体
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-31 16:23
 */
public interface QueryBasisHaving<B extends Serializable> {
    B getQueryBasis();
}
