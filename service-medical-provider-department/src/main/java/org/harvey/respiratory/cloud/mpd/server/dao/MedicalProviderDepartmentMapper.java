package org.harvey.respiratory.cloud.mpd.server.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProviderDepartment;

/**
 * 医院科室
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */
@Mapper
public interface MedicalProviderDepartmentMapper extends BaseMapper<MedicalProviderDepartment> {
}
