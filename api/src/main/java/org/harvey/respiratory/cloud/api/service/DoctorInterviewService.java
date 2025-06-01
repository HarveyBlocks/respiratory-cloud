package org.harvey.respiratory.cloud.api.service;


import org.harvey.respiratory.cloud.common.pojo.dto.InterviewDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.ExpenseRecord;
import org.harvey.respiratory.cloud.common.pojo.entity.SpecificUsingDrugRecord;
import org.harvey.respiratory.cloud.common.pojo.entity.SymptomaticPresentation;
import org.harvey.respiratory.cloud.common.pojo.entity.VisitDoctor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 医生问诊
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-21 15:27
 */
public interface DoctorInterviewService {
    void interview(InterviewDto interviewDto, UserDto user);

    @Transactional
    void transitionallySaveRecords(
            VisitDoctor newData, List<ExpenseRecord> expenseRecordList,
            List<SpecificUsingDrugRecord> usingDrugRecords,
            Map<Integer, Integer> drugIdToDepleteCountMap, List<SymptomaticPresentation> symptomaticPresentationList,
            List<Integer> diseaseIds);
}
