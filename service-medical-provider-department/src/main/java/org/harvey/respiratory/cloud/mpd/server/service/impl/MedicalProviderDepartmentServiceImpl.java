package org.harvey.respiratory.cloud.mpd.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.MedicalProviderDepartmentService;
import org.harvey.respiratory.cloud.api.service.MedicalProviderService;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.DaoException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProvider;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProviderDepartment;
import org.harvey.respiratory.cloud.mpd.server.dao.MedicalProviderDepartmentMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:37
 * @see MedicalProviderDepartment
 * @see MedicalProviderDepartmentMapper
 * @see MedicalProviderDepartmentService
 */
@org.apache.dubbo.config.annotation.DubboService
@Service
public class MedicalProviderDepartmentServiceImpl extends
        ServiceImpl<MedicalProviderDepartmentMapper, MedicalProviderDepartment> implements
        MedicalProviderDepartmentService {
    @DubboReference
    private MedicalProviderService medicalProviderService;

    @Override
    @NonNull
    public Integer register(MedicalProviderDepartment department) {
        boolean saved = super.save(department);
        if (saved) {
            log.debug("成功科室保存");
            return department.getId();
        } else {
            throw new DaoException(DaoException.Operation.SAVE_FAIL, "未能保存科室, 未知原因");
        }
    }

    @Override
    public void update(MedicalProviderDepartment newDepartment) {
        boolean updated = super.updateById(newDepartment);
        if (updated) {
            log.debug("更新科室成功");
        } else {
            throw new DaoException(DaoException.Operation.UPDATE_FAIL, "未能更新, 未找到目标科室");
        }
    }

    @Override
    public void delete(long id) {
        boolean deleted = super.removeById(id);
        if (deleted) {
            log.debug("删除科室成功");
        } else {
            throw new DaoException(DaoException.Operation.DELETE_FAIL, "未能删除科室, 未找到目标科室");
        }
    }

    @Override
    @NonNull
    public MedicalProviderDepartment querySelf(long userId) {
        MedicalProvider medicalProvider;
        try {
            medicalProvider = medicalProviderService.selectByUser(userId);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("不能" + userId + "通过的身份证查询到医疗提供者信息", e);
        }
        Integer departmentId = medicalProvider.getDepartmentId();
        return queryById(departmentId);
    }

    @Override
    @NonNull
    public List<MedicalProviderDepartment> queryAny(Page<MedicalProviderDepartment> page) {
        Page<MedicalProviderDepartment> departmentPage = super.lambdaQuery().page(page);
        return departmentPage.getRecords();
    }

    @Override
    @NonNull
    public MedicalProviderDepartment queryById(int departmentId) {
        MedicalProviderDepartment one = super.getById(departmentId);
        if (one == null) {
            throw new ResourceNotFountException("不能通过id " + departmentId + " 查询到医疗提供科室的信息");
        }
        return one;
    }

    @Override
    @NonNull
    public List<MedicalProviderDepartment> queryByName(String name) {
        if (name.isEmpty()) {
            throw new BadRequestException("科室退化成全查, 这不好");
        }
        // TODO 还得是倒排索引
        return super.lambdaQuery().like(MedicalProviderDepartment::getName, name).list();
    }

}