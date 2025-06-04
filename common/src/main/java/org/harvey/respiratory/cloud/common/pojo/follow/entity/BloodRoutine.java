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
@TableName("tb_blood_routine")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("血常规表")
public class BloodRoutine implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("白细胞计数")
    @TableField("white_blood_cell_count")
    private Integer whiteBloodCellCount;

    @ApiModelProperty("中性粒细胞计数")
    @TableField("neutrophil_count")
    private Integer neutrophilCount;

    @ApiModelProperty("淋巴细胞计数")
    @TableField("lymphocyte_count")
    private Integer lymphocyteCount;

    @ApiModelProperty("嗜酸性粒细胞计数")
    @TableField("eosinophil_count")
    private Integer eosinophilCount;

    @ApiModelProperty("中性粒细胞百分比")
    @TableField("neutrophil_percentage")
    private Double neutrophilPercentage;

    @ApiModelProperty("淋巴细胞百分比")
    @TableField("percentage_lymphocytes")
    private Double percentageLymphocytes;

    @ApiModelProperty("嗜酸性粒细胞百分比")
    @TableField("percentage_eosinophils")
    private Double percentageEosinophils;

    @ApiModelProperty("血红蛋白")
    @TableField("hemoglobin")
    private String hemoglobin;

    @ApiModelProperty("红细胞计数")
    @TableField("red_blood_cell_count")
    private Integer redBloodCellCount;

    @ApiModelProperty("血小板")
    @TableField("platelets")
    private String platelets;

    @ApiModelProperty("C-反应蛋白")
    @TableField("c_reactive_protein")
    private Double cReactiveProtein;

}
