package org.harvey.respiratory.cloud.disease.server.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.harvey.respiratory.cloud.common.pojo.entity.Disease;
import org.harvey.respiratory.cloud.common.pojo.vo.IntegerBasisPagePair;
import org.harvey.respiratory.cloud.common.pojo.vo.PageRequest;

import java.util.List;

/**
 * 疾病
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */
@Mapper
public interface DiseaseMapper extends BaseMapper<Disease> {
    List<IntegerBasisPagePair> selectIdsByPageBatch(@Param("requests") List<PageRequest> pageRequests);
}
