package org.harvey.respiratory.cloud.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 充值
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-14 23:54
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("充值")
public class RechargeDto implements Serializable {
    @ApiModelProperty("充值的查询依据")
    private QueryBalanceDto queryBalanceDto;
    @ApiModelProperty(value = "充值金额, 单位, 分, 必正", required = true)
    private Integer amount;
}
