package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * 平坦', '隆起', '凹陷
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum Abdominal implements IEnum<String> {
    ABDOMINAL_1("平坦"),
    ABDOMINAL_2("隆起"),
    ABDOMINAL_3("凹陷");
    private final String description;


    Abdominal(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
