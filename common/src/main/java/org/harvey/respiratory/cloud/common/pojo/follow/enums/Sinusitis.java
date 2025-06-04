package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * '急性', '慢性'
 * cockroach, Cat dander,Dog dander,Egg white,牛奶
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum Sinusitis implements IEnum<String> {
    SINUSITIS_1("急性"),
    SINUSITIS_2("慢性");
    private final String description;


    Sinusitis(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
