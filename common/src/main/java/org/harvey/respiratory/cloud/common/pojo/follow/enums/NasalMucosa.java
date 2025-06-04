package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * 鼻黏膜(苍白，水肿，肿胀，清涕，评分
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum NasalMucosa implements IEnum<String> {
    MUCOSA_1("苍白"),
    MUCOSA_2("水肿"),
    MUCOSA_3("肿胀"),
    MUCOSA_4("清涕"),
    Mucosa_5("评分");
    private final String description;


    NasalMucosa(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
