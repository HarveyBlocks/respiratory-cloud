package org.harvey.respiratory.cloud.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 付费
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-21 23:13
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("充值")
public class PayDto implements Serializable {
    @ApiModelProperty("充值的查询依据")
    private QueryBalanceDto queryBalanceDto;
    @ApiModelProperty(value = "需要被付款的就诊id", required = true)
    private Long visitId;
}
