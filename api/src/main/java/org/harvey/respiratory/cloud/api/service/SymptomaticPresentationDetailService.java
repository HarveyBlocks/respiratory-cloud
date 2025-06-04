package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.harvey.respiratory.cloud.common.pojo.entity.SymptomaticPresentationDetail;
import org.harvey.respiratory.cloud.common.pojo.enums.SymptomaticPresentationType;

import java.util.List;
import java.util.Map;

/**
 * 症状表现
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface SymptomaticPresentationDetailService extends IService<SymptomaticPresentationDetail> {
    List<SymptomaticPresentationDetail> queryByType(SymptomaticPresentationType type);

    Map<Integer, SymptomaticPresentationDetail> queryMapByIds(List<Integer> ids);

    SymptomaticPresentationDetail queryById(Integer id);
}
