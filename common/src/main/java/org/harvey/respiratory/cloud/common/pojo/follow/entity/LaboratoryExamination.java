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
import org.harvey.respiratory.cloud.common.pojo.follow.enums.SkinPrickTest;

import java.io.Serializable;
import java.sql.Date;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 14:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_laboratory_examination")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("实验室检查表")
public class LaboratoryExamination implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("检查项目名称")
    @TableField("check_project_name")
    private String checkProjectName;

    @ApiModelProperty("检查日期")
    @TableField("inspection_date")
    private Date inspectionDate;

    @ApiModelProperty("检查值")
    @TableField("check_value")
    private String checkValue;

    @ApiModelProperty("血常规id")
    @TableField("blood_routine_id")
    private Integer bloodRoutineId;

    @ApiModelProperty("过敏原测试id")
    @TableField("allergen_test_id")
    private Integer allergenTestId;

    @ApiModelProperty("皮肤点刺试验")
    @TableField("skin_prick_test")
    private SkinPrickTest skinPrickTest;

    @ApiModelProperty("痰嗜酸性粒细胞计数")
    @TableField("sputum_eosinophil_count")
    private Integer sputumEosinophilCount;

    @ApiModelProperty("总IgE值")
    @TableField("total_igE_value")
    private Integer totalIgEValue;
}
