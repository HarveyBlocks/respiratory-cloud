package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * 收缩期
 * 舒张期
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum HeartMurmursDetected implements IEnum<String> {
   DETECTED_1("收缩期"),
   DETECTED_2("舒张期");
    private final String description;


    HeartMurmursDetected(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
