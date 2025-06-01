package org.harvey.respiratory.cloud.common.utils;

import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-18 19:50
 */
public class RoleUtil {
    public static void validOnRole(Role role, Role[] accept) {
        for (Role acc : accept) {
            if (role == acc) {
                return;
            }
        }
        throw new UnauthorizedException("权限不足, 不允许");
    }
}
