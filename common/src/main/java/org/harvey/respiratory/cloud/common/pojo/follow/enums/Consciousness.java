package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * '清醒', '谵妄', '昏睡', '昏迷'
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum Consciousness implements IEnum<String> {
    CONSCIOUSNESS_1("清醒"),
    CONSCIOUSNESS_2("谵妄"),
    CONSCIOUSNESS_3("昏睡"),
    CONSCIOUSNESS_4("昏迷");
    private final String description;


    Consciousness(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
