package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * '良好', '倦怠'
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum SpiritualState implements IEnum<String> {
    STATE_1("良好"),
    STATE_2("倦怠");
    private final String description;


    SpiritualState(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
