package org.harvey.respiratory.cloud.common.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.harvey.respiratory.cloud.common.advice.cache.executor.QueryBasisHaving;

import java.io.Serializable;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-02 15:28
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasisPagePair<B extends Serializable> {
    private Long current;
    private B id;

}
