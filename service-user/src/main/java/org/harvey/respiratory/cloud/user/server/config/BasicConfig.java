package org.harvey.respiratory.cloud.user.server.config;

import org.harvey.respiratory.cloud.common.config.MvcConfig;
import org.harvey.respiratory.cloud.common.security.SecurityConfig;
import org.harvey.respiratory.cloud.common.utils.UserDtoRedissonLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 对config的import
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-27 16:18
 */
@Configuration
@Import({SecurityConfig.class, MvcConfig.class})
public class BasicConfig {
    @Bean
    public UserDtoRedissonLock userDtoRedissonLock(RedissonClient redissonClient) {
        return new UserDtoRedissonLock(redissonClient);
    }
}
