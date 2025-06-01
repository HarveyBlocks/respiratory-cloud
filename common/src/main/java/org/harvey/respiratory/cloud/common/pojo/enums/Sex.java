package org.harvey.respiratory.cloud.common.pojo.enums;


import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * MALE, FEMALE
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:54
 */
public enum Sex implements IEnum<String> {
    MALE, FEMALE;

    @Override
    public String getValue() {
        return name();
    }
}
