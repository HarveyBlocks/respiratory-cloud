package org.harvey.respiratory.cloud.sp.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.MedicalProviderService;
import org.harvey.respiratory.cloud.api.service.SymptomaticPresentationService;
import org.harvey.respiratory.cloud.api.service.UserSecurityService;
import org.harvey.respiratory.cloud.api.service.VisitDoctorService;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserSecurityDto;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProvider;
import org.harvey.respiratory.cloud.common.pojo.entity.SymptomaticPresentation;
import org.harvey.respiratory.cloud.common.pojo.entity.VisitDoctor;
import org.harvey.respiratory.cloud.sp.server.dao.SymptomaticPresentationMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:39
 * @see SymptomaticPresentation
 * @see SymptomaticPresentationMapper
 * @see SymptomaticPresentationService
 */
@Service
@Slf4j
@org.apache.dubbo.config.annotation.DubboService
public class SymptomaticPresentationServiceImpl extends
        ServiceImpl<SymptomaticPresentationMapper, SymptomaticPresentation> implements SymptomaticPresentationService {

    @DubboReference
    private VisitDoctorService visitDoctorService;
    @DubboReference
    private MedicalProviderService medicalProviderService;
    @DubboReference
    private UserSecurityService userSecurityService;

    private static SymptomaticPresentationService currentProxy() {
        return (SymptomaticPresentationService) AopContext.currentProxy();
    }

    @Override
    public void logicDelete(Long targetId, String currentUserIdentityCardId) {
        SymptomaticPresentation symptomaticPresentation;
        try {


            symptomaticPresentation = queryByIdIgnoreDeleted(targetId);
        } catch (ResourceNotFountException e) {
            log.warn("用户{}想删除不存在的{}", currentUserIdentityCardId, targetId);
            return;
        }
        if (symptomaticPresentation.getDeleted()) {
            return;
        }
        validToWrite(currentUserIdentityCardId, symptomaticPresentation.getVisitDoctorId());
        // 可以执行
        updateDeletedColumn(targetId);
    }

    @Override
    @Transactional
    public void logicUpdate(Long oldId, SymptomaticPresentation newData) {
        boolean updated = updateDeletedColumn(oldId);
        if (updated) {
            saveNewData(newData);
        } else {
            // 如果更新失败了, 那是因为已经被删除了, 所以不更新新data
            throw new BadRequestException("数据已经被其他请求更新, 请重新更新");
        }
    }

    private boolean updateDeletedColumn(Long targetId) {
        // 真正执行删除操作, 其实是update
        // 允许多次删除
        boolean update = super.lambdaUpdate()
                .set(SymptomaticPresentation::getDeleted, true)
                .eq(SymptomaticPresentation::getId, targetId)
                .eq(SymptomaticPresentation::getDeleted, false)
                .update();
        if (update) {
            log.debug("成功更新对症状{}的逻辑删除", targetId);
        } else {
            log.debug("删除对症状{}的逻辑删除失败, 未知原因, 可能已经被删除了", targetId);
        }
        return update;
    }

    private void saveNewData(SymptomaticPresentation newData) {
        boolean saved = super.save(newData);
        if (saved) {
            log.debug("新增症状成功");
        } else {
            log.warn("新增症状失败");
        }
    }

    @Override
    @NonNull
    public Long updateRetainTrace(
            String currentUserIdentityCardId, Long oldVersionId, SymptomaticPresentation newData) {
        SymptomaticPresentation old;
        try {
            old = queryById(oldVersionId);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("更新不存在的字段", e.getCause());
        }
        if (old.getDeleted()) {
            throw new BadRequestException("更新已删除的字段");
        }
        Long visitDoctorId = old.getVisitDoctorId();
        if (newData.getVisitDoctorId() == null) {
            newData.setVisitDoctorId(visitDoctorId);
        }
        if (!newData.getVisitDoctorId().equals(old.getVisitDoctorId())) {
            throw new UnauthorizedException("不允许更换有关的就诊");
        }
        validToWrite(currentUserIdentityCardId, visitDoctorId);
        newData.updateFromOldVersionIgnoreNull(old);
        // 可以执行
        currentProxy().logicUpdate(old.getId(), newData);
        return newData.getId();
    }

    @Override
    @NonNull
    public SymptomaticPresentation queryByIdIgnoreDeleted(Long oldVersionId) {
        SymptomaticPresentation presentation = super.getById(oldVersionId);
        if (presentation == null) {
            throw new BadRequestException("symptomatic presentation by id");
        }
        return presentation;
    }

    @Override
    @NonNull
    public SymptomaticPresentation queryById(Long id) {
        SymptomaticPresentation one = super.lambdaQuery()
                .eq(SymptomaticPresentation::getId, id)
                .eq(SymptomaticPresentation::getDeleted, false)
                .one();
        if (one == null) {
            throw new BadRequestException("can not find symptomatic presentation by " + id);
        }
        return one;
    }

    /**
     * 校验能否写这个数据->当前这个用户填写的这份就诊记录->可, 否则不可
     *
     * @param currentUserIdentityCardId 当前用户的身份证
     * @param visitDoctorId             当前症状所在的
     */
    private void validToWrite(String currentUserIdentityCardId, Long visitDoctorId) {
        VisitDoctor visitDoctor = visitDoctorService.getById(visitDoctorId);
        Long medicalProviderId = visitDoctor.getMedicalProviderId();
        MedicalProvider medicalProvider = medicalProviderService.getById(medicalProviderId);
        String identityCardId = medicalProvider.getIdentityCardId();
        UserSecurityDto userSecurity = userSecurityService.selectByIdentityCardId(identityCardId);
        if (!Objects.equals(userSecurity.getIdentityCardId(), currentUserIdentityCardId)) {
            // 没有权限
            throw new UnauthorizedException("不是本症状的提交者, 无权限");
        }
    }

    @Override
    @NonNull
    @Transactional
    public List<Long> saveSymptomaticPresentationBatch(List<SymptomaticPresentation> presentationList) {
        // 初始化一下, 防止奇奇怪怪的字段
        presentationList.forEach(SymptomaticPresentation::resetForNewInsert);
        boolean saved = super.saveBatch(presentationList);
        if (saved) {
            log.debug("新增症状成功");
        } else {
            log.warn("新增症状失败");
        }
        return presentationList.stream().map(SymptomaticPresentation::getId).collect(Collectors.toList());
    }


    @Override
    @NonNull
    public List<SymptomaticPresentation> selectByVisitId(long visitId) {
        return super.lambdaQuery()
                .eq(SymptomaticPresentation::getVisitDoctorId, visitId)
                .eq(SymptomaticPresentation::getDeleted, false)
                .list();
    }


}