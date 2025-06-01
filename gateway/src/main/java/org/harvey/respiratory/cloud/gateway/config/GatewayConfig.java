package org.harvey.respiratory.cloud.gateway.config;

import org.harvey.respiratory.cloud.common.config.ApplicationConfig;
import org.harvey.respiratory.cloud.common.security.SecurityConfig;
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
@Import({ApplicationConfig.class, SecurityConfig.class})
public class GatewayConfig {

}
