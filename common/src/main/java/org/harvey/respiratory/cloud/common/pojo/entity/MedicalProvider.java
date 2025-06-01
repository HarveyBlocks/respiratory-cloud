package org.harvey.respiratory.cloud.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 医疗服务者表
 * id
 * 电话号码(unique, char(11))
 * 名称(varchar(63))
 * 职称(enum-普通/主管/药物)
 * 医疗服务机构表id
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_medical_provider")
@NoArgsConstructor
@AllArgsConstructor
public class MedicalProvider implements Serializable {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "新增时需要为null, 更新时不能为null, 就是依据id更新")
    private Long id;
    @ApiModelProperty(value = "医疗提供者身份证号, char(11) 不可为null", required = true)
    private String identityCardId;
    @ApiModelProperty(value = "名称(varchar(63))")
    private String name;
    @ApiModelProperty(value = "医疗服务机构表id, 不可以为null")
    private Integer formId;
    @ApiModelProperty(value = "科室的id, 不可以为null")
    private Integer departmentId;
    @ApiModelProperty(value = "职位的id, 不可以为null")
    private Integer jobId;
}
