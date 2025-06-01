package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Disease;

import java.util.List;

/**
 * 疾病
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface DiseaseService extends IService<Disease> {
    void deleteById(int id);

    @NonNull
    List<Disease> selectByVisitDoctor(long visitId);

    @NonNull
    Disease selectById(int id);

    @NonNull
    List<Disease> selectByPage(Page<Disease> page);

    @NonNull
    List<Disease> selectByName(String name, Page<Disease> page);

    @NonNull
    List<Integer> queryIdsByName(String diseaseName);

    @NonNull
    List<String> queryDiseaseNameByIds(List<Integer> diseaseIds);

    void validOnWrite(UserDto user);

    @NonNull
    Integer register(Disease disease);
}
