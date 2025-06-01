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
 * 病患关系表
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-16 19:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_family_relationship")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户权限表")
public class FamilyRelationshipEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Integer id;
    /**
     * 关系名
     */
    @ApiModelProperty("关系名, 英文")
    private String name;
    /**
     * 关系描述
     */
    @ApiModelProperty("关系名, 中文")
    private String description;
    /**
     * true for level-亲近
     */
    @ApiModelProperty("一代亲属, 二代亲属, 大概, true 表 一代亲属")
    private Boolean closeLevel;
}
