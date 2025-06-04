package org.harvey.respiratory.cloud.common.pojo.enums;


import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-03 22:34
 */
public enum SymptomaticPresentationType implements IEnum<String> {
    ASTHMA,
    ALLERGIC_RHINITIS,
    ECZEMA_OR_CHARACTERISTIC_DERMATITIS;

    @Override
    public String getValue() {
        return name();
    }
}
