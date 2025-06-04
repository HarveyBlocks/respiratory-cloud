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

import java.io.Serializable;
import java.sql.Date;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 14:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_hearing_test")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("听力测试表")
public class HearingTest implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("检查名称")
    @TableField("check_names")
    private String checkNames;

    @ApiModelProperty("检查日期")
    @TableField("inspection_date")
    private Date inspectionDate;

    @ApiModelProperty("声阻抗检查值")
    @TableField("check_acoustic_impedance_value")
    private String checkAcousticImpedanceValue;

    @ApiModelProperty("纯音听力检查值")
    @TableField("check_pure_tone_audiometry_value")
    private String checkPureToneAudiometryValue;
}
