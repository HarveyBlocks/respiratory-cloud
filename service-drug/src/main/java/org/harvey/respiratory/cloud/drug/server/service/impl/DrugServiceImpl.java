package org.harvey.respiratory.cloud.drug.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.harvey.respiratory.cloud.api.service.DrugService;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Drug;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;
import org.harvey.respiratory.cloud.drug.server.dao.DrugMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:33
 * @see Drug
 * @see DrugMapper
 * @see DrugService
 */
@Service
@org.apache.dubbo.config.annotation.DubboService
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {

    @Resource
    private DrugMapper drugMapper;

    @Override
    public void writeValid(UserDto user) {
        if (user == null) {
            throw new UnauthorizedException("请先登录");
        }
        Role role = user.getRole();
        switch (role) {
            case UNKNOWN:
            case PATIENT:
            case NORMAL_DOCTOR:
                throw new UnauthorizedException("权限不足");
            case CHARGE_DOCTOR:
            case MEDICATION_DOCTOR:
            case DEVELOPER:
            case DATABASE_ADMINISTRATOR:
                break;
            default:
                throw new ServerException("Unexpected role value: " + role);
        }
    }

    @Override
    public void deleteById(long drugId) {
        super.removeById(drugId);
    }

    @Override
    public void saveDrug(Drug drug) {
        boolean saved = super.save(drug);
        if (saved) {
            log.debug("新增药物成功");
        } else {
            log.debug("新增药物失败");
        }
    }


    @Override
    public void deplete(Map<Integer, Integer> drugIdToDepleteCountMap) {
        drugMapper.deplete(drugIdToDepleteCountMap);
    }

    @Override
    @NonNull
    public Drug queryById(long id) {
        Drug drug = super.getById(id);
        if (drug == null) {
            throw new ResourceNotFountException("drug of id: " + id);
        } else {
            return drug;
        }
    }

    @Override
    @NonNull
    public List<Drug> queryByName(String name, Page<Drug> page) {
        return super.lambdaQuery().like(Drug::getName, name).page(page).getRecords();
    }

    @Override
    @NonNull
    public Map<Integer, Drug> queryByIds(Collection<Integer> drugIds) {
        List<Drug> drugList = super.lambdaQuery().in(Drug::getId, drugIds).list();
        Map<Integer, Drug> resultMap = new HashMap<>();
        for (Drug drug : drugList) {
            resultMap.put(drug.getId(), drug);
        }
        return resultMap;
    }

    @Override
    @NonNull
    public Map<Integer, Drug> queryByIdsFilterName(Set<Integer> drugIds, String name) {
        if (name == null || name.isEmpty()) {
            throw new BadRequestException("name 不得为空");
        }
        List<Drug> drugList = super.lambdaQuery().in(Drug::getId, drugIds).like(Drug::getName, name).list();
        Map<Integer, Drug> resultMap = new HashMap<>();
        for (Drug drug : drugList) {
            resultMap.put(drug.getId(), drug);
        }
        return resultMap;
    }


}