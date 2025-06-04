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

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 14:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_pulmonary_function_test")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("肺功能测试表")
public class PulmonaryFunctionTest implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("一秒用力呼气量")
    @TableField("IC")
    private Double ic;

    @ApiModelProperty("一秒用力呼气量50")
    @TableField("MEF50")
    private Double mef50 ;

    @ApiModelProperty("一秒用力呼气量1秒")
    @TableField("FEV1")
    private Double fev1;

    @ApiModelProperty("肺活量")
    @TableField("FVC")
    private Double fvc;

    @ApiModelProperty("最大中段呼气量75/25")
    @TableField("MMEF75_25")
    private Double mmef75Or25;

    @ApiModelProperty("一秒用力呼气量1秒/肺活量")
    @TableField("FEV1_FVC")
    private Double fev1Fvc;

    @ApiModelProperty("最大中段呼气量25")
    @TableField("MEF25")
    private Double mef25;

    @ApiModelProperty("最大中段呼气量75")
    @TableField("MEF75")
    private Double mef75;

    @ApiModelProperty("最大呼气量")
    @TableField("MVV")
    private Double mvv;

    @ApiModelProperty("一秒用力呼气量1秒/最大呼气量")
    @TableField("FEV1_VCmax")
    private Double fev1Vcmax;

    @ApiModelProperty("最大呼气量")
    @TableField("VCmax")
    private Double vcmax;

    @ApiModelProperty("结论")
    @TableField("conclusion")
    private String conclusion;
}
