package org.harvey.respiratory.cloud.common.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.harvey.respiratory.cloud.common.constants.ConstantsInitializer;
import org.harvey.respiratory.cloud.common.pojo.entity.SpecificUsingDrugRecord;
import org.harvey.respiratory.cloud.common.pojo.entity.SymptomaticPresentation;
import org.harvey.respiratory.cloud.common.pojo.entity.VisitDoctor;
import org.harvey.respiratory.cloud.common.pojo.enums.Severity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 问诊一次会上传的信息
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-14 13:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "医生问诊信息")
public class InterviewDto implements Serializable {

    @ApiModelProperty(value = "就诊号/就诊表id", required = true)
    private Long visitDoctorId;
    @ApiModelProperty("简要介绍(varchar(255)), 如果医生不提供, 就是病症名的list")
    private String briefDescription;
    @ApiModelProperty("其他辅助治疗")
    private String otherAdjuvantTherapy;

    @ApiModelProperty(value = "症状表, 可以为empty, 不可为null", required = true)
    private List<SymptomaticPresentationDto> symptomaticPresentationList;

    @ApiModelProperty(value = "药物具体使用表, 可以为empty, 不可为null", required = true)
    private List<SpecificUsingDrugRecordDto> specificUsingDrugRecordDtoList;

    @ApiModelProperty(value = "疾病诊断, 可以为empty, 不可为null", required = true)
    private List<Integer> diseaseIds;

    public static String joinBriefDescription(List<String> diseaseNames) {
        return String.join(",", diseaseNames);
    }

    /**
     * 生成visit doctor 实体
     */
    public VisitDoctor buildVisitDoctor(String briefDescription, int totalPrice) {
        // 已知总费用, 就诊时间就是执行当前业务的时间
        return new VisitDoctor(visitDoctorId, null, null, briefDescription, otherAdjuvantTherapy, LocalDateTime.now(),
                totalPrice, true, false, null
        );
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "药物具体使用")
    public static class SpecificUsingDrugRecordDto {
        @ApiModelProperty("药品表id, 指向药品表中的药品")
        private Integer drugId;

        @ApiModelProperty("药品数量, 和费用计算相关")
        private Integer count;

        @ApiModelProperty("使用剂量(varchar(63)), 由于单位不确定, 所以是字符串")
        private String dosageUsed;

        @ApiModelProperty("使用天数(int)")
        private Integer dayTimeUsed;

        @ApiModelProperty("使用频率(varchar(63)), 同样不知道单位")
        private String frequencyUsed;

        @ApiModelProperty("治疗开始时间(date)")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private Date treatStart;

        @ApiModelProperty("治疗结束时间(date)")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private Date treatEnd;


        @ApiModelProperty("其他用药指导(TEXT)")
        private String otherMedicationGuidance;


        public SpecificUsingDrugRecord buildSpecificUsingDrugRecord(long visitDoctorId, long patientId) {
            return new SpecificUsingDrugRecord(null, visitDoctorId, drugId, count, patientId, dosageUsed, dayTimeUsed,
                    frequencyUsed, treatStart, treatEnd, otherMedicationGuidance, false, null
            );
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "症状")
    public static class SymptomaticPresentationDto {
        @ApiModelProperty("名称(varchar(63))")
        private String name;

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

        public SymptomaticPresentation buildSymptomaticPresentation(long visitDoctorId) {
            return new SymptomaticPresentation(null, visitDoctorId, name, severity, frequency, startTime, incentive,
                    environmentalFactor, signDescription, description,
                    ConstantsInitializer.nowDateTime(), false, null
            );
        }
    }
}
