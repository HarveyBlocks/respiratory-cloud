package org.harvey.respiratory.cloud.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 医疗科室
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-14 00:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_medical_provider_department")
@NoArgsConstructor
@ApiModel("随访信息")
@AllArgsConstructor
public class MedicalProviderDepartment implements Serializable {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "科室名")
    private String name;
    /**
     * 外面的大类, null for 没有外面的大类了
     */
    @ApiModelProperty(value = "外面的大类, null for 没有外面的大类了")
    private Integer outerDepartment;
    /**
     * 单位, 分, 不能为null
     */
    @ApiModelProperty(value = "单位, 分, 不能为null")
    private Integer expenseEveryVisit;

    @ApiModelProperty(value = "描述, varchar(255)")
    private String description;

}
