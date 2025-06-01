package org.harvey.respiratory.cloud.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.harvey.respiratory.cloud.common.pojo.enums.Sex;

import java.io.Serializable;
import java.util.Date;

/**
 * 患者表(既往用药史查询特定药品使用方式中间表)
 * id
 * 电话(unique, char(11))
 * 姓名(varchar(64))
 * 性别(enum-male/female)
 * 出生日期(date)
 * 家庭住址(varchar(255))
 * 身高(m) (float)
 * 体重(kg) (float)
 * 医保表id
 *
 *
 *
 * <pre>{@code
 * create table tb_patient
 * (
 *     id          int8                                     not null comment '患者id'
 *         primary key,
 *     phone       char(11)                                         not null comment '电话号码, +86 only',
 *     name        varchar(63)                               not  null comment '姓名',
 *     sex    enum('MALE','FEMALE')                                 not null comment '性别, 目前只有MALE和FEMALE',
 *     birth_date       date                            not null comment '出生日期',
 *     address        varchar(255)              not null comment '家庭地址',
 *     height        float                      not null comment '升高(m)',
 *     weight        float                      not null comment '体重(kg)',
 *      healthcare_id       int8                null comment '医保号', --  考虑拆表
 *      healthcare_type       varchar(63)       null comment '医保类型',
 *     healthcare_balance       int4            null comment '医保余额',
 *     constraint tb_patient_phone
 *         unique (phone)
 *     constraint tb_patient_healthcare_id
 *         unique (healthcare_id)
 * )
 *     comment '患者表';
 *
 * }</pre>
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:48
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_patient")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "患者信息")
public class Patient implements Serializable {
    @ApiModelProperty(value = "新增时需要为null, 更新时不能为null, 就是依据id更新")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "手机号码(unique, char(11)), 不可为null", required = true)
    private String phone;

    @ApiModelProperty(value = "病患身份证号, char(11) 不可为null", required = true)
    private String identityCardId;

    @ApiModelProperty(value = "名称(varchar(63)), 不可为null", required = true)
    private String name;

    @ApiModelProperty(value = "性别, 不可为null", required = true)
    private Sex sex;

    @ApiModelProperty(value = "出生日期, 不可为null", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthDate;

    @ApiModelProperty(value = "家庭住址(varchar(255)), 不可为null", required = true)
    private String address;

    @ApiModelProperty(value = "身高(m) (float), 不可为null", required = true)
    private float height;

    @ApiModelProperty(value = "体重(kg) (float), 不可为null", required = true)
    private float weight;

    @ApiModelProperty(value = "医保表id, 可以为null. 只能在医保接口更新医保, 不能在病患接口更新医保id")
    private Long healthcareId;

}
