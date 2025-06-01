package org.harvey.respiratory.cloud.user.server.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.harvey.respiratory.cloud.common.pojo.entity.UserSecurity;

/**
 * 用户
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */
@Mapper
public interface UserSecurityMapper extends BaseMapper<UserSecurity> {
}
