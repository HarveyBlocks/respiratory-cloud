package org.harvey.respiratory.cloud.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.harvey.respiratory.cloud.common.pojo.dto.UserSecurityDto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户, 不可暴露给外界
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 14:05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`user_security`")
@NoArgsConstructor
@AllArgsConstructor
public class UserSecurity implements Serializable {

    @TableField(exist = false)
    public static final String DEFAULT_NAME = "unknown";

    /**
     * 主键, 不用医保号是因为医保号可能不是升序插入的
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 手机号码, 实际上有外键的功能,
     * 但并不会建立实际的外键, 因为外键应当指向主键,
     * 而phone不能成为主键
     * 因为phone可以脱离数据库的表存在
     */
    private String phone;


    private String identityCardId;

    /**
     * 密码，加密存储
     */
    private String password;

    /**
     * 昵称，默认是随机字符
     */
    private String name;

    /**
     * 创建时间
     */

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDateTime updateTime;

    public UserSecurity(UserSecurityDto newSecurity) {
        this(newSecurity.getId(), newSecurity.getPhone(), newSecurity.getIdentityCardId(), null,
                newSecurity.getName(), newSecurity.getCreateTime(), newSecurity.getUpdateTime()
        );
    }

    public UserSecurityDto buildDto() {
        return new UserSecurityDto(this.getId(), this.getPhone(), this.getIdentityCardId(), this.getName(),
                this.getCreateTime(), this.getUpdateTime()
        );
    }


}
