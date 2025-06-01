package org.harvey.respiratory.cloud.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-30 23:46
 */
@Configuration
@ComponentScan("org.harvey.respiratory.cloud.common.advice")
@Import(ApplicationConfig.class)
public class AdviceConfig {
}
