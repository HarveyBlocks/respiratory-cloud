package org.harvey.respiratory.cloud.common.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 日期范围
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-20 23:11
 */
@Data
public class RangeDate implements Serializable {
    /**
     * null for 不设限制
     */
    private final Date start;
    /**
     * null for 不设限制
     */
    private final Date end;
}
