package org.harvey.respiratory.cloud.fh.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.DiseaseService;
import org.harvey.respiratory.cloud.api.service.FamilyHistoryService;
import org.harvey.respiratory.cloud.api.service.UserPatientIntermediationService;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.FamilyHistory;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;
import org.harvey.respiratory.cloud.fh.server.dao.FamilyHistoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:36
 * @see FamilyHistory
 * @see FamilyHistoryMapper
 * @see FamilyHistoryService
 */
@Service
@Slf4j
@org.apache.dubbo.config.annotation.DubboService
public class FamilyHistoryServiceImpl extends ServiceImpl<FamilyHistoryMapper, FamilyHistory> implements
        FamilyHistoryService {

    @DubboReference
    private UserPatientIntermediationService userPatientIntermediationService;
    @DubboReference
    private DiseaseService diseaseService;

    /**
     * 病患只能添加自己的
     * 医生可以都过
     */
    @Override
    public void validRoleToRegister(UserDto currentUser, Long patientId) {
        if (currentUser == null) {
            throw new UnauthorizedException("登录后可使用");
        }
        if (patientId == null) {
            throw new BadRequestException("需要有关病患id");
        }
        Role role = currentUser.getRole();
        // 病患只能添加自己的
        // 医生可以都过
        switch (role) {
            case UNKNOWN:
                throw new UnauthorizedException("你还未实名, 没有权限");
            case PATIENT:
                boolean exist = userPatientIntermediationService.existRelation(currentUser.getId(), patientId);
                if (!exist) {
                    throw new UnauthorizedException("不能修改未在自己账号处注册的用户的家族史");
                }
                return;
            case NORMAL_DOCTOR:
            case MEDICATION_DOCTOR:
            case CHARGE_DOCTOR:
            case DEVELOPER:
            case DATABASE_ADMINISTRATOR:
                break;
            default:
                throw new ServerException("Unexpected role value: " + role);
        }

    }

    @Override
    @NonNull
    public Long register(FamilyHistory familyHistory) {
        Long patientId = familyHistory.getPatientId();
        if (patientId == null) {
            throw new BadRequestException("需要病患id");
        }

        // 关系校验成功, 可以注册
        boolean saved = super.save(familyHistory);
        if (saved) {
            log.debug("保存患者{}的{}家族史成功", patientId, familyHistory.getFamilyRelationshipId());
        } else {
            log.warn("保存患者{}的{}家族史失败", patientId, familyHistory.getFamilyRelationshipId());
        }
        return familyHistory.getId();
    }

    @Override
    @NonNull
    public List<FamilyHistory> queryByPatient(long patientId, Page<FamilyHistory> page) {
        return super.lambdaQuery().eq(FamilyHistory::getPatientId, patientId).page(page).getRecords();
    }

    @Override
    @NonNull
    public List<FamilyHistory> queryByDiseaseName(long patientId, String diseaseName, Page<FamilyHistory> page) {
        List<Integer> ids = diseaseService.queryIdsByName(diseaseName);
        return super.lambdaQuery()
                .eq(FamilyHistory::getPatientId, patientId)
                .in(FamilyHistory::getDiseaseId, ids)
                .page(page)
                .getRecords();
    }

    @Override
    @NonNull
    public List<FamilyHistory> queryByDisease(long patientId, int diseaseId, Page<FamilyHistory> page) {
        return super.lambdaQuery()
                .eq(FamilyHistory::getPatientId, patientId)
                .eq(FamilyHistory::getDiseaseId, diseaseId)
                .page(page)
                .getRecords();
    }

    @Override
    @NonNull
    public List<FamilyHistory> queryByRelationship(
            long patientId, List<Integer> relationshipIds, Page<FamilyHistory> page) {
        boolean relationshipIdsValid = relationshipIds != null && !relationshipIds.isEmpty();
        return super.lambdaQuery()
                .eq(FamilyHistory::getPatientId, patientId)
                .in(relationshipIdsValid, FamilyHistory::getFamilyRelationshipId, relationshipIds)
                .page(page)
                .getRecords();
    }


}