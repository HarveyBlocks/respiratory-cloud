package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Drug;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 疾病
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface DrugService extends IService<Drug> {
    void writeValid(UserDto user);

    void deleteById(long drugId);

    @NonNull
    Drug queryById(long id);

    @NonNull
    List<Drug> queryByName(String name, Page<Drug> page);

    @NonNull
    Map<Integer, Drug> queryByIds(Collection<Integer> drugIds);

    @NonNull
    Map<Integer, Drug> queryByIdsFilterName(Set<Integer> drugIds, String name);

    void saveDrug(Drug drug);

    void deplete(Map<Integer, Integer> drugIdToDepleteCountMap);
}
