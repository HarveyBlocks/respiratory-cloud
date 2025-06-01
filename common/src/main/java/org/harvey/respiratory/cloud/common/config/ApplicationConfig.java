package org.harvey.respiratory.cloud.common.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.harvey.respiratory.cloud.common.properties.ApplicationProperties;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.harvey.respiratory.cloud.common.utils.RandomUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 一些基础的配置的Bean
 * {@link ObjectMapper}, {@link JacksonUtil},{@link RedissonClient}
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-21 21:13
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public JacksonUtil jacksonUtil(ObjectMapper objectMapper) {
        return new JacksonUtil(objectMapper);
    }

    @Bean
    public RandomUtil randomUtil() {
        return new RandomUtil();
    }


    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        // 配置类
        Config config = new Config();
        // 添加Redis地址, 这里添加了单点的地址
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setPassword(redisProperties.getPassword());
        // 也可以使用config.useClusterServers()添加集群地址
        return Redisson.create(config);
    }
}
