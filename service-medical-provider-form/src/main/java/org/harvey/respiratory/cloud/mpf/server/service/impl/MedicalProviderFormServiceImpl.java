package org.harvey.respiratory.cloud.mpf.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.MedicalProviderFormService;
import org.harvey.respiratory.cloud.api.service.MedicalProviderService;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.DaoException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProvider;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProviderForm;
import org.harvey.respiratory.cloud.mpf.server.dao.MedicalProviderFormMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:37
 * @see MedicalProviderForm
 * @see MedicalProviderFormMapper
 * @see MedicalProviderFormService
 */
@org.apache.dubbo.config.annotation.DubboService
@Service
public class MedicalProviderFormServiceImpl extends
        ServiceImpl<MedicalProviderFormMapper, MedicalProviderForm> implements MedicalProviderFormService {

    @DubboReference
    private MedicalProviderService medicalProviderService;

    @Override
    @NonNull
    public Integer register(MedicalProviderForm form) {
        boolean saved = super.save(form);
        if (saved) {
            log.debug("成功保存");
            return form.getId();
        } else {
            throw new DaoException(DaoException.Operation.SAVE_FAIL, "未能保存机构, 未知原因");
        }
    }

    @Override
    public void update(MedicalProviderForm newForm) {
        boolean updated = super.updateById(newForm);
        if (updated) {
            log.debug("更新成功");
        } else {
            throw new DaoException(DaoException.Operation.UPDATE_FAIL, "未能更新机构, 未知原因");
        }
    }

    @Override
    public void delete(long id) {
        boolean deleted = super.removeById(id);
        if (deleted) {
            log.debug("删除成功");
        } else {
            throw new DaoException(DaoException.Operation.DELETE_FAIL, "未能删除机构, 未知原因");
        }
    }

    @Override
    @NonNull
    public MedicalProviderForm querySelf(long userId) {
        MedicalProvider medicalProvider;
        try {
            medicalProvider = medicalProviderService.selectByUser(userId);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("不能" + userId + "通过的身份证查询到医疗提供者信息", e);
        }
        Integer formId = medicalProvider.getFormId();
        return queryById(formId);
    }

    @Override
    @NonNull
    public List<MedicalProviderForm> queryAny(Page<MedicalProviderForm> page) {
        Page<MedicalProviderForm> formPage = super.lambdaQuery().page(page);
        return formPage.getRecords();
    }

    @Override
    @NonNull
    public MedicalProviderForm queryById(int formId) {
        MedicalProviderForm one = super.getById(formId);
        if (one == null) {
            throw new ResourceNotFountException("不能通过id " + formId + " 查询到医疗提供机构的信息");
        }
        return one;
    }

    @Override
    @NonNull
    public List<MedicalProviderForm> queryByName(String name) {
        if (name.isEmpty()) {
            throw new BadRequestException("机构退化成全查, 这不好");
        }
        return super.lambdaQuery().like(MedicalProviderForm::getName, name).list();
    }

    @Override
    @NonNull
    public List<MedicalProviderForm> queryByAddress(String address) {
        if (address.isEmpty()) {
            throw new BadRequestException("机构退化成全查, 这不好");
        }
        return super.lambdaQuery().like(MedicalProviderForm::getAddress, address).list();
    }
}