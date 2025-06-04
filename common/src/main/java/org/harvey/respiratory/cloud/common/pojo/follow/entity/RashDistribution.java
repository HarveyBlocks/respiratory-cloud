package org.harvey.respiratory.cloud.common.pojo.follow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.harvey.respiratory.cloud.common.pojo.follow.enums.AffectedArea;
import org.harvey.respiratory.cloud.common.pojo.follow.enums.SkinRashSymptom;

import java.io.Serializable;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 14:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_rash_distribution")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("皮疹分布表")
public class RashDistribution implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * {@link SkinRashSymptom}
     */
    @ApiModelProperty("皮疹症状")
    @TableField("symptoms")
    private byte[] symptoms;

    @ApiModelProperty("皮疹部位")
    @TableField("location")
    private AffectedArea location;

}