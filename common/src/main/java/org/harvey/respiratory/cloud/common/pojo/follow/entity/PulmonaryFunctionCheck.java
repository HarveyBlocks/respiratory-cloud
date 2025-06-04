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
import org.harvey.respiratory.cloud.common.pojo.follow.enums.AntigenTest;

import java.io.Serializable;
import java.sql.Date;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 14:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_pulmonary_function_check")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("肺功能测试表")
public class PulmonaryFunctionCheck implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("检查名称")
    @TableField("check_names")
    private String checkNames;

    @ApiModelProperty("检查日期")
    @TableField("check_date")
    private Date checkDate;

    @ApiModelProperty("检查值")
    @TableField("check_value")
    private String checkValue;

    @ApiModelProperty("肺功能测试id")
    @TableField("pulmonary_function_test_id")
    private Integer pulmonaryFunctionTestId;

    @ApiModelProperty("支气管扩张试验")
    @TableField("bronchial_dilation_test")
    private AntigenTest bronchialDilationTest;

    @ApiModelProperty("支气管激发试验")
    @TableField("bronchial_provocation_test")
    private AntigenTest bronchialProvocationTest;

    @ApiModelProperty("潮气量肺功能测试id")
    @TableField("tidal_lung_function_test_id")
    private Integer tidalLungFunctionTestId;
}
