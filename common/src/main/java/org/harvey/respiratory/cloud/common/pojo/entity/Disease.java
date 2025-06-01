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
import org.harvey.respiratory.cloud.common.advice.cache.executor.QueryBasisHaving;

import java.io.Serializable;

/**
 * 疾病表
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-12 23:55
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_disease")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("疾病具体信息")
public class Disease implements Serializable, QueryBasisHaving<Integer> {
    /**
     * primary key
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Integer id;
    /**
     * 名称
     */
    @ApiModelProperty("名称varchar(63)")
    private String name;
    /**
     * 描述
     */
    @ApiModelProperty("描述TEXT")
    private String description;

    @Override
    public Integer getQueryBasis() {
        return id;
    }
}
