package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.FamilyHistory;

import java.util.List;

/**
 * 家族史
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface FamilyHistoryService extends IService<FamilyHistory> {

    @NonNull
    Long register(FamilyHistory familyHistory);


    @NonNull
    List<FamilyHistory> queryByPatient(long patientId, Page<FamilyHistory> page);


    @NonNull
    List<FamilyHistory> queryByDiseaseName(long patientId, String diseaseName, Page<FamilyHistory> page);

    @NonNull
    List<FamilyHistory> queryByDisease(long patientId, int diseaseId, Page<FamilyHistory> page);

    @NonNull
    List<FamilyHistory> queryByRelationship(
            long patientId, List<Integer> relationshipIds, Page<FamilyHistory> objectPage);

    void validRoleToRegister(UserDto currentUser, Long patientId);

}
