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
 * 家族病史(由于患者家属的就诊记录不一定在本数据库中, 故不能直接使用患者家属的患者id)
 * 患者本人在患者表中id
 * 家族史记录id
 * 家族成员关系enum(有很多), 该属性冗余, 不属于BC FC, 但为了提高效率
 * 疾病表id
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:55
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_family_history")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("家庭病史")
public class FamilyHistory implements Serializable {
    /**
     * 家族史记录id, 冗余, 不然就要全属性主键了, 这不好
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "家族史记录id, 冗余, 不过没有这个, 就要全属性主键了, 这不好")
    private Long id;
    /**
     * 患者本人在患者表中id
     */
    @ApiModelProperty(value = "患者本人在患者表中id")
    private Long patientId;
    /**
     * 家族成员关系enum(有很多)
     */
    @ApiModelProperty(value = "家族成员关系enum(有很多)")
    private Integer familyRelationshipId;
    /**
     * 疾病表id
     */
    @ApiModelProperty(value = "疾病表id")
    private Integer diseaseId;

    @ApiModelProperty(value = "家族居住环境共同暴露因素, varchar")
    private String livingEnvironmentCommonFactor;

    @ApiModelProperty(value = "居住在吸烟环境")
    private Boolean livingInSmokingEnvironment;


}
