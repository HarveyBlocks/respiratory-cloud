package org.harvey.respiratory.cloud.api.service;


import com.baomidou.mybatisplus.extension.service.IService;
import lombok.NonNull;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.ExpenseRecord;

import java.util.List;

/**
 * 费用记录
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */

public interface ExpenseRecordService extends IService<ExpenseRecord> {
    void saveOnInterview(List<ExpenseRecord> expenseRecordList);

    @NonNull
    List<ExpenseRecord> querySelfExpenseRecord(UserDto userDto, long visitId);
}
