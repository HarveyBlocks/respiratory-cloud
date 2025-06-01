package org.harvey.respiratory.cloud.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户病人中间表
 * 用户可以持有多个病人的信息
 * 病人可以被多个用户持有
 * 不适合作为Model
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-12 23:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_user_patient_intermediation")
@NoArgsConstructor
@AllArgsConstructor
public class UserPatientIntermediation implements Serializable {
    /**
     * 不能为null
     */
    private Long userId;
    /**
     * 不能为null
     */
    private Long patientId;
}
