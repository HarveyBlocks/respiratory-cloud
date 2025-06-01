package org.harvey.respiratory.cloud.common.utils;


import lombok.NonNull;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;


/**
 * 将用户信息存在ThreadLocal,方便取用
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 14:12
 */
public class UserHolder {
    private static final ThreadLocal<UserDto> TL = new ThreadLocal<>();

    public static void saveUser(UserDto user) {
        TL.set(user);
    }

    public static UserDto getUser() {
        return TL.get();
    }

    public static void removeUser() {
        TL.remove();
    }

    @NonNull
    public static Long currentUserId() {
        UserDto user = getUser();
        if (user == null) {
            throw new UnauthorizedException("未登录,需要登录");
        }
        return user.getId();
    }

    public static boolean exist() {
        return getUser() != null;
    }
}
