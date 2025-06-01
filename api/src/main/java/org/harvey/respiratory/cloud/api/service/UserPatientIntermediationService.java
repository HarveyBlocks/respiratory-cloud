package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.entity.UserPatientIntermediation;

import java.util.List;

/**
 * 用户患者中间表
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface UserPatientIntermediationService extends IService<UserPatientIntermediation> {
    boolean existRelation(long userId, long patientId);

    boolean register(long userId, long patientId);

    boolean delete(long patientId, long userId);

    @NonNull
    List<Long> queryPatientOnUser(long userId);

    @NonNull
    List<Long> queryPatientOnUser(long userId, Page<UserPatientIntermediation> page);
}
