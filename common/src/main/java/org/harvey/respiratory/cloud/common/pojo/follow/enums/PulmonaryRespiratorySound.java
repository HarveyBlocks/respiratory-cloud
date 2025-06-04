package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * 左清
 * 右清
 * 左粗
 * 右粗
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum PulmonaryRespiratorySound implements IEnum<String> {
    SOUND_1("左清"),
    SOUND_2("右清"),
    SOUND_3("左粗"),
    SOUND_4("右粗");
    private final String description;


    PulmonaryRespiratorySound(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
