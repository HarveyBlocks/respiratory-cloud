package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * '头颈', '上肢', '躯干', '下肢', '生殖器'
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum AffectedArea implements IEnum<String> {
    AREA_1("头颈"),
    AREA_2("上肢"),
    AREA_3("躯干"),
    AREA_4("下肢"),
    AREA_5("生殖器");
    private final String description;


    AffectedArea(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
