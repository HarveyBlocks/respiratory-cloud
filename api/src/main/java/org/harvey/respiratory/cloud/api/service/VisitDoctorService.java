package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.VisitDoctor;
import org.harvey.respiratory.cloud.common.pojo.vo.RangeDate;

import java.util.List;

/**
 * 就诊
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface VisitDoctorService extends IService<VisitDoctor> {
    /**
     * 仅在服务器内部使用
     */
    @NonNull
    Long queryMedicalProviderId(long visitDoctorId);

    @NonNull
    VisitDoctor queryMedicalProviderAndPatientId(long visitId);

    @NonNull
    VisitDoctor querySimplyById(long visitDoctorId);

    void updateAfterInterview(VisitDoctor visitDoctor);

    @NonNull
    Long createVisitDoctorId(long patientId, long medicalProviderId);

    @NonNull
    List<VisitDoctor> doctorQuery(String identityCardId, RangeDate rangeDate, Page<VisitDoctor> page);

    @NonNull
    List<VisitDoctor> patientQuery(long currentUserId, RangeDate rangeDate, Page<VisitDoctor> page);

    void queryValid(UserDto currentUser, long visitId);
}
