
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
public enum SkinPrickTest implements IEnum<String> {
    TEST_1("屋尘螨"),
    TEST_2("粉尘螨");
    private final String description;


    SkinPrickTest(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
