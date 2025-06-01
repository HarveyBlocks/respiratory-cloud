package org.harvey.respiratory.cloud.common.interceptor;


import lombok.NonNull;
import org.harvey.respiratory.cloud.common.constants.ServerConstants;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-03 13:32
 */
public class LoginThreadLocalInterceptor implements HandlerInterceptor {
    private final JacksonUtil jacksonUtil;

    public LoginThreadLocalInterceptor(JacksonUtil jacksonUtil) {
        this.jacksonUtil = jacksonUtil;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        // 1.获取请求头中的 userId
        String userInfo = request.getHeader(ServerConstants.USER_INFO_HEADER_NAME);
        if (userInfo == null || userInfo.isEmpty()) {
            return true;
        }
        // 2.存入上下文
        try {
            UserDto bean = jacksonUtil.toBean(userInfo, UserDto.class);
            UserHolder.saveUser(bean);
        } catch (NumberFormatException ignored) {
        } catch (Throwable e) {
            throw new BadRequestException("错误的user-info信息, 无法解析");
        }
        // 4.放行
        return true;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex) {
        // 清理用户
        UserHolder.removeUser();
    }
}
