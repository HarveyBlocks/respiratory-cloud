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
@TableName("tb_tidal_lung_function_test")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("潮气量肺功能测试表")
public class TidalLungFunctionTest implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("潮气量kg")
    @TableField("WT")
    private Double wt;

    @ApiModelProperty("呼吸频率次/分")
    @TableField("RR")
    private Double rr;

    @ApiModelProperty("吸气时间")
    @TableField("Ti")
    private Double ti;

    @ApiModelProperty("呼气时间")
    @TableField("Te")
    private Double te;

    @ApiModelProperty("吸气/呼气时间比")
    @TableField("Ti_Te")
    private Double tiTe;

    @ApiModelProperty("总时间/呼气时间比")
    @TableField("TPTEF")
    private Double tptef;

    @ApiModelProperty("有效时间/呼气时间比")
    @TableField("VPTEF")
    private Double vptef;

    @ApiModelProperty("总时间/呼气时间比")
    @TableField("TPTEF_TE")
    private Double tptefTe;

    @ApiModelProperty("有效时间/有效呼气量比")
    @TableField("VPEF_VE")
    private Double vpefVe;

    @ApiModelProperty("平均时间/呼气时间比")
    @TableField("PTEF")
    private Double ptef;

    @ApiModelProperty("50%有效时间/50%吸气时间比")
    @TableField("TEF50_TIF50")
    private Double tef50Tif50;

    @ApiModelProperty("50%有效时间")
    @TableField("TEF50")
    private Double tef50;

    @ApiModelProperty("25%有效时间")
    @TableField("TEF25")
    private Double tef25;

    @ApiModelProperty("25%有效时间/75%有效时间比")
    @TableField("TEF25_75")
    private Double tef2575;

}
