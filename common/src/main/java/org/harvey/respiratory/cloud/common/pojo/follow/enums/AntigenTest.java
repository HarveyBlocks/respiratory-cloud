package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * 抗原测试
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum AntigenTest implements IEnum<String> {
    TEST_1("阴性"), TEST_2("阳性");
    private final String description;


    AntigenTest(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
