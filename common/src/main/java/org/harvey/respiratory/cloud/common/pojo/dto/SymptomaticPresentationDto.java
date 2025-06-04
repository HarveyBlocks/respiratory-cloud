package org.harvey.respiratory.cloud.common.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.harvey.respiratory.cloud.common.pojo.entity.SymptomaticPresentation;
import org.harvey.respiratory.cloud.common.pojo.entity.SymptomaticPresentationDetail;
import org.harvey.respiratory.cloud.common.pojo.enums.Severity;
import org.harvey.respiratory.cloud.common.pojo.enums.SymptomaticPresentationType;

import java.io.Serializable;
import java.util.Date;


/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-03 22:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("症状表现")
public class SymptomaticPresentationDto implements Serializable {
    @ApiModelProperty("症状具体, 具体症状而不是症状表现!")
    private String detailName;
    @ApiModelProperty("症状具体的类型")
    private SymptomaticPresentationType type;

    @ApiModelProperty("严重程度(enum-轻度/中度/重度)")
    private Severity severity;

    @ApiModelProperty("频率(varchar(63)), 由于不知道单位")
    private String frequency;

    @ApiModelProperty("开始时间(date)")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("诱因(varchar(63))")
    private String incentive;

    @ApiModelProperty("环境因素(varchar(63))")
    private String environmentalFactor;

    @ApiModelProperty("体征描述(varchar(63))")
    private String signDescription;

    @ApiModelProperty("描述(TEXT)")
    private String description;

    public SymptomaticPresentationDto(
            SymptomaticPresentation entity, SymptomaticPresentationDetail detail) {
        this(detail.getName(), detail.getType(), entity.getSeverity(),
                entity.getFrequency(), entity.getStartTime(), entity.getIncentive(), entity.getEnvironmentalFactor(),
                entity.getSignDescription(), entity.getDescription()
        );
    }
}
