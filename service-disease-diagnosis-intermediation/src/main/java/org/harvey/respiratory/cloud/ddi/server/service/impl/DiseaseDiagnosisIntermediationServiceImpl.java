package org.harvey.respiratory.cloud.ddi.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.api.service.DiseaseDiagnosisIntermediationService;
import org.harvey.respiratory.cloud.common.pojo.entity.DiseaseDiagnosisIntermediation;
import org.harvey.respiratory.cloud.ddi.server.dao.DiseaseDiagnosisIntermediationMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 疾病诊断中间表
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:21
 */
@Service
@org.apache.dubbo.config.annotation.DubboService
@Slf4j
public class DiseaseDiagnosisIntermediationServiceImpl extends
        ServiceImpl<DiseaseDiagnosisIntermediationMapper, DiseaseDiagnosisIntermediation> implements
        DiseaseDiagnosisIntermediationService {

    @Override
    @NonNull
    public List<Integer> selectDiseaseByVisit(long visitId) {
        return super.lambdaQuery()
                .select(DiseaseDiagnosisIntermediation::getDiseaseId)
                .eq(DiseaseDiagnosisIntermediation::getVisitDoctorId, visitId)
                .list()
                .stream()
                .map(DiseaseDiagnosisIntermediation::getDiseaseId)
                .collect(Collectors.toList());
    }

    @Override
    public void saveOnInterview(long visitDoctorId, List<Integer> diseaseIds) {
        List<DiseaseDiagnosisIntermediation> diagnosisList = diseaseIds.stream()
                .map(id -> new DiseaseDiagnosisIntermediation(visitDoctorId, id))
                .collect(Collectors.toList());
        boolean saved = super.saveBatch(diagnosisList);
        if (saved) {
            log.debug("成功保存{}诊断的疾病", visitDoctorId);
        } else {
            log.debug("保存{}诊断的疾病失败", visitDoctorId);
        }
    }


}
