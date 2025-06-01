package org.harvey.respiratory.cloud.upi.server.config;

import org.harvey.respiratory.cloud.common.config.MvcConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 导入common的一些配置
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-30 20:32
 */
@Configuration
@Import({MvcConfig.class})
public class ConfigImporter {
}
