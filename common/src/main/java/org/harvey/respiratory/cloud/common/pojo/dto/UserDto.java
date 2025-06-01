package org.harvey.respiratory.cloud.common.pojo.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;

import java.io.Serializable;

/**
 * 用户简要信息
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 14:13
 */
@Data
@AllArgsConstructor
@ApiModel(description = "简单的用户信息")
public class UserDto implements Serializable {
    @ApiModelProperty("用户主键. 对于更新, 用户只能更新自己. 所以更新的业务上这个字段没有意义")
    private Long id;
    @ApiModelProperty("姓名")
    private String name;

    /**
     * 会被服务器set, 每次请求都会set一下, 而不已用户的为准
     */
    @ApiModelProperty(value = "权限", hidden = true)
    private Role role;
    @ApiModelProperty("用户身份证")
    private String identityCardId;

    public UserDto() {
    }


    /**
     * 当对象是直接展示给用户的时候, 需要做安全措施
     */
    public void safelySet() {
        this.setRole(null);
        String origin = this.getIdentityCardId();
        this.setIdentityCardId(origin.substring(0, 3) + "***");
    }
}
