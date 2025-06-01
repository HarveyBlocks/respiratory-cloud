package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.entity.RoleEntity;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;

/**
 * 疾病
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface RoleService extends IService<RoleEntity> {
    /**
     * 只有服务器内部调用
     * 1. 先依据身份证, 如果是未实名, 就是Unknown, 否则进入2
     * 2. medicalProvider查询, 如果有, 则从job中取出role
     * 3. 其他权限表, 给开发者用的, 暂且没有这一选项
     */
    @NonNull
    Role queryRole(String identityCardId);

    @NonNull
    Role getMedicalProviderRole(int jobId);

    @NonNull
    Role selectRole(int roleId);
}
