package org.harvey.respiratory.cloud.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 费用产生表
 * 费用表id
 * 就诊号/就诊表id
 * 类别(varchar(63))
 * 金额(int/分)
 * 描述(varchar(255))
 * <p>
 * <p>
 * tb_expenses_record
 * id int8 comment '费用表主键id' primary key,
 * visit_doctor_id int8 not null comment '就诊号/就诊表',
 * category varchar(63)  not null  comment '消费类别',
 * description varchar(255) not null  comment '描述',
 * amount int4  not null  comment  '金额,分'
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_expenses_record")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("费用产生记录")
public class ExpenseRecord implements Serializable {
    /**
     * 费用表id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "费用表id")
    private Long id;
    /**
     * 就诊号/就诊表id
     */
    @ApiModelProperty(value = "就诊号/就诊表id")
    private Long visitDoctorId;
    /**
     * 类别(varchar(63))
     */
    @ApiModelProperty(value = "类别(varchar(63))")
    private String category;

    /**
     * 金额/分(int)
     */
    @ApiModelProperty(value = "金额/分(int)")
    private Integer amount;
    /**
     * 数量(int)
     */
    @ApiModelProperty(value = "数量(int)")
    private Integer count;
    /**
     * 描述(varchar(255))
     */
    @ApiModelProperty(value = "描述(varchar(255))")
    private String description;
}
