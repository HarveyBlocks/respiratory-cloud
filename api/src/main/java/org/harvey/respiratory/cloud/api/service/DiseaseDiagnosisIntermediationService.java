package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.entity.DiseaseDiagnosisIntermediation;

import java.util.List;

/**
 * 疾病
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface DiseaseDiagnosisIntermediationService extends IService<DiseaseDiagnosisIntermediation> {

    @NonNull
    List<Integer> selectDiseaseByVisit(long visitId);

    /**
     * 校验由外界决定
     */
    void saveOnInterview(long visitDoctorId, List<Integer> diseaseIds);

}
