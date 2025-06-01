package org.harvey.respiratory.cloud.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-26 23:16
 */
@Data
@ConfigurationProperties(prefix = "respiratory.identity-card")
public class IdentityCardProperties {
    private String citySorted;
    private String cityRaw;
    private Boolean openCodeVerify;
}
