package org.harvey.respiratory.cloud.mpj.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.MedicalProviderJobService;
import org.harvey.respiratory.cloud.api.service.MedicalProviderService;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.DaoException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProvider;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProviderJob;
import org.harvey.respiratory.cloud.mpj.server.dao.MedicalProviderJobMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:38
 * @see MedicalProviderJob
 * @see MedicalProviderJobMapper
 * @see MedicalProviderJobService
 */
@org.apache.dubbo.config.annotation.DubboService
@Service
public class MedicalProviderJobServiceImpl extends ServiceImpl<MedicalProviderJobMapper, MedicalProviderJob> implements
        MedicalProviderJobService {
    @DubboReference
    private MedicalProviderService medicalProviderService;

    @Override
    @NonNull
    public Integer register(MedicalProviderJob job) {
        boolean saved = super.save(job);
        if (saved) {
            log.debug("成功保存职业");
            return job.getId();
        } else {
            throw new DaoException(DaoException.Operation.SAVE_FAIL, "未能保存职业, 未知原因");
        }
    }

    @Override
    public void update(MedicalProviderJob newJob) {
        boolean updated = super.updateById(newJob);
        if (updated) {
            log.debug("更新职业成功");
        } else {
            throw new DaoException(DaoException.Operation.UPDATE_FAIL, "未能更新职业, 未知原因");
        }
    }

    @Override
    public void delete(long id) {
        boolean deleted = super.removeById(id);
        if (deleted) {
            log.debug("删除职业成功");
        } else {
            //  其实可能是因为并发环境下同时删除产生的重复删的问题
            throw new DaoException(DaoException.Operation.DELETE_FAIL, "未能删除职业, 未知原因");
        }
    }

    @Override
    @NonNull
    public MedicalProviderJob querySelf(long userId) {
        MedicalProvider medicalProvider;
        try {
            medicalProvider = medicalProviderService.selectByUser(userId);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("不能" + userId + "通过的身份证查询到医疗提供者信息");
        }
        Integer jobId = medicalProvider.getJobId();
        return queryById(jobId);
    }

    @Override
    @NonNull
    public List<MedicalProviderJob> queryAny(Page<MedicalProviderJob> page) {
        Page<MedicalProviderJob> jobPage = super.lambdaQuery().page(page);
        return jobPage.getRecords();
    }

    @Override
    @NonNull
    public MedicalProviderJob queryById(int jobId) {
        MedicalProviderJob one = super.getById(jobId);
        if (one == null) {
            throw new ResourceNotFountException("不能通过id " + jobId + " 查询到医疗提供职业的信息");
        }
        return one;
    }

    @Override
    @NonNull
    public List<MedicalProviderJob> queryByName(String name) {
        if (name.isEmpty()) {
            throw new BadRequestException("职业退化成全查, 这不好");
        }
        return super.lambdaQuery().like(MedicalProviderJob::getName, name).list();
    }

}