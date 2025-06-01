package org.harvey.respiratory.cloud.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;

import java.io.Serializable;
import java.util.Date;

/**
 * 特定药品使用方式中间表
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-12 23:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_specific_using_drug_record")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("具体药物使用记录")
public class SpecificUsingDrugRecord implements Serializable {
    @ApiModelProperty
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 就诊号/就诊表id
     */
    @ApiModelProperty(value = "就诊号/就诊表id, 和 drug id 一起是联合唯一键", required = true)
    private Long visitDoctorId;

    @ApiModelProperty(value = "药品表id, 和 visit doctor id 一起是联合唯一键", required = true)
    private Integer drugId;

    @ApiModelProperty(value = "药品数量, 和费用计算相关")
    private Integer count;

    @ApiModelProperty(value = "患者表id(冗余, 为了提高查询效率)")
    private Long patientId;

    @ApiModelProperty(value = "使用剂量(varchar(63)), 由于单位不确定, 所以是字符串")
    private String dosageUsed;

    @ApiModelProperty(value = "使用天数(int)")
    private Integer dayTimeUsed;

    @ApiModelProperty(value = " 使用频率(varchar(63)), 同样不知道单位")
    private String frequencyUsed;

    @ApiModelProperty(value = "治疗开始时间(date)")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date treatStart;

    @ApiModelProperty(value = "治疗结束时间(date)")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date treatEnd;


    @ApiModelProperty(value = "其他用药指导(TEXT)")
    private String otherMedicationGuidance;
    /**
     * 逻辑删除字段, 不给用户显现
     */
    @ApiModelProperty(value = "逻辑删除字段", hidden = true)
    @JsonIgnore
    private Boolean deleted;

    @ApiModelProperty(value = "指向老版本, 默认为null, 表示不指向, 初版", hidden = true)
    @JsonIgnore
    private Long oldVersionId;

    public void resetForNewInsert() {
        this.deleted = false;
        this.id = null;
        this.oldVersionId = null;
    }

    public void updateFromOldVersionIgnoreNull(SpecificUsingDrugRecord old) {
        // 强制一致
        if (this.getVisitDoctorId() == null || this.getVisitDoctorId().equals(old.getVisitDoctorId())) {
            this.setVisitDoctorId(old.getVisitDoctorId());
        } else {
            throw new UnauthorizedException("visit doctor id, 不能修改的字段");
        }
        if (this.getDrugId() == null || this.getDrugId().equals(old.getDrugId())) {
            this.setDrugId(old.getDrugId());
        } else {
            throw new UnauthorizedException("drug id, 不能修改的字段");
        }
        if (this.getPatientId() == null || this.getPatientId().equals(old.getPatientId())) {
            this.setPatientId(old.getPatientId());
        } else {
            throw new UnauthorizedException("patient id, 不能修改的字段");
        }
        if (this.getCount() == null || this.getCount().equals(old.getCount())) {
            this.setCount(old.getCount());
        } else {
            throw new UnauthorizedException("count, 不能修改的字段");
        }
        if (this.getDosageUsed() == null) {
            this.setDosageUsed(old.getDosageUsed());
        }
        if (this.getDayTimeUsed() == null) {
            this.setDayTimeUsed(old.getDayTimeUsed());
        }
        if (this.getFrequencyUsed() == null) {
            this.setFrequencyUsed(old.getFrequencyUsed());
        }
        if (this.getTreatStart() == null) {
            this.setTreatStart(old.getTreatStart());
        }
        if (this.getTreatEnd() == null) {
            this.setTreatEnd(old.getTreatEnd());
        }
        if (this.getOtherMedicationGuidance() == null) {
            this.setOtherMedicationGuidance(old.getOtherMedicationGuidance());
        }
        resetForNewInsert();
        this.setOldVersionId(old.getId());
    }
}
