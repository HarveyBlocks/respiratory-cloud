package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * 左肺满布
 * 右肺满布
 * 左肺散在
 * 右肺散在
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum LungTest implements IEnum<String> {
    TEST_1("左肺满布"),
    TEST_2("右肺满布"),
    TEST_3("左肺散在"),
    TEST_4("右肺散在");
    private final String description;


    LungTest(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
