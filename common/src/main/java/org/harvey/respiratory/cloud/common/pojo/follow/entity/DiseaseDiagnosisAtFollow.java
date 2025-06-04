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
import org.harvey.respiratory.cloud.common.pojo.follow.enums.SeverityLevel;
import org.harvey.respiratory.cloud.common.pojo.follow.enums.Sinusitis;

import java.io.Serializable;
import java.sql.Date;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 14:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_disease_diagnosis_at_follow")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("疾病诊断表")
public class DiseaseDiagnosisAtFollow implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("疾病名称")
    @TableField("disease_name")
    private String diseaseName;

    @ApiModelProperty("严重程度")
    @TableField("severity_level")
    private SeverityLevel severityLevel;

    @ApiModelProperty("诊断日期")
    @TableField("diagnosis_date")
    private Date diagnosisDate;

    @ApiModelProperty("支气管哮喘")
    @TableField("bronchial_asthma_id")
    private Integer bronchialAsthmaId;

    @ApiModelProperty("过敏性鼻炎")
    @TableField("allergic_rhinitis_id")
    private Integer allergicRhinitisId;

    @ApiModelProperty("atop性皮炎")
    @TableField("atop_dermatitis")
    private SeverityLevel atopDermatitis;

    @ApiModelProperty("鼻窦炎")
    @TableField("sinusitis")
    private Sinusitis sinusitis;

    @ApiModelProperty("过敏原个体检测")
    @TableField("adenoid_hypertrophy")
    private Boolean adenoidHypertrophy;

    @ApiModelProperty("扁桃体肿大")
    @TableField("enlarged_tonsils")
    private Boolean enlargedTonsils;

    @ApiModelProperty("打鼾")
    @TableField("snoring")
    private Boolean snoring;

    @ApiModelProperty("阻塞性睡眠呼吸暂停")
    @TableField("obstructive_sleep_apnea")
    private Boolean obstructiveSleepApnea;

    @ApiModelProperty("其他")
    @TableField("other")
    private String other;
}
