package org.harvey.respiratory.cloud.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限配置
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 14:05
 */
@Data
@Component
@ConfigurationProperties(prefix = "respiratory.auth")
public class AuthProperties {
    private List<String> includePaths;
    private List<String> rootAuthPaths;
    private List<String> excludePaths;
}
