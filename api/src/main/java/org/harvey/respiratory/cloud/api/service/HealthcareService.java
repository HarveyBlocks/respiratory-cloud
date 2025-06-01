package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.dto.QueryBalanceDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Healthcare;

import java.util.List;

/**
 * 医保
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 22:37
 */
public interface HealthcareService extends IService<Healthcare> {
    /**
     * @param healthcare balance 强制为0
     */
    void register(Healthcare healthcare);

    @NonNull
    Healthcare queryByCode(String healthcareCode);

    @NonNull
    Healthcare queryById(long healthcareId);

    @NonNull
    Healthcare query(QueryBalanceDto queryBalanceDto);

    void updateBalance(long healthcareId, int newBalance);

    /**
     * @param healthcareIds 这个集合中的元素可以为null
     * @return 集合中的元素都不为null, 如果healthcareIds的元素是null, 其对应的元素的id是null
     */
    List<Healthcare> queryNullableElementsByIds(List<Long> healthcareIds);
}
