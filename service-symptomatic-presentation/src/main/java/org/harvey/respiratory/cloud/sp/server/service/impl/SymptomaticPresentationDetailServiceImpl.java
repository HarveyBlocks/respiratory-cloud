package org.harvey.respiratory.cloud.sp.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.harvey.respiratory.cloud.api.service.SymptomaticPresentationDetailService;
import org.harvey.respiratory.cloud.common.pojo.entity.SymptomaticPresentationDetail;
import org.harvey.respiratory.cloud.common.pojo.enums.SymptomaticPresentationType;
import org.harvey.respiratory.cloud.sp.server.dao.SymptomaticPresentationDetailMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-03 22:38
 * @see SymptomaticPresentationDetail
 * @see SymptomaticPresentationDetailMapper
 * @see SymptomaticPresentationDetailService
 */
@Service
public class SymptomaticPresentationDetailServiceImpl extends
        ServiceImpl<SymptomaticPresentationDetailMapper, SymptomaticPresentationDetail> implements
        SymptomaticPresentationDetailService {

    @Override
    public List<SymptomaticPresentationDetail> queryByType(SymptomaticPresentationType type) {
        return super.lambdaQuery().eq(SymptomaticPresentationDetail::getType, type).list();
    }

    @Override
    public Map<Integer, SymptomaticPresentationDetail> queryMapByIds(List<Integer> ids) {
        return super.lambdaQuery()
                .in(SymptomaticPresentationDetail::getId, ids)
                .list()
                .stream()
                .collect(Collectors.toMap(SymptomaticPresentationDetail::getId, d -> d));
    }

    @Override
    public SymptomaticPresentationDetail queryById(Integer id) {
        return super.lambdaQuery().eq(SymptomaticPresentationDetail::getId, id).one();
    }
}