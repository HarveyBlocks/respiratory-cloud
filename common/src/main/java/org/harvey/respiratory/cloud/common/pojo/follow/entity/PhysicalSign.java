package org.harvey.respiratory.cloud.common.pojo.follow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.harvey.respiratory.cloud.common.pojo.follow.enums.*;

import java.io.Serializable;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 14:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_physical_signs")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("体征表")
public class PhysicalSign implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("体温℃")
    @TableField("temperature")
    private Double temperature;

    @ApiModelProperty("脉搏/分钟")
    @TableField("pulse_beats_minute")
    private Integer pulseBeatsMinute;

    @ApiModelProperty("血氧饱和度%")
    @TableField("blood_oxygen_saturation")
    private Integer bloodOxygenSaturation;

    @ApiModelProperty("是否吸氧")
    @TableField("whether_oxygen_inhalation")
    private Boolean whetherOxygenInhalation;

    @ApiModelProperty("神志")
    @TableField("consciousness")
    private Consciousness consciousness;

    @ApiModelProperty("精神状态")
    @TableField("spiritual_state")
    private SpiritualState spiritualState;

    @ApiModelProperty("呼吸")
    @TableField("breathing")
    private Breathing breathing;

    @ApiModelProperty("外观")
    @TableField("appearance")
    private Appearance appearance;

    /**
     * {@link  NasalMucosa}
     */
    @ApiModelProperty("鼻黏膜")
    @TableField("nasal_mucosa_bitmap")
    private byte[] nasalMucosaBitmap;

    @ApiModelProperty("三凹征")
    @TableField("three_concave")
    private Boolean threeConcave;
    /**
     * {@link  PulmonaryRespiratorySound}
     */
    @ApiModelProperty("肺部呼吸音")
    @TableField("pulmonary_respiratory_sound")
    private byte[] pulmonaryRespiratorySound;
    /**
     * {@link  LungTest}
     */
    @ApiModelProperty("干罗音")
    @TableField("rales")
    private byte[]  rales;
    /**
     * {@link  LungTest}
     */
    @ApiModelProperty("湿罗音")
    @TableField("wheezing")
    private byte[]  wheezing;
    /**
     * {@link  HeartMurmursDetected}
     */
    @ApiModelProperty("心脏杂音")
    @TableField("heart_murmurs_detected")
    private byte[]  heartMurmursDetected;

    @ApiModelProperty("腹部")
    @TableField("abdominal")
    private Abdominal abdominal;

    @ApiModelProperty("腹部压痛")
    @TableField("abdominal_tenderness")
    private Boolean abdominalTenderness;

    @ApiModelProperty("腹部弹痛")
    @TableField("bounce_pain")
    private Boolean bouncePain;

    @ApiModelProperty("皮疹分布")
    @TableField("distribution_of_rash_id")
    private Integer distributionOfRashId;

    @ApiModelProperty("电子呼吸音")
    @TableField("electronic_respiratory_sound")
    private String electronicRespiratorySound;
}
