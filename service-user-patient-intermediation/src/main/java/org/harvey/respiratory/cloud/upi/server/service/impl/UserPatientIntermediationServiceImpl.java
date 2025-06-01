package org.harvey.respiratory.cloud.upi.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.harvey.respiratory.cloud.api.service.UserPatientIntermediationService;
import org.harvey.respiratory.cloud.common.pojo.entity.UserPatientIntermediation;
import org.harvey.respiratory.cloud.upi.server.dao.UserPatientIntermediationMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:39
 * @see UserPatientIntermediation
 * @see UserPatientIntermediationMapper
 * @see UserPatientIntermediationService
 */
@Service
@Slf4j
@DubboService
public class UserPatientIntermediationServiceImpl extends
        ServiceImpl<UserPatientIntermediationMapper, UserPatientIntermediation> implements
        UserPatientIntermediationService {

    @Override
    public boolean existRelation(long userId, long patientId) {
        return super.lambdaQuery()
                .eq(UserPatientIntermediation::getUserId, userId)
                .and(w -> w.eq(UserPatientIntermediation::getPatientId, patientId))
                .oneOpt()
                .isPresent();
    }

    @Override
    public boolean register(long userId, long patientId) {
        boolean saved = super.save(new UserPatientIntermediation(userId, patientId));
        if (saved) {
            log.debug("保存用户{}-患者{},成功", userId, patientId);
        } else {
            log.warn("保存用户{}-患者{},失败", userId, patientId);
        }
        return saved;
    }

    @Override
    public boolean delete(long patientId, long userId) {
        boolean removed = super.remove(
                new LambdaQueryWrapper<UserPatientIntermediation>().eq(UserPatientIntermediation::getUserId, userId)
                        .and(w -> w.eq(UserPatientIntermediation::getPatientId, patientId)));
        if (removed) {
            log.debug("删除用户{}-患者{}记录,成功", userId, patientId);
        } else {
            log.warn("删除用户{}-患者{}记录,失败", userId, patientId);
        }
        return removed;
    }

    @Override
    @NonNull
    public List<Long> queryPatientOnUser(long userId) {
        return super.lambdaQuery()
                .select(UserPatientIntermediation::getPatientId)
                .eq(UserPatientIntermediation::getUserId, userId)
                .list()
                .stream()
                .map(UserPatientIntermediation::getPatientId)
                .collect(Collectors.toList());
    }

    @Override
    public @NonNull List<Long> queryPatientOnUser(long userId, Page<UserPatientIntermediation> page) {
        // TODO TEST
        return super.lambdaQuery()
                .select(UserPatientIntermediation::getPatientId)
                .eq(UserPatientIntermediation::getUserId, userId)
                .page(page)
                .getRecords()
                .stream()
                .map(UserPatientIntermediation::getPatientId)
                .collect(Collectors.toList());
    }
}