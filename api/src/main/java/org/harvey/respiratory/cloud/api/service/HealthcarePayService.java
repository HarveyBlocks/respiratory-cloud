package org.harvey.respiratory.cloud.api.service;


import org.harvey.respiratory.cloud.common.pojo.dto.QueryBalanceDto;
import org.harvey.respiratory.cloud.common.pojo.dto.RechargeDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Healthcare;
import org.springframework.transaction.annotation.Transactional;

/**
 * 医保余额有关付款
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-21 23:11
 */
public interface HealthcarePayService {
    int queryBalance(QueryBalanceDto queryBalanceDto);

    int recharge(RechargeDto rechargeDto);

    void pay(long visitId);

    /**
     * 事务, 避免脏读啥的
     *
     * @return 返回更新后的余额
     */
    @Transactional
    int updateBalance(Healthcare healthcare, int deltaExpense);
}
