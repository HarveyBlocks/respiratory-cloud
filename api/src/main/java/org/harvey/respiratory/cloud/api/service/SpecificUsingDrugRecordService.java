package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.dto.DrugDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.SpecificUsingDrugRecord;
import org.harvey.respiratory.cloud.common.pojo.vo.RangeDate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 具体药物使用记录
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface SpecificUsingDrugRecordService extends IService<SpecificUsingDrugRecord> {
    void validOnWrite(UserDto user, Long visitDoctorId);

    void validOnVisitRead(UserDto user, Long visitId);

    void validOnPatientRead(UserDto user, Long patientId);

    void logicDelete(long id);

    void logicDelete(long visitDoctorId, long drugId);

    /**
     * @return 返回新记录的信息
     */
    long updateRetainTrace(UserDto userDto, long oldVersionId, SpecificUsingDrugRecord newData);

    @Transactional
    void logicUpdate(long oldId, SpecificUsingDrugRecord newData);

    @NonNull
    SpecificUsingDrugRecord queryByIdIgnoreDeleted(Long id);

    @NonNull
    SpecificUsingDrugRecord queryById(Long id);

    @NonNull
    List<DrugDto> queryDrugInVisit(long visitId);

    @NonNull
    List<DrugDto> queryHistoryDrugUsingByName(long patientId, String name);

    @NonNull
    List<DrugDto> queryHistoryDrugUsingByDate(long patientId, RangeDate rangeDate);

    /**
     * 只允许服务器内调用, 只有医生用户可调用
     * 外部进行校验 {@link #validOnWrite(UserDto, Long)}
     *
     * @return 插入后的多个症状的id
     */
    @Transactional
    @NonNull
    List<Long> saveSymptomaticPresentationBatch(List<SpecificUsingDrugRecord> recordList);

}
