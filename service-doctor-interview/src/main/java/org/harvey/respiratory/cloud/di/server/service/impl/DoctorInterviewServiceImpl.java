package org.harvey.respiratory.cloud.di.server.service.impl;

import lombok.NonNull;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.*;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.InterviewDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.*;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 医生就诊
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-21 15:28
 */
@Service
@org.apache.dubbo.config.annotation.DubboService
public class DoctorInterviewServiceImpl implements DoctorInterviewService {

    @DubboReference
    private DiseaseService diseaseService;
    @DubboReference
    private VisitDoctorService visitDoctorService;

    @DubboReference
    private DrugService drugService;

    @DubboReference
    private MedicalProviderService medicalProviderService;
    @DubboReference
    private MedicalProviderJobService medicalProviderJobService;
    @DubboReference
    private MedicalProviderDepartmentService medicalProviderDepartmentService;
    @DubboReference
    private ExpenseRecordService expenseRecordService;

    @DubboReference
    private SpecificUsingDrugRecordService specificUsingDrugRecordService;

    @DubboReference
    private SymptomaticPresentationService symptomaticPresentationService;

    @DubboReference
    private DiseaseDiagnosisIntermediationService diseaseDiagnosisIntermediationService;

    private static DoctorInterviewService currentProxy() {
        return (DoctorInterviewService) AopContext.currentProxy();
    }

    /**
     * 映射症状dto->entity
     */
    @NonNull
    private static List<SymptomaticPresentation> getSymptomaticPresentationList(
            List<InterviewDto.SymptomaticPresentationDto> symptomaticPresentationDtoList, Long visitDoctorId) {
        if (symptomaticPresentationDtoList == null) {
            throw new BadRequestException("symptomaticPresentationList 不能为null");
        }
        return symptomaticPresentationDtoList.stream()
                .map(sp -> sp.buildSymptomaticPresentation(visitDoctorId))
                .collect(Collectors.toList());
    }

    /**
     * 生成药物具体使用的记录, 数据库entity
     */
    @NonNull
    private static List<SpecificUsingDrugRecord> getSpecificUsingDrugRecords(
            List<InterviewDto.SpecificUsingDrugRecordDto> usingDrugDto, Long visitDoctorId, Long patientId) {
        return usingDrugDto.stream()
                .map(dto -> dto.buildSpecificUsingDrugRecord(visitDoctorId, patientId))
                .collect(Collectors.toList());
    }

    /**
     * 将所有费用记录相加, 获取总费用
     */
    private static int summarizeExpense(List<ExpenseRecord> expenseRecordList) {
        return (int) expenseRecordList.stream()
                .collect(Collectors.summarizingInt(r -> r.getAmount() * r.getCount()))
                .getSum();
    }

    @NonNull
    private static Map<Integer, Integer> mapDrugIdToDepleteCount(List<SpecificUsingDrugRecord> usingDrugRecords) {
        // 完成从药品id到drug的消耗的映射
        return usingDrugRecords.stream()
                .collect(Collectors.toMap(SpecificUsingDrugRecord::getDrugId, SpecificUsingDrugRecord::getCount));
    }

    @Override
    public void interview(InterviewDto interviewDto, UserDto user) {
        Long visitDoctorId = interviewDto.getVisitDoctorId();
        List<ExpenseRecord> expenseRecordList = new ArrayList<>();
        if (visitDoctorId == null) {
            throw new BadRequestException("需要visit doctor id");
        }
        VisitDoctor inDb = visitDoctorService.querySimplyById(visitDoctorId);
        if (inDb.isInterviewed()) {
            // 已经完成了问诊了
            throw new BadRequestException("请不要重复提交问诊结果");
        }
        // 获取数据库中病患id和医生id
        Long patientId = inDb.getPatientId();
        Long medicalProviderId = inDb.getMedicalProviderId();
        // 查询出就诊医生的具体信息, 医生职位和所在科室有关费用
        MedicalProvider medicalProvider = medicalProviderService.queryById(medicalProviderId);
        // 这个工作一定要问诊的这个医生做
        validInterviewRole(medicalProvider.getIdentityCardId(), user);
        // 添加医生的有关的费用
        createMedicalProviderExpenseRecord(medicalProvider, visitDoctorId, expenseRecordList);
        // 映射症状dto->entity
        List<SymptomaticPresentation> symptomaticPresentationList = getSymptomaticPresentationList(
                interviewDto.getSymptomaticPresentationList(), visitDoctorId);
        // 取出药物使用dto
        List<InterviewDto.SpecificUsingDrugRecordDto> usingDrugDto = interviewDto.getSpecificUsingDrugRecordDtoList();
        if (usingDrugDto == null) {
            throw new BadRequestException("usingDrugDto 不能为null");
        }
        // 从药物使用中取出药物id,查询出药物单价, 与药物数量相乘, 并生成费用记录
        createDrugExpenseRecord(usingDrugDto, visitDoctorId, expenseRecordList);
        // 生成药物具体使用的记录, 数据库entity
        List<SpecificUsingDrugRecord> usingDrugRecords = getSpecificUsingDrugRecords(
                usingDrugDto, visitDoctorId, patientId);
        // 需要扣除的库存, drug-count
        Map<Integer, Integer> drugIdToDepleteCountMap = mapDrugIdToDepleteCount(usingDrugRecords);
        // 将所有费用记录相加, 获取总费用
        int totalPrice = summarizeExpense(expenseRecordList);
        // 如果没有概述, 就依据确证的病症生成概述
        String briefDescription = getBriefDescription(interviewDto);
        // 生成visit doctor 实体
        VisitDoctor visitDoctor = interviewDto.buildVisitDoctor(briefDescription, totalPrice);
        // 药品ids
        List<Integer> diseaseIds = interviewDto.getDiseaseIds();
        // 接下来是事务, 插入
        currentProxy().transitionallySaveRecords(visitDoctor, expenseRecordList, usingDrugRecords,
                drugIdToDepleteCountMap, symptomaticPresentationList, diseaseIds
        );
    }

