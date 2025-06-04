package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * 消瘦', '正常', '肥胖
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum Appearance implements IEnum<String> {
    APPEARANCE_1("消瘦"),
    APPEARANCE_2("正常"),
    APPEARANCE_3("肥胖");
    private final String description;


    Appearance(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
