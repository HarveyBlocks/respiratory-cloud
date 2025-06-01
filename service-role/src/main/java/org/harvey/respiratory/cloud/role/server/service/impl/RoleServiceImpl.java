package org.harvey.respiratory.cloud.role.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.MedicalProviderJobService;
import org.harvey.respiratory.cloud.api.service.MedicalProviderService;
import org.harvey.respiratory.cloud.api.service.RoleService;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProvider;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProviderJob;
import org.harvey.respiratory.cloud.common.pojo.entity.RoleEntity;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;
import org.harvey.respiratory.cloud.common.utils.identifier.IdentifierIdPredicate;
import org.harvey.respiratory.cloud.role.server.dao.RoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-16 20:28
 * @see RoleEntity
 * @see RoleMapper
 * @see RoleService
 */
@org.apache.dubbo.config.annotation.DubboService
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {
    @Resource
    private IdentifierIdPredicate predicate;
    @DubboReference
    private MedicalProviderService medicalProviderService;
    @DubboReference
    private MedicalProviderJobService medicalProviderJobService;

    /**
     * 只有服务器内部调用
     * 1. 先依据身份证, 如果是未实名, 就是Unknown, 否则进入2
     * 2. medicalProvider查询, 如果有, 则从job中取出role
     * 3. 其他权限表, 给开发者用的, 暂且没有这一选项
     */
    @Override
    @NonNull
    public Role queryRole(String identityCardId) {
        // 1. 判断身份证有效
        if (identityCardId == null || !predicate.test(identityCardId)) {
            return Role.UNKNOWN;
        }
        // 2. 从医生处查询
        try {
            MedicalProvider medicalProvider = medicalProviderService.selectByIdentityCardId(identityCardId);
            return getMedicalProviderRole(medicalProvider.getJobId());
        } catch (ResourceNotFountException e) {
            return Role.PATIENT;
        }

    }

    @Override
    @NonNull
    public Role getMedicalProviderRole(int jobId) {
        MedicalProviderJob job = medicalProviderJobService.getById(jobId);
        if (job == null) {
            throw new ServerException("can not find job witch register in medical provider.");
        }
        int roleId = job.getRoleId();
        return selectRole(roleId);
    }

    @Override
    @NonNull
    public Role selectRole(int roleId) {
        // TODO
        return Role.create(roleId);
    }
}