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
 * 医疗服务机构表
 * id
 * 名称(varchar(63))
 * 地址(varchar(255))
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_medical_provider_form")
@ApiModel("医疗提供机构")
@NoArgsConstructor
@AllArgsConstructor
public class MedicalProviderForm implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("医疗提供机构id")
    private Integer id;

    @ApiModelProperty("名称(varchar(63))")
    private String name;

    @ApiModelProperty("地址(varchar(255))")
    private String address;

}
