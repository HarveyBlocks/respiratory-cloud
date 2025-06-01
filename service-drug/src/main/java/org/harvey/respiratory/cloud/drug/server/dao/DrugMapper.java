package org.harvey.respiratory.cloud.drug.server.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.harvey.respiratory.cloud.common.pojo.entity.Drug;

import java.util.Map;

/**
 * 疾病
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */
@Mapper
public interface DrugMapper extends BaseMapper<Drug> {
    void deplete(@Param("depleteMap") Map<Integer, Integer> depleteMap);
}
