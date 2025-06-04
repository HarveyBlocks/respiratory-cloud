package org.harvey.respiratory.cloud.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.harvey.respiratory.cloud.common.pojo.enums.Severity;
import org.harvey.respiratory.cloud.common.pojo.enums.SymptomaticPresentationType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_symptomatic_presentation_detail")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("具体症状")
public class SymptomaticPresentationDetail implements Serializable {
    /**
     * 症状id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "症状id")
    private Integer id;
    /**
     * 就诊号/就诊表id
     */
    @ApiModelProperty(value = "名字varchar(63)")
    private String name;
    /**
     * 具体的症状的id(int)
     */
    @ApiModelProperty(value = "类型enum")
    private SymptomaticPresentationType type;
}
