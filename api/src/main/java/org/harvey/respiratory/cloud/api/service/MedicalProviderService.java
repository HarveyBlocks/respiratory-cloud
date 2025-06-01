package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProvider;

import java.util.List;

/**
 * 医疗提供者
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface MedicalProviderService extends IService<MedicalProvider> {
    @NonNull
    Long register(MedicalProvider medicalProvider);

    void update(MedicalProvider medicalProvider);

    void delete(long id);

    @NonNull
    MedicalProvider selectByUser(long userId);

    @NonNull
    MedicalProvider selectByIdentityCardId(String identityCardId);

    @NonNull
    MedicalProvider selectByPhone(String phoneNumber);

    /**
     * @param name 空字符和null将退化为全查
     */
    @NonNull
    List<MedicalProvider> selectByAny(String name, Integer formId, Page<MedicalProvider> page);


    boolean samePeople(long id, String identityCardId);

    /**
     * 依据 id 查询
     */
    @NonNull
    MedicalProvider queryById(long medicalProviderId);
}
