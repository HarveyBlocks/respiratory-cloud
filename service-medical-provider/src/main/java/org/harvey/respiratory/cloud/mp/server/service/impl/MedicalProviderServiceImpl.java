package org.harvey.respiratory.cloud.mp.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.MedicalProviderService;
import org.harvey.respiratory.cloud.api.service.UserSecurityService;
import org.harvey.respiratory.cloud.common.exception.DaoException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserSecurityDto;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProvider;
import org.harvey.respiratory.cloud.mp.server.dao.MedicalProviderMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:38
 * @see MedicalProvider
 * @see MedicalProviderMapper
 * @see MedicalProviderService
 */
@Service
@org.apache.dubbo.config.annotation.DubboService
public class MedicalProviderServiceImpl extends ServiceImpl<MedicalProviderMapper, MedicalProvider> implements
        MedicalProviderService {
    @DubboReference
    private UserSecurityService userSecurityService;

    @Override
    @NonNull
    public Long register(MedicalProvider medicalProvider) {
        boolean saved = super.save(medicalProvider);
        if (saved) {
            log.debug("保存医疗提供者成功");
        } else {
            throw new DaoException(DaoException.Operation.SAVE_FAIL, "保存医疗提供者失败, 未知原因");
        }
        return medicalProvider.getId();
    }

    @Override
    public void update(MedicalProvider medicalProvider) {
        boolean updated = super.updateById(medicalProvider);
        if (updated) {
            log.debug("更新医疗提供者成功");
        } else {
            throw new DaoException(DaoException.Operation.UPDATE_FAIL, "更新医疗提供者失败, 未知原因");
        }
    }

    @Override
    public void delete(long id) {
        boolean removed = super.removeById(id);
        if (removed) {
            log.debug("删除医疗提供者成功");
        } else {
            throw new DaoException(DaoException.Operation.UPDATE_FAIL, "删除医疗提供者失败, 不存在的id");
        }
    }

    @Override
    @NonNull
    public MedicalProvider selectByUser(long userId) {
        UserSecurityDto userSecurity = userSecurityService.selectById(userId);
        if (userSecurity.getIdentityCardId() == null) {
            throw new UnauthorizedException("不能查询医疗提供者信息");
        }
        MedicalProvider one = super.lambdaQuery()
                .eq(MedicalProvider::getIdentityCardId, userSecurity.getIdentityCardId())
                .one();
        if (one == null) {
            throw new ResourceNotFountException("not found medical provider: " + userId);
        }
        return one;
    }

    @Override
    @NonNull
    public MedicalProvider selectByIdentityCardId(String identityCardId) {
        MedicalProvider one = super.lambdaQuery().eq(MedicalProvider::getIdentityCardId, identityCardId).one();
        if (one == null) {
            throw new ResourceNotFountException("no medical provider of: " + identityCardId);
        }
        return one;
    }

    @Override
    @NonNull
    public MedicalProvider selectByPhone(String phoneNumber) {
        UserSecurityDto userSecurity = userSecurityService.selectByPhone(phoneNumber);
        String identityCardId = userSecurity.getIdentityCardId();
        if (identityCardId == null) {
            // 没有被实名的用户, 其实一定不是医生了
            throw new UnauthorizedException("未实名一定不存在这位医生");
        }
        return this.selectByIdentityCardId(identityCardId);
    }

    @Override
    @NonNull
    public List<MedicalProvider> selectByAny(String name, Integer formId, Page<MedicalProvider> page) {
        return super.lambdaQuery()
                .eq(formId != null, MedicalProvider::getFormId, formId)
                .like(name != null && !name.isEmpty(), MedicalProvider::getName, name)
                .page(page)
                .getRecords();
    }

    @Override
    public boolean samePeople(long id, String identityCardId) {
        return super.lambdaQuery()
                .eq(MedicalProvider::getId, id)
                .eq(MedicalProvider::getIdentityCardId, identityCardId)
                .oneOpt().isPresent();
    }

    @Override
    @NonNull
    public MedicalProvider queryById(long medicalProviderId) {
        MedicalProvider one = super.lambdaQuery().eq(MedicalProvider::getId, medicalProviderId).one();
        if (one == null) {
            throw new ResourceNotFountException("no medical provider of: " + medicalProviderId);
        }
        return one;
    }
}