package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * '匀称', '急促', '不规则'
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum Breathing implements IEnum<String> {
    BREATHING_1("匀称"),
    BREATHING_2("急促"),
    BREATHING_3("不规则");
    private final String description;


    Breathing(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
