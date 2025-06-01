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
 * 医保
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 22:14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_healthcare")
@NoArgsConstructor
@ApiModel("医保信息")
@AllArgsConstructor
public class Healthcare implements Serializable {
    @ApiModelProperty(value = "医保表id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long healthcareId;
    @ApiModelProperty(value = "医保表码, 不可以可以为null")
    private String healthcareCode;

    @ApiModelProperty(value = "医保类型, 可以为null, varchar(63)")
    private String type;

    @ApiModelProperty(value = "医保余额,单位, 分, 不可以为null, 全部是null", hidden = true)
    private Integer balance;


}
