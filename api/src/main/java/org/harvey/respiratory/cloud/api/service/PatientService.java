package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.dto.PatientDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Healthcare;
import org.harvey.respiratory.cloud.common.pojo.entity.Patient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 患者
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface PatientService extends IService<Patient> {
    /**
     * 当前患者不存在个人信息才能注册
     * 用phone检查是否存在
     * 如果不存在, 新增, 存在更新
     *
     * @return 返回id
     */

    long registerPatientInformation(PatientDto patientDto, long currentUserId);

    /**
     * 如果不存在用户-病患关系, 登记用户-病患关系
     * 如果新增healthcare登记, 就加入healthcare
     *
     * @return patient id
     */
    @Transactional
    long registerForExistPatient(
            Patient patientFromDb, Healthcare healthcare, long currentUserId);

    /**
     * 插入Patient
     * 登记用户-病患关系
     * 如果含有healthcare, 就加入healthcare
     */
    @NonNull
    @Transactional
    Long registerForNotExistPatient(Patient patient, Healthcare healthcare, long currentUserId);

    /**
     * 最简单的查询, 不会做任何的校验
     */
    @NonNull
    Patient selectByIdCard(String identityCardId);

    void updatePatient(Patient patient, long currentUserId);

    @NonNull
    List<PatientDto> querySelfPatients(long currentUserId, Page<Patient> page);

    @NonNull
    PatientDto queryByHealthcare(UserDto user, String healthcareCode);

    @NonNull
    PatientDto queryById(UserDto user, long patientId);

    /**
     * 如果不存在则注册, 如果存在则返回
     */
    @NonNull
    PatientDto queryByIdentity(long currentUserId, String cardId);

    void deletePatientRecord(long patientId, long currentUserId);

    @NonNull
    Patient queryByIdSimply(long patientId);

    @NonNull
    Patient queryByCardIdSimply(String identifierCardId);
}
