package org.harvey.respiratory.cloud.common.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-02 11:52
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageRequest {
    private long start;
    private long current;
    private long limit;
}
