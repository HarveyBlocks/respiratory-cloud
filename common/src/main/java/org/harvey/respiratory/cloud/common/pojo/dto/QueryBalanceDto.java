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
@ApiModel("充值的查询依据")
public class QueryBalanceDto implements Serializable {
    @ApiModelProperty("或医保id,或医保号,或病患号, 或身份证, 四选一, 医保id优先级最高")
    private Long healthcareId;
    @ApiModelProperty("或医保id,或医保号,或病患号, 或身份证, 四选一, 医保号次之")
    private String healthcareCode;
    @ApiModelProperty("或医保id,或医保号,或病患id, 或身份证, 四选一, 病患id再次之")
    private Long patientId;
    @ApiModelProperty("或医保id,或医保号,或病患id, 或身份证, 四选一, 身份证优先级最低")
    private String identifierCardId;
}
