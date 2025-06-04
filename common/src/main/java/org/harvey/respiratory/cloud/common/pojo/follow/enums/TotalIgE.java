package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * (尘螨组合, 霉菌组合, 宠物毛屑组合, 常见食物组合, 坚果组合, 其他)
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
public enum TotalIgE implements IEnum<String> {
    IGE_1("尘螨组合"),
    IGE_2("霉菌组合"),
    IGE_3("宠物毛屑组合"),
    IGE_4("常见食物组合"),
    IGE_5("坚果组合"),
    IGE_6("其他");
    private final String description;

    TotalIgE(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
