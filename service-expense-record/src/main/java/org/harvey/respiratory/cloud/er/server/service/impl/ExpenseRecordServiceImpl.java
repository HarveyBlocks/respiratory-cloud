package org.harvey.respiratory.cloud.er.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.ExpenseRecordService;
import org.harvey.respiratory.cloud.api.service.VisitDoctorService;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.ExpenseRecord;
import org.harvey.respiratory.cloud.er.server.dao.ExpenseRecordMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:35
 * @see ExpenseRecord
 * @see ExpenseRecordMapper
 * @see ExpenseRecordService
 */
@Service
@org.apache.dubbo.config.annotation.DubboService
public class ExpenseRecordServiceImpl extends ServiceImpl<ExpenseRecordMapper, ExpenseRecord> implements
        ExpenseRecordService {

    @DubboReference
    private VisitDoctorService visitDoctorService;

    @Override
    public void saveOnInterview(List<ExpenseRecord> expenseRecordList) {
        boolean saved = super.saveBatch(expenseRecordList);
        if (saved) {
            log.debug("更新费用记录成功");
        } else {
            log.warn("更新费用记录失败");
        }
    }


    @Override
    @NonNull
    public List<ExpenseRecord> querySelfExpenseRecord(UserDto currentUser, long visitId) {
        visitDoctorService.queryValid(currentUser, visitId);
        return super.lambdaQuery().eq(ExpenseRecord::getVisitDoctorId, visitId).list();
    }
}