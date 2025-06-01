package org.harvey.respiratory.cloud.common.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-26 21:55
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户信息")
public class UserSecurityDto implements Serializable {
    public static final String DEFAULT_NAME = "unknown";

    /**
     * 主键, 不用医保号是因为医保号可能不是升序插入的
     */
    private Long id;

    /**
     * 手机号码, 实际上有外键的功能,
     * 但并不会建立实际的外键, 因为外键应当指向主键,
     * 而phone不能成为主键
     * 因为phone可以脱离数据库的表存在
     */
    @ApiModelProperty("电话号码, 11位")
    private String phone;

    @ApiModelProperty("身份证号, 18位")
    private String identityCardId;

    /**
     * 昵称，默认是随机字符
     */
    @ApiModelProperty("姓名")
    private String name;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @ApiModelProperty("注册时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @ApiModelProperty("最近一次更新")
    private LocalDateTime updateTime;

    public UserDto buildSimple() {
        UserDto userDto = new UserDto();
        userDto.setId(this.getId());
        userDto.setName(this.getName());
        userDto.setIdentityCardId(this.getIdentityCardId());
        return userDto;
    }
}
