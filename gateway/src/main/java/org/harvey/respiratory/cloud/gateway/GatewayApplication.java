package org.harvey.respiratory.cloud.gateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.harvey.respiratory.cloud.common.config.ApplicationConfig;
import org.harvey.respiratory.cloud.common.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 15:17
 */
@SpringBootApplication(excludeName = "dataSource")
@EnableAspectJAutoProxy(exposeProxy = true)
@Import({ApplicationConfig.class, SecurityConfig.class})
@EnableDubbo
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
