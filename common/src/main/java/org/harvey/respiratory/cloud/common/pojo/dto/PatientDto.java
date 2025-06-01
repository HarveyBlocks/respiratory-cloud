package org.harvey.respiratory.cloud.common.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.pojo.entity.Healthcare;
import org.harvey.respiratory.cloud.common.pojo.entity.Patient;
import org.harvey.respiratory.cloud.common.pojo.enums.Sex;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 患者
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 00:48
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "患者信息")
public class PatientDto implements Serializable {
    @ApiModelProperty(value = "患者的id, 服务端生成, 只可读")
    private Long id;
    @ApiModelProperty(value = "手机号码(unique, char(11)), 监护人电话, 不可为null", required = true)
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

    @ApiModelProperty(value = "医保表id, 可以为null")
    private String healthcareCode;

    @ApiModelProperty(value = "医保类型, 可以为null, varchar(63)")
    private String healthcareType;

    @ApiModelProperty(value = "医保余额,单位, 分, 可以为null")
    private Integer balance;

    /**
     * @param patient    not null
     * @param healthcare not null
     */
    public static PatientDto union(Patient patient, Healthcare healthcare) {
        if (!Objects.equals(patient.getHealthcareId(), healthcare.getHealthcareId())) {
            throw new ServerException("foreign key not match");
        }

        return new PatientDto(patient.getId(), patient.getPhone(), patient.getIdentityCardId(), patient.getName(),
                patient.getSex(), patient.getBirthDate(), patient.getAddress(), patient.getHeight(),
                patient.getWeight(), healthcare.getHealthcareCode(), healthcare.getType(), healthcare.getBalance()
        );
    }

    public static PatientDto adapt(Patient patient) {
        return new PatientDto(patient.getId(), patient.getPhone(), patient.getIdentityCardId(), patient.getName(),
                patient.getSex(), patient.getBirthDate(), patient.getAddress(), patient.getHeight(),
                patient.getWeight(), null, null, null
        );
    }

    public Patient buildPatient() {
        return new Patient(null, phone, identityCardId, name, sex, birthDate, address, height, weight, null);
    }

    /**
     * @return null for healthcareId is null
     */
    public Healthcare buildHealthcare() {
        if (healthcareCode == null) {
            return null;
        }
        return new Healthcare(null, healthcareCode, healthcareType, balance);
    }
}
