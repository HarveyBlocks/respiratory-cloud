package org.harvey.respiratory.cloud.common.pojo.enums;

/**
 * 家庭关系
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:58
 */
public enum FamilyRelationship {
    // 父
    FATHER,
    // 母
    MOTHER,
    // 哥哥
    ELDER_BROTHER,
    // 姐姐
    ELDER_SISTER,
    // 弟弟
    YOUNGER_BROTHER,
    // 妹妹
    YOUNGER_SISTER,
    // 爷爷
    FATHER_OF_FATHER,
    // 奶奶
    MATHER_OF_FATHER,
    // 外公?
    FATHER_OF_MATHER,
    // 外婆?
    MATHER_OF_MATHER,
    // 叔叔
    YOUNGER_BROTHER_OF_FATHER,
    // 伯伯
    ELDER_BROTHER_OF_FATHER,
    // 舅舅
    BROTHER_OF_MOTHER,
    // 姑姑
    ELDER_SISTER_OF_FATHER,
    // 姨姨
    YOUNGER_SISTER_OF_FATHER;

    public boolean isFirstDegreeRelative() {
        return this.ordinal() < FATHER_OF_MATHER.ordinal();
    }
}
