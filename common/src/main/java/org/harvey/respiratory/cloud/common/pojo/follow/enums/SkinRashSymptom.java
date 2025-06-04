package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * 红斑，水肿/丘疹，渗出/结痂，表皮脱落，苔藓样变，干燥，其他
 * cockroach, Cat dander,Dog dander,Egg white,牛奶
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum SkinRashSymptom implements IEnum<String> {
    SYMPTOM_1("红斑"),
    SYMPTOM_2("水肿"),
    SYMPTOM_3("丘疹"),
    SYMPTOM_4("渗出"),
    SYMPTOM_5("表皮脱落"),
    SYMPTOM_6("苔藓样变"),
    SYMPTOM_7("干燥"),
    SYMPTOM_8("其他");
    private final String description;


    SkinRashSymptom(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
