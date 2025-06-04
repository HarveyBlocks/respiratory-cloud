package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.dto.SymptomaticPresentationDto;
import org.harvey.respiratory.cloud.common.pojo.entity.SymptomaticPresentation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 症状表现
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface SymptomaticPresentationService extends IService<SymptomaticPresentation> {
    void logicDelete(Long symptomaticPresentationId, String currentUserIdentityCardId);

    /**
     * @return 更新后的新记录的字敦啊
     */
    @NonNull
    Long updateRetainTrace(String currentUserIdentityCardId, Long oldVersionId, SymptomaticPresentation newData);

    @NonNull
    SymptomaticPresentation queryByIdIgnoreDeleted(Long oldVersionId);

    @NonNull
    SymptomaticPresentation queryById(Long oldVersionId);

    @Transactional
    void logicUpdate(Long oldId, SymptomaticPresentation newData);


    /**
     * 只允许服务器内调用, 只有医生用户可调用
     * 外部进行校验
     *
     * @return 插入后的多个症状的id
     */
    @Transactional
    @NonNull
    List<Long> saveSymptomaticPresentationBatch(List<SymptomaticPresentation> presentationList);

    /**
     * 读不出已经逻辑删除的
     */
    @NonNull
    List<SymptomaticPresentation> selectByVisitId(long visitId);

    List<SymptomaticPresentationDto> selectDtoByVisitId(Long visitId);
}
