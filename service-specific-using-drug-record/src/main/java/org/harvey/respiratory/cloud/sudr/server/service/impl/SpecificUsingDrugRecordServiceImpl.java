package org.harvey.respiratory.cloud.sudr.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.*;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.DrugDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Drug;
import org.harvey.respiratory.cloud.common.pojo.entity.SpecificUsingDrugRecord;
import org.harvey.respiratory.cloud.common.pojo.entity.VisitDoctor;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;
import org.harvey.respiratory.cloud.common.pojo.vo.RangeDate;
import org.harvey.respiratory.cloud.sudr.server.dao.SpecificUsingDrugRecordMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:38
 * @see SpecificUsingDrugRecord
 * @see SpecificUsingDrugRecordMapper
 * @see SpecificUsingDrugRecordService
 */
@Service
@Slf4j
@org.apache.dubbo.config.annotation.DubboService
public class SpecificUsingDrugRecordServiceImpl extends
        ServiceImpl<SpecificUsingDrugRecordMapper, SpecificUsingDrugRecord> implements SpecificUsingDrugRecordService {

    @DubboReference
    private VisitDoctorService visitDoctorService;
    @DubboReference
    private MedicalProviderService medicalProviderService;

    @DubboReference
    private DrugService drugService;
    @DubboReference
    private UserPatientIntermediationService userPatientIntermediationService;

    @NonNull
    private static SpecificUsingDrugRecordService currentProxy() {
        return (SpecificUsingDrugRecordService) AopContext.currentProxy();
    }

    @NonNull
    private static Set<Integer> mapDrugIds(List<SpecificUsingDrugRecord> records) {
        return records.stream().map(SpecificUsingDrugRecord::getDrugId).collect(Collectors.toSet());
    }

    @NonNull
    private static List<DrugDto> setDto(List<SpecificUsingDrugRecord> records, Map<Integer, Drug> drugMap) {
        return records.stream()
                .map(record -> new DrugDto(record, drugMap.get(record.getDrugId())))
                .collect(Collectors.toList());
    }

    @Override
    public void validOnWrite(UserDto user, Long visitDoctorId) {
        Role role = preCheckThenGetRole(user);
        if (visitDoctorId == null) {
            throw new BadRequestException("需要问诊号");
        }
        switch (role) {
            case UNKNOWN:
            case PATIENT:
            case MEDICATION_DOCTOR:
                throw new UnauthorizedException("无权限使用此功能");
            case NORMAL_DOCTOR:
                long medicalProviderId;
                try {
                    medicalProviderId = visitDoctorService.queryMedicalProviderId(visitDoctorId);
                } catch (ResourceNotFountException e) {
                    throw new ResourceNotFountException("不存在的visit doctor id");
                }
                boolean samePeople = medicalProviderService.samePeople(medicalProviderId, user.getIdentityCardId());
                if (!samePeople) {
                    throw new UnauthorizedException("无权限使用此功能");
                }
            case CHARGE_DOCTOR:
            case DEVELOPER:
            case DATABASE_ADMINISTRATOR:
                // 直接过
                break;
            default:
                throw new ServerException("Unexpected role value: " + role);
        }

    }

    @Override
    public void validOnVisitRead(UserDto user, Long visitId) {
        // 0. 高级用户直接过
        Role role = preCheckThenGetRole(user);
        // 1. 当前的用户是医生->是医生进入2, 否则进入4
        // 2. 是这次问诊的医生->进入3, 否则进入4(退化成病人)
        // 3. 可以查询
        // 4. 用户和这次问诊的病患有关联->进入5, 否则进入6
        // 5. 可以查询
        // 5. 不可以查询
        if (visitId == null) {
            throw new BadRequestException("需要问诊号");
        }
        VisitDoctor visitDoctor = null;
        switch (role) {
            case UNKNOWN:
                throw new UnauthorizedException("无权限使用此功能");
            case NORMAL_DOCTOR:
                try {
                    visitDoctor = visitDoctorService.queryMedicalProviderAndPatientId(visitId);
                } catch (ResourceNotFountException e) {
                    throw new ResourceNotFountException("不存在的visit doctor id");
                }
                boolean samePeople = medicalProviderService.samePeople(
                        visitDoctor.getMedicalProviderId(), user.getIdentityCardId());
                if (samePeople) {
                    break;
                }
                // 否则, 退化成病人
            case MEDICATION_DOCTOR: // 医药医生在这个业务中不具备医生的权限, 退化成病人
            case PATIENT:
                if (visitDoctor == null) {
                    try {
                        visitDoctor = visitDoctorService.queryMedicalProviderAndPatientId(visitId);
                    } catch (ResourceNotFountException e) {
                        throw new ResourceNotFountException("不存在的visit doctor id", e);
                    }
                }
                boolean exist = userPatientIntermediationService.existRelation(
                        user.getId(), visitDoctor.getPatientId());
                if (!exist) {
                    // 无关联则没有权限
                    throw new UnauthorizedException("没有权限访问其他患者的信息");
                }
                // 有关联
            case CHARGE_DOCTOR:
            case DEVELOPER:
            case DATABASE_ADMINISTRATOR:
                // 直接过
                break;
            default:
                throw new ServerException("Unexpected role value: " + role);
        }

    }

    @Override
    public void validOnPatientRead(UserDto user, Long patientId) {
        Role role = preCheckThenGetRole(user);
        switch (role) {
            case UNKNOWN:
            case MEDICATION_DOCTOR:
                throw new UnauthorizedException("无权限使用此功能");
            case PATIENT:
                // 需要检查有关联的用户
                boolean exist = userPatientIntermediationService.existRelation(user.getId(), patientId);
                if (!exist) {
                    // 无关联则没有权限
                    throw new UnauthorizedException("没有权限访问其他患者的信息");
                }
            case NORMAL_DOCTOR:
            case CHARGE_DOCTOR:
            case DEVELOPER:
            case DATABASE_ADMINISTRATOR:
                // 直接过
                break;
            default:
                throw new ServerException("Unexpected role value: " + role);
        }
    }

    @NonNull
    private Role preCheckThenGetRole(UserDto user) {
        if (user == null) {
            throw new UnauthorizedException("未登录无权限使用此功能");
        }
        String identityCardId = user.getIdentityCardId();
        if (identityCardId == null) {
            throw new UnauthorizedException("未实名无权限使用此功能");
        }
        return user.getRole();
    }

    @Override
    public void logicDelete(long id) {
        // 允许重复删除
        updateDeletedColumn(id);
    }

    @Override
    public void logicDelete(long visitDoctorId, long drugId) {
        // 允许重复删除
        boolean removed = super.lambdaUpdate()
                .set(SpecificUsingDrugRecord::getDeleted, true) // 删除了
                .eq(SpecificUsingDrugRecord::getVisitDoctorId, visitDoctorId)
                .eq(SpecificUsingDrugRecord::getDrugId, drugId)
                .update();
        if (removed) {
            log.debug("成功逻辑删除问诊号{}, 药品号{}的记录", visitDoctorId, drugId);
        } else {
            log.warn("逻辑删除问诊号{}, 药品号{}的记录失败", visitDoctorId, drugId);
        }
    }

    @Override
    public long updateRetainTrace(UserDto user, long oldVersionId, SpecificUsingDrugRecord newData) {
        SpecificUsingDrugRecord old = queryById(oldVersionId);
        validOnWrite(user, old.getVisitDoctorId());
        newData.updateFromOldVersionIgnoreNull(old);
        currentProxy().logicUpdate(oldVersionId, newData);
        return newData.getId();
    }

    @Override
    @Transactional
    public void logicUpdate(long oldId, SpecificUsingDrugRecord newData) {
        boolean updated = updateDeletedColumn(oldId);
        if (updated) {
            saveNewData(newData);
        } else {
            // 如果更新失败了, 那是因为已经被删除了, 所以不更新新data
            throw new BadRequestException("数据已经被其他请求更新, 请重新更新");
        }
    }

    private boolean updateDeletedColumn(long targetId) {
        // 真正执行删除操作, 其实是update
        // 允许多次删除
        boolean update = super.lambdaUpdate()
                .set(SpecificUsingDrugRecord::getDeleted, true)
                .eq(SpecificUsingDrugRecord::getId, targetId)
                .eq(SpecificUsingDrugRecord::getDeleted, false)
                .update();
        if (update) {
            log.debug("成功更新对药物使用{}的逻辑删除", targetId);
        } else {
            log.debug("删除对药物使用{}的逻辑删除失败, 未知原因, 可能已经被删除了", targetId);
        }
        return update;
    }

    private void saveNewData(SpecificUsingDrugRecord newData) {
        boolean saved = super.save(newData);
        if (saved) {
            log.debug("新增症状失败");
        } else {
            log.debug("新增症状成功");
        }
    }

    @Override
    @NonNull
    public SpecificUsingDrugRecord queryByIdIgnoreDeleted(Long id) {
        SpecificUsingDrugRecord record = super.getById(id);
        if (record == null) {
            throw new ResourceNotFountException("specific using drug record by id: " + id);
        }
        return record;
    }

    @Override
    @NonNull
    public SpecificUsingDrugRecord queryById(Long id) {
        SpecificUsingDrugRecord one = super.lambdaQuery()
                .eq(SpecificUsingDrugRecord::getId, id)
                .eq(SpecificUsingDrugRecord::getDeleted, false)
                .one();
        if (one == null) {
            throw new ResourceNotFountException("can not found by id: " + id);
        }
        return one;
    }

    @Override
    @NonNull
    public List<DrugDto> queryDrugInVisit(long visitId) {
        List<SpecificUsingDrugRecord> records = super.lambdaQuery()
                .eq(SpecificUsingDrugRecord::getVisitDoctorId, visitId)
                .eq(SpecificUsingDrugRecord::getDeleted, false)
                .list();
        Set<Integer> drugIds = mapDrugIds(records);
        Map<Integer, Drug> drugMap = drugService.queryByIds(drugIds);
        return setDto(records, drugMap);
    }

    @Override
    @NonNull
    public List<DrugDto> queryHistoryDrugUsingByName(long patientId, String name) {
        List<SpecificUsingDrugRecord> records = super.lambdaQuery()
                .eq(SpecificUsingDrugRecord::getPatientId, patientId)
                .eq(SpecificUsingDrugRecord::getDeleted, false)
                .list();
        Set<Integer> drugIds = mapDrugIds(records);
        Map<Integer, Drug> drugMap = drugService.queryByIdsFilterName(drugIds, name);
        return setDto(records, drugMap);
    }

    @Override
    @NonNull
    public List<DrugDto> queryHistoryDrugUsingByDate(long patientId, RangeDate rangeDate) {
        // record.start>rangeDate.start and record.end<rangeDate.end
        List<SpecificUsingDrugRecord> records = super.lambdaQuery()
                .eq(SpecificUsingDrugRecord::getPatientId, patientId)
                .gt(rangeDate.getStart() != null, SpecificUsingDrugRecord::getTreatStart, rangeDate.getStart())
                .lt(rangeDate.getEnd() != null, SpecificUsingDrugRecord::getTreatEnd, rangeDate.getEnd())
                .eq(SpecificUsingDrugRecord::getDeleted, false)
                .list();
        Set<Integer> drugIds = mapDrugIds(records);
        Map<Integer, Drug> drugMap = drugService.queryByIds(drugIds);
        return setDto(records, drugMap);
    }

    @Override
    @NonNull
    public List<Long> saveSymptomaticPresentationBatch(List<SpecificUsingDrugRecord> recordList) {
        // 初始化一下, 防止奇奇怪怪的字段
        recordList.forEach(SpecificUsingDrugRecord::resetForNewInsert);
        boolean saved = super.saveBatch(recordList);
        if (saved) {
            log.debug("新增药品使用成功");
        } else {
            log.warn("新增药品使用失败");
        }
        return recordList.stream().map(SpecificUsingDrugRecord::getId).collect(Collectors.toList());
    }

}