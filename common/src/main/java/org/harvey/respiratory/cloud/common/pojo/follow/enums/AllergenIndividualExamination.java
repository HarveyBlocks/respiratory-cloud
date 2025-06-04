package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * (屋尘螨,粉尘螨,花粉,杂草,烟曲霉,链格孢,蟑螂,猫皮屑,狗毛屑,鸡蛋白,牛奶,虾,螃蟹,大豆,芝麻,小麦,坚果,其它)
 * cockroach, Cat dander,Dog dander,Egg white,牛奶
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:07
 */
@Getter
public enum AllergenIndividualExamination implements IEnum<String> {
    Examination_1("屋尘螨"),
    Examination_2("粉尘螨"),
    Examination_3("花粉"),
    Examination_4("杂草"),
    Examination_5("烟曲霉"),
    Examination_6("链格孢"),
    Examination_7("蟑螂"),
    Examination_8("猫皮屑"),
    Examination_9("狗毛屑"),
    Examination_10("鸡蛋白"),
    Examination_11("牛奶"),
    Examination_12("虾"),
    Examination_13("螃蟹"),
    Examination_14("大豆"),
    Examination_15("芝麻"),
    Examination_16("小麦"),
    Examination_17("坚果"),
    Examination_18("其它");
    private final String description;


    AllergenIndividualExamination(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return description;
    }
}
