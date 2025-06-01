package org.harvey.respiratory.cloud.common.pojo.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 严重程度
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:36
 */
public enum Severity implements IEnum<String> {
    // 轻微
    SLIGHT,
    // 中度
    MODERATE,
    // 重度
    SEVERE;

    @Override
    public String getValue() {
        return name();
    }
}
