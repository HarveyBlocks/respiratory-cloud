package org.harvey.respiratory.cloud.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 包含药物具体信息和药物使用方法-医生开的
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-14 21:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = " 抓药信息. 包含药物具体信息和药物使用方法-医生开的, 方便药师抓药")
public class FillDrugDto implements Serializable {
    @ApiModelProperty("药物名称(varchar(63))")
    private String name;
    @ApiModelProperty("药物规格(varchar(63))")
    private String specification;
    @ApiModelProperty("药品数量, 和抓药数量相关")
    private Integer count;

}
