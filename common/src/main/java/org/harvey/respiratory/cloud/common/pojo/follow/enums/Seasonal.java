package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * ('常年性', '间歇性', '持续性')
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:31
 */
public enum Seasonal implements IEnum<String> {
    SEASONAL_1("常年性"), SEASONAL_2("间歇性"), SEASONAL_3("持续性");
    private final String description;

    Seasonal(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
