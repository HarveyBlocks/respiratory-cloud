package org.harvey.respiratory.cloud.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.harvey.respiratory.cloud.common.pojo.entity.Drug;
import org.harvey.respiratory.cloud.common.pojo.entity.SpecificUsingDrugRecord;

import java.io.Serializable;

/**
 * 药品信息
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-20 23:01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("药物信息")
public class DrugDto implements Serializable {
    @ApiModelProperty("用药指南")
    private SpecificUsingDrugRecord specificUsingDrugRecord;
    @ApiModelProperty("具体药物")
    private Drug drug;
}
