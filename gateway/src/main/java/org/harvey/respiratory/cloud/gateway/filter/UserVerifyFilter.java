package org.harvey.respiratory.cloud.gateway.filter;


import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.RoleService;
import org.harvey.respiratory.cloud.api.service.UserSecurityService;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.constants.ServerConstants;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;
import org.harvey.respiratory.cloud.common.pojo.vo.Result;
import org.harvey.respiratory.cloud.common.security.JwtTool;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.harvey.respiratory.cloud.gateway.properties.AuthProperties;
import org.harvey.respiratory.cloud.gateway.util.IpTool;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 校验用户
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-01-10 15:46
 */
@Component
@Slf4j
public class UserVerifyFilter implements GlobalFilter, Ordered {
    // Spring提供路径解析器
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @DubboReference // 远程注入
    private RoleService roleService;
    @Resource
    private JacksonUtil jacksonUtil;
    @Resource
    private JwtTool jwtTool;
    @Resource
    private AuthProperties authProperties;

    private static String ipFromRequest(ServerHttpRequest request) {
        return Optional.ofNullable(request.getRemoteAddress())
                .map(address -> address.getAddress().getHostAddress())
                .orElse(null);
    }

    /**
     * 模拟登录校验逻辑
     *
     * @param exchange 提供网关内部的共享数据
     * @param chain    提供一种执行下一个过滤器的方式
     * @return 将回调的Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 判断是否需要做登录拦截
        String path = request.getPath().toString();
        if (isExcludePath(path)) {
            // 被排除, 就放行
            return chain.filter(exchange.mutate().build());
        }
        // 从请求头获取登录凭证
        String id = idFromRequest(request);
        UserDto userDto = userFromRedis(id);
        if (userDto == null) {
            // UNAUTHORIZED 401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            dataBufferFactory.wrap(jacksonUtil.toBytes(Result.error(401, "权限不足")));
            return response.setComplete();// 停止
        }
        ServerWebExchange.Builder mutatedBuilder = exchange.mutate();// 突变,改变
        if (userDto.getId() != null) {
            // 保存到ThreadLocal
            Role role = roleService.queryRole(userDto.getIdentityCardId());
            userDto.setRole(role);
            UserHolder.saveUser(userDto);
            // 对于已登录的请求,将用户上下文放入请求头
            String jsonUser = jacksonUtil.toJsonStr(userDto);
            mutatedBuilder.request(builder -> builder.header(ServerConstants.USER_INFO_HEADER_NAME, jsonUser));
        }
        // 放行
        return chain.filter(mutatedBuilder.build()).then(Mono.fromRunnable(() -> {
            addRedisRequestCount(request);
            // 完成Controller之后移除UserHolder, 以防下一次用这条线程的请求获取到不属于它的用户信息
            UserHolder.removeUser();
        }));
    }

    private void addRedisRequestCount(ServerHttpRequest request) {
        String userId;
        if (!UserHolder.exist()) {
            userId = ipFromRequest(request);
        } else {
            userId = UserHolder.currentUserId().toString();
        }
        String tokenKey = RedisConstants.QUERY_USER_KEY + userId;
        increaseTimeField(tokenKey);
    }


    private String idFromRequest(ServerHttpRequest request) {
        List<String> tokens = request.getHeaders().get(ServerConstants.AUTHORIZATION_HEADER);
        String token = null;
        if (tokens != null && tokens.size() == 1) {
            token = tokens.get(0);
        }
        String id;
        if (token == null || token.isEmpty()) {
            id = ipFromRequest(request);
            // IP归属地
            String[] regin = IpTool.map(id);
            log.info(String.join(".", regin));
        } else {
            try {
                id = jwtTool.parseToken(token).toString();
            } catch (Exception e) {
                log.warn(e.getMessage());
                id = ipFromRequest(request);
            }
        }
        return id;
    }

    public UserDto userFromRedis(String id) {
        if (existInBlacklist(id)) {
            // 如果在黑名单, 直接不行
            return null;
        }
        // 获取user数据
        String tokenKey = RedisConstants.QUERY_USER_KEY + id;

        Map<Object, Object> originMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        Map<String, String> userFieldMap = new HashMap<>();
        originMap.forEach((k, v) -> userFieldMap.put(String.valueOf(k), v == null ? null : String.valueOf(v)));

        boolean isTempVisit = userFieldMap.get(UserSecurityService.NAME_FIELD) == null;
        if (isTempVisit) {
            // entries不会返回null
            // 用户不存在,就是游客,也给他限个流
            if (userFieldMap.get(UserSecurityService.TIME_FIELD) == null) {
                resetTimeField(true, tokenKey);
                userFieldMap.put(UserSecurityService.TIME_FIELD, ServerConstants.RESTRICT_REQUEST_TIMES_FOR_TEMP);
            }
        }
        // 更新时间
        if (RedisConstants.QUERY_USER_TTL != -1L) {
            stringRedisTemplate.expire(tokenKey, RedisConstants.QUERY_USER_TTL, TimeUnit.MINUTES);
        }
        String time = userFieldMap.get(UserSecurityService.TIME_FIELD);
        if ("0".equals(time) || time.startsWith("-")) {
            log.error("访问次数太多了");
            //  经测试, 这个需要频率数在200QPS能触发28次左右(限制是同时7个)
            // 这种肯定是开挂了, 要不要加黑名单到里去?
            // 不在黑名单, 加入黑名单
            addToBlacklist(id);
            resetTimeField(isTempVisit, tokenKey);
            return null;
        } else {
            decreaseTimeField(tokenKey);
        }
        userFieldMap.remove(UserSecurityService.TIME_FIELD);
        if (userFieldMap.isEmpty()) {
            // 现在是游客的可以走了
            return new UserDto();
        }
        // 第三个参数: 是否忽略转换过程中产生的异常
        return jacksonUtil.toBean(userFieldMap, UserDto.class);
    }

    private void decreaseTimeField(String tokenKey) {
        stringRedisTemplate.opsForHash().increment(tokenKey, UserSecurityService.TIME_FIELD, -1);
    }

    private void increaseTimeField(String tokenKey) {
        stringRedisTemplate.opsForHash().increment(tokenKey, UserSecurityService.TIME_FIELD, 1);
    }

    private void resetTimeField(boolean isTempVisit, String tokenKey) {
        String visitableTime =
                isTempVisit ? ServerConstants.RESTRICT_REQUEST_TIMES_FOR_TEMP : ServerConstants.RESTRICT_REQUEST_TIMES;
        stringRedisTemplate.opsForHash().put(tokenKey, UserSecurityService.TIME_FIELD, visitableTime);
    }

    private boolean existInBlacklist(String id) {
        return stringRedisTemplate.opsForValue().get(RedisConstants.BLACKLIST_PREFIX + id) != null;
    }

    private void addToBlacklist(String id) {
        stringRedisTemplate.opsForValue()
                .set(RedisConstants.BLACKLIST_PREFIX + id, "", RedisConstants.BLACKLIST_TIME, TimeUnit.SECONDS);
    }

    private boolean isExcludePath(String path) {
        List<String> excludePaths = authProperties.getExcludePaths();
        if (excludePaths == null) {
            return false;
        }
        for (String pattern : excludePaths) {
            if (antPathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        // 提高此过滤器的优先级(比Netty高)
        return 0;
    }


}
