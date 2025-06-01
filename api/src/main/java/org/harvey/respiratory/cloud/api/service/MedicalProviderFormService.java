package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.constants.RoleConstant;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProviderForm;

import java.util.List;

/**
 * 医疗提供机构
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface MedicalProviderFormService extends IService<MedicalProviderForm> {
    /**
     * @see RoleConstant#MEDICAL_PROVIDER_UPDATE
     */
    @NonNull
    Integer register(MedicalProviderForm form);

    /**
     * @see RoleConstant#MEDICAL_PROVIDER_UPDATE
     */
    void update(MedicalProviderForm newForm);

    /**
     * @see RoleConstant#MEDICAL_PROVIDER_UPDATE
     */
    void delete(long id);

    /**
     * @see RoleConstant#MEDICAL_PROVIDER_READ
     */
    @NonNull
    MedicalProviderForm querySelf(long userId);

    @NonNull
    List<MedicalProviderForm> queryAny(Page<MedicalProviderForm> page);

    @NonNull
    MedicalProviderForm queryById(int formId);

    @NonNull
    List<MedicalProviderForm> queryByName(String name);

    @NonNull
    List<MedicalProviderForm> queryByAddress(String address);


}
