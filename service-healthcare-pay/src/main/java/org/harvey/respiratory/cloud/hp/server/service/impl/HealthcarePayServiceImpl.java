package org.harvey.respiratory.cloud.hp.server.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.HealthcarePayService;
import org.harvey.respiratory.cloud.api.service.HealthcareService;
import org.harvey.respiratory.cloud.api.service.VisitDoctorService;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.pojo.dto.QueryBalanceDto;
import org.harvey.respiratory.cloud.common.pojo.dto.RechargeDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Healthcare;
import org.harvey.respiratory.cloud.common.pojo.entity.VisitDoctor;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-21 23:11
 */
@Service
@org.apache.dubbo.config.annotation.DubboService
public class HealthcarePayServiceImpl implements HealthcarePayService {
    @DubboReference
    private HealthcareService healthcareService;
    @DubboReference
    private VisitDoctorService visitDoctorService;

    private static HealthcarePayService currentProxy() {
        return (HealthcarePayService) AopContext.currentProxy();
    }

    @Override
    public int queryBalance(QueryBalanceDto queryBalanceDto) {
        Healthcare healthcare;
        try {
            healthcare = healthcareService.query(queryBalanceDto);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("未能找到医保资源", e);
        }
        return healthcare.getBalance();
    }

    @Override
    public int recharge(RechargeDto rechargeDto) {
        Integer amount = rechargeDto.getAmount();
        if (amount == null || amount <= 0) {
            throw new ResourceNotFountException("充值的金额必须是正数");
        }
        Healthcare healthcare = healthcareService.query(rechargeDto.getQueryBalanceDto());
        return currentProxy().updateBalance(healthcare, amount);
    }

    @Override
    public void pay(long visitId) {
        VisitDoctor visitDoctor;
        try {
            visitDoctor = visitDoctorService.querySimplyById(visitId);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("未能找到就诊单", e);
        }
        if (!visitDoctor.isInterviewed()) {
            throw new BadRequestException("目标就诊单还未完成就诊");
        }
        if (visitDoctor.isPaid()) {
            throw new BadRequestException("目标账单已经付款");
        }
        int totalExpense = visitDoctor.getTotalExpense();
        Long patientId = visitDoctor.getPatientId();
        // 问诊的目标用户和医保的目标用户必须是同一个人
        QueryBalanceDto queryBalanceDto = new QueryBalanceDto();
        queryBalanceDto.setPatientId(patientId);
        Healthcare healthcare = healthcareService.query(queryBalanceDto);
        currentProxy().updateBalance(healthcare, -totalExpense);
    }

    @Transactional
    @Override
    public int updateBalance(Healthcare healthcare, int deltaExpense) {
        if (healthcare == null) {
            throw new ResourceNotFountException("未能找到医保资源");
        }
        int newBalance = healthcare.getBalance() + deltaExpense;
        if (newBalance < 0) {
            throw new BadRequestException("余额不足, 请先充值");
        }
        healthcareService.updateBalance(healthcare.getHealthcareId(), newBalance);
        return newBalance;
    }
}
