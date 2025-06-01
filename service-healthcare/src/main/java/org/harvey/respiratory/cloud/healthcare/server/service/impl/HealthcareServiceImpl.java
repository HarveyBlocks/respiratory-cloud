package org.harvey.respiratory.cloud.healthcare.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.HealthcareService;
import org.harvey.respiratory.cloud.api.service.PatientService;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.pojo.dto.QueryBalanceDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Healthcare;
import org.harvey.respiratory.cloud.common.pojo.entity.Patient;
import org.harvey.respiratory.cloud.healthcare.server.dao.HealthcareMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 22:38
 * @see Healthcare
 * @see HealthcareMapper
 * @see HealthcareService
 */
@org.apache.dubbo.config.annotation.DubboService
@Slf4j
@Service
public class HealthcareServiceImpl extends ServiceImpl<HealthcareMapper, Healthcare> implements HealthcareService {
    @DubboReference
    private PatientService patientService;

    @Override
    public void register(Healthcare healthcare) {
        // 强制设置为0, 防止请求不好的东西
        healthcare.setBalance(0);
        boolean saved = this.save(healthcare);
        if (saved) {
            log.debug("成功添加医保号{}", healthcare.getHealthcareId());
        } else {
            log.error("添加医保号{}失败. ", healthcare.getHealthcareId());
        }
    }

    @Override
    @NonNull
    public Healthcare queryByCode(String healthcareCode) {
        Healthcare one = super.lambdaQuery().eq(Healthcare::getHealthcareCode, healthcareCode).one();
        if (one == null) {
            throw new ResourceNotFountException("不能依据code找到: " + healthcareCode);
        }
        return one;
    }

    @Override
    @NonNull
    public Healthcare queryById(long healthcareId) {
        Healthcare healthcare = super.getById(healthcareId);
        if (healthcare == null) {
            throw new ResourceNotFountException("依据医保id " + healthcareId + " 未查询到医保");
        }
        return healthcare;
    }

    @Override
    @NonNull
    public Healthcare query(QueryBalanceDto queryBalanceDto) {
        Long healthcareId = queryBalanceDto.getHealthcareId();
        if (healthcareId != null) {
            Healthcare one = super.lambdaQuery().eq(Healthcare::getHealthcareId, healthcareId).one();
            if (one != null) {
                return one;
            }
        }
        String healthcareCode = queryBalanceDto.getHealthcareCode();
        if (healthcareCode != null && !healthcareCode.isEmpty()) {
            Healthcare one = super.lambdaQuery().eq(Healthcare::getHealthcareCode, healthcareCode).one();
            if (one != null) {
                return one;
            }
        }
        Long patientId = queryBalanceDto.getPatientId();
        if (patientId != null) {
            Patient patient;
            try {
                patient = patientService.queryByIdSimply(patientId);
            } catch (ResourceNotFountException e) {
                patient = null;
            }
            if (patient != null) {
                if (patient.getHealthcareId() == null) {
                    throw new ResourceNotFountException("不能依据病人 " + patient.getId() + " 找到");
                }
                Healthcare one = super.lambdaQuery().eq(Healthcare::getHealthcareId, patient.getHealthcareId()).one();
                if (one == null) {
                    throw new ResourceNotFountException("不能依据code找到: " + healthcareCode);
                }
                return one;
            }
        }
        String identifierCardId = queryBalanceDto.getIdentifierCardId();
        if (identifierCardId == null || identifierCardId.isEmpty()) {
            throw new ResourceNotFountException("不能找到");
        }
        Patient patient;
        try {
            patient = patientService.queryByCardIdSimply(identifierCardId);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("不能找到", e);
        }
        if (patient.getHealthcareId() == null) {
            throw new ResourceNotFountException("不能依据病人 " + patient.getId() + " 找到");
        }
        Healthcare one = super.lambdaQuery().eq(Healthcare::getHealthcareId, patient.getHealthcareId()).one();
        if (one == null) {
            throw new ResourceNotFountException("不能依据id找到: " + patient.getHealthcareId());
        }
        return one;
    }

    @Override
    public void updateBalance(long healthcareId, int newBalance) {
        super.lambdaUpdate()
                .set(Healthcare::getBalance, newBalance)
                .eq(Healthcare::getHealthcareId, healthcareId)
                .update();
    }

    @Override
    public List<Healthcare> queryNullableElementsByIds(List<Long> healthcareIds) {
        // TODO TEST
        int size = healthcareIds.size();
        List<Long> existIds = healthcareIds.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Map<Long, Healthcare> existMap = super.lambdaQuery()
                .in(Healthcare::getHealthcareId, existIds)
                .list().stream().collect(Collectors.toMap(Healthcare::getHealthcareId, h -> h));
        List<Healthcare> healthcareList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Long id = healthcareIds.get(i);
            if (id == null) {
                Healthcare nullHealthcare = new Healthcare();
                healthcareList.add(nullHealthcare);
            } else {
                Healthcare healthcare = existMap.get(id);
                if (healthcare == null) {
                    throw new ResourceNotFountException("医保id为" + id + "的医保记录不存在");
                }
                healthcareList.add(healthcare);
            }
        }
        return healthcareList;
    }
}