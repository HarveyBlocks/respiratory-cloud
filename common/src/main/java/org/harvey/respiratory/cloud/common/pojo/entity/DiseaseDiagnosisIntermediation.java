package org.harvey.respiratory.cloud.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 疾病诊断中间表, 不适合作为Model
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-12 23:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_disease_diagnosis_intermediation")
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseDiagnosisIntermediation implements Serializable {
    /**
     * 就诊号/就诊表id
     */
    private Long visitDoctorId;
    /**
     * 疾病表id
     */
    private Integer diseaseId;
}
