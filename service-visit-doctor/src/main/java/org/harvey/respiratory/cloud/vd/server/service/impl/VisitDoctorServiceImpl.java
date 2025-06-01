package org.harvey.respiratory.cloud.vd.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.harvey.respiratory.cloud.api.service.MedicalProviderService;
import org.harvey.respiratory.cloud.api.service.UserPatientIntermediationService;
import org.harvey.respiratory.cloud.api.service.VisitDoctorService;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProvider;
import org.harvey.respiratory.cloud.common.pojo.entity.VisitDoctor;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;
import org.harvey.respiratory.cloud.common.pojo.vo.RangeDate;
import org.harvey.respiratory.cloud.vd.server.dao.VisitDoctorMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:39
 * @see VisitDoctor
 * @see VisitDoctorMapper
 * @see VisitDoctorService
 */
@Service
@Slf4j
@DubboService
public class VisitDoctorServiceImpl extends ServiceImpl<VisitDoctorMapper, VisitDoctor> implements VisitDoctorService {
    @DubboReference
    private MedicalProviderService medicalProviderService;
    @DubboReference
    private UserPatientIntermediationService userPatientIntermediationService;

    @Override
    @NonNull
    public Long queryMedicalProviderId(long visitDoctorId) {
        return super.lambdaQuery()
                .select(VisitDoctor::getMedicalProviderId)
                .eq(VisitDoctor::getId, visitDoctorId)
                .one()
                .getMedicalProviderId();
    }

    @Override
    @NonNull
    public VisitDoctor queryMedicalProviderAndPatientId(long visitId) {
        return super.lambdaQuery()
                .select(VisitDoctor::getMedicalProviderId)
                .select(VisitDoctor::getPatientId)
                .eq(VisitDoctor::getId, visitId)
                .one();
    }

    @Override
    @NonNull
    public VisitDoctor querySimplyById(long visitDoctorId) {
        VisitDoctor one = super.lambdaQuery().eq(VisitDoctor::getId, visitDoctorId).one();
        if (one == null) {
            throw new ResourceNotFountException("can not find visit doctor by: " + visitDoctorId);
        }
        return one;
    }

    @Override
    public void updateAfterInterview(VisitDoctor visitDoctor) {
        boolean update = super.updateById(visitDoctor);
        if (update) {
            log.debug("更新{}成功", visitDoctor.getId());
        } else {
            log.warn("更新{}失败", visitDoctor.getId());
        }
    }

    @Override
    @NonNull
    public Long createVisitDoctorId(long patientId, long medicalProviderId) {
        VisitDoctor takeTheNumber = VisitDoctor.takeTheNumber(patientId, medicalProviderId);
        takeTheNumber.setVisitTime(LocalDateTime.now());
        super.save(takeTheNumber);
        return takeTheNumber.getId();
    }

    @Override
    @NonNull
    public List<VisitDoctor> doctorQuery(String identityCardId, RangeDate rangeDate, Page<VisitDoctor> page) {
        MedicalProvider medicalProvider = medicalProviderService.selectByIdentityCardId(identityCardId);
        return super.lambdaQuery()
                .eq(VisitDoctor::getMedicalProviderId, medicalProvider.getId())
                .gt(rangeDate.getStart() != null, VisitDoctor::getVisitTime, rangeDate.getStart())
                .lt(rangeDate.getEnd() != null, VisitDoctor::getVisitTime, rangeDate.getEnd())
                .page(page)
                .getRecords();
    }

    @Override
    @NonNull
    public List<VisitDoctor> patientQuery(long currentUserId, RangeDate rangeDate, Page<VisitDoctor> page) {
        List<Long> patientIds = userPatientIntermediationService.queryPatientOnUser(currentUserId);
        return super.lambdaQuery()
                .in(VisitDoctor::getPatientId, patientIds)
                .gt(rangeDate.getStart() != null, VisitDoctor::getVisitTime, rangeDate.getStart())
                .lt(rangeDate.getEnd() != null, VisitDoctor::getVisitTime, rangeDate.getEnd())
                .page(page)
                .getRecords();
    }

    @Override
    public void queryValid(UserDto currentUser, long visitId) {
        Role role = currentUser.getRole();
        switch (role) {
            case UNKNOWN:
                throw new UnauthorizedException("无权限执行");
            case PATIENT:
            case NORMAL_DOCTOR:
            case MEDICATION_DOCTOR:
                try {
                    this.getIfRelatedOnPatient(currentUser.getId(), visitId);
                } catch (ResourceNotFountException e) {
                    throw new UnauthorizedException("不能访问与自己无关的问诊信息", e);
                }
                break;
            case CHARGE_DOCTOR:
            case DEVELOPER:
            case DATABASE_ADMINISTRATOR:
                // 直接同行
                break;
            default:
                throw new ServerException("Unexpected role value: " + role);
        }
    }

    @NonNull
    private VisitDoctor getIfRelatedOnPatient(long userId, long visitId) {
        List<Long> patientIds = userPatientIntermediationService.queryPatientOnUser(userId);
        if (patientIds.isEmpty()) {
            throw new ResourceNotFountException("不能找到id信息");
        }
        VisitDoctor one = super.lambdaQuery()
                .eq(VisitDoctor::getId, visitId)
                .in(VisitDoctor::getPatientId, patientIds)
                .one();
        if (one == null) {
            throw new ResourceNotFountException("不能通过id " + visitId + " 和当前用户持有斌换找到信息");
        }
        return one;
    }


}