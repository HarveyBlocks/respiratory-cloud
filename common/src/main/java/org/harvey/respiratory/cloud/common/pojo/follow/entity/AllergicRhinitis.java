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
import org.harvey.respiratory.cloud.common.pojo.follow.enums.Seasonal;
import org.harvey.respiratory.cloud.common.pojo.follow.enums.SeverityLevel;

import java.io.Serializable;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 14:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_allergic_rhinitis")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("过敏性鼻炎表")
public class AllergicRhinitis implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("严重程度")
    @TableField("severity_level")
    private SeverityLevel severityLevel;

    @ApiModelProperty("季节性")
    @TableField("seasonal")
    private Seasonal seasonal;
}