    /**
     * 这个工作一定要问诊的这个医生做
     */
    private void validInterviewRole(String medicalProviderIdentityCardId, UserDto userDto) {
        if (medicalProviderIdentityCardId.equals(userDto.getIdentityCardId())) {
            // 指定的医生, 直接过
            return;
        }
        Role role = userDto.getRole();
        switch (role) {
            case UNKNOWN:
                throw new UnauthorizedException("未知用户");
            case PATIENT:
                throw new UnauthorizedException("患者没有添加信息的权限");
            case MEDICATION_DOCTOR:
            case NORMAL_DOCTOR:
                throw new UnauthorizedException("只有此次问诊的目标医生有权限进行问诊");
            case CHARGE_DOCTOR: // 专家, 也能问诊
            case DEVELOPER:
            case DATABASE_ADMINISTRATOR:
                break;
            default:
                throw new ServerException("Unexpected role value: " + role);
        }

    }

    /**
     * 添加医生的有关的费用
     */
    private void createMedicalProviderExpenseRecord(
            MedicalProvider medicalProvider, Long visitDoctorId, List<ExpenseRecord> expenseRecordList) {
        // 5.2 计算此次问诊的医生费用, 并生成费用记录
        Integer jobId = medicalProvider.getJobId();
        MedicalProviderJob job = medicalProviderJobService.queryById(jobId);
        ExpenseRecord expenseRecordOnJob = new ExpenseRecord(null, visitDoctorId, "医生职位问诊费用",
                job.getExpenseEveryVisit(), 1, job.getName()
        );
        expenseRecordList.add(expenseRecordOnJob);
        Integer departmentId = medicalProvider.getDepartmentId();
        MedicalProviderDepartment department = medicalProviderDepartmentService.queryById(departmentId);
        ExpenseRecord expenseRecordOnDepartment = new ExpenseRecord(null, visitDoctorId, "医生科室问诊费用",
                department.getExpenseEveryVisit(), 1, department.getName()
        );
        expenseRecordList.add(expenseRecordOnDepartment);
    }

    /**
     * 从药物使用中取出药物id,查询出药物单价, 与药物数量相乘, 并生成费用记录
     */
    private void createDrugExpenseRecord(
            List<InterviewDto.SpecificUsingDrugRecordDto> usingDrugDto,
            Long visitDoctorId,
            List<ExpenseRecord> expenseRecordList) {
        Set<Integer> drugIds = usingDrugDto.stream()
                .map(InterviewDto.SpecificUsingDrugRecordDto::getDrugId)
                .collect(Collectors.toSet());
        Map<Integer, Drug> drugMap = drugService.queryByIds(drugIds);
        for (InterviewDto.SpecificUsingDrugRecordDto dto : usingDrugDto) {
            // 从药物使用中取出药物id, 查询出药物单价, 与药物数量相乘, 并生成费用记录
            Integer drugId = dto.getDrugId();
            Integer count = dto.getCount();
            Drug drug = drugMap.get(drugId);
            ExpenseRecord expenseRecordOnDrug = new ExpenseRecord(null, visitDoctorId, "药品费用",
                    drug.getExpenseEach(), count, drug.getName()
            );
            expenseRecordList.add(expenseRecordOnDrug);
        }
    }

    /**
     * 如果没有概述, 就依据确证的病症生成概述
     */
    @NonNull
    private String getBriefDescription(InterviewDto interviewDto) {
        String briefDescription = interviewDto.getBriefDescription();
        if (briefDescription != null) {
            return briefDescription;
        }
        // 查询疾病名字, 然后自动生成
        List<String> diseaseNames = diseaseService.queryDiseaseNameByIds(interviewDto.getDiseaseIds());
        return InterviewDto.joinBriefDescription(diseaseNames);
    }

    @Transactional
    @Override
    public void transitionallySaveRecords(
            VisitDoctor newData,
            List<ExpenseRecord> expenseRecordList,
            List<SpecificUsingDrugRecord> usingDrugRecords,
            Map<Integer, Integer> drugIdToDepleteCountMap,
            List<SymptomaticPresentation> symptomaticPresentationList,
            List<Integer> diseaseIds) {
        VisitDoctor oldData = visitDoctorService.querySimplyById(newData.getId());
        if (oldData.isInterviewed()) {
            // 另一条线程已经完成, 故跳出
            return;
        }
        // 依据id更新数据库就诊信息, visit doctor 实体
        visitDoctorService.updateAfterInterview(newData);
        // 插入费用记录
        expenseRecordService.saveOnInterview(expenseRecordList);
        // 插入具体药物使用usingDrugRecords(多个)
        specificUsingDrugRecordService.saveSymptomaticPresentationBatch(usingDrugRecords);
        // 损耗药品
        drugService.deplete(drugIdToDepleteCountMap);
        // 依次插入症状信息(多个)
        symptomaticPresentationService.saveSymptomaticPresentationBatch(symptomaticPresentationList);
        // 插入疾病-就诊中间表(多个)
        diseaseDiagnosisIntermediationService.saveOnInterview(newData.getId(), diseaseIds);
    }

}
