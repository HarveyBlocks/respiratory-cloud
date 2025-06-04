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

import java.io.Serializable;

/**
 * 药品表
 * id
 * 名称(varchar(63))
 * 规格(varchar(63))
 * 给药途径(varchar(63))
 * 用药部位(varchar(63))
 * 用药注意事项(varchar(255))
 * 用药指导(varchar(255))
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_drug")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("药品具体信息")
public class Drug implements Serializable {
    /**
     * 药品id, 不是雪花, 因为不是高并发, 也不怕被猜到id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("药品id")
    private Integer id;
    /**
     * 药物名称(varchar(63))
     */
    @ApiModelProperty("药物名称(varchar(63))")
    private String name;

    @ApiModelProperty("治疗目标(varchar(255))")
    private String treatTarget;
    /**
     * 药物单价, 单位, 分
     */
    @ApiModelProperty("药物单价, 单位, 分")
    private Integer expenseEach;
    /**
     * 药物规格(varchar(63))
     */
    @ApiModelProperty(value = "药物规格(varchar(63))")
    private String specification;
    /**
     * 给药途径(varchar(63))
     */
    @ApiModelProperty(value = "给药途径(varchar(63))")
    private String administrationRoute;
    /**
     * 用药部位(varchar(63))
     */
    @ApiModelProperty(value = "用药部位(varchar(63))")
    private String medicationSite;
    /**
     * 用药注意事项(varchar(255))
     */
    @ApiModelProperty(value = "用药注意事项(varchar(255))")
    private String medicationPrecaution;
    /**
     * 用药指导(TEXT)
     */
    @ApiModelProperty(value = " 用药指导(TEXT)")
    private String guidance;
    @ApiModelProperty(value = " 药品库存")
    private Integer storage;
}
