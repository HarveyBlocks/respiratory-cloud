package org.harvey.respiratory.cloud.role.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.harvey.respiratory.cloud.common.properties.IdentityCardProperties;
import org.harvey.respiratory.cloud.common.utils.identifier.AddressOnIdLoader;
import org.harvey.respiratory.cloud.common.utils.identifier.IdentifierIdPredicate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-26 22:57
 */
@Configuration
@EnableConfigurationProperties({IdentityCardProperties.class})
public class IdentifierCardConfig {
    @Resource
    private IdentityCardProperties identityCardProperties;

    @Bean
    public AddressOnIdLoader addressOnIdLoader(ObjectMapper objectMapper) {
        return new AddressOnIdLoader(objectMapper, identityCardProperties.getCitySorted());
    }

    @Bean
    public IdentifierIdPredicate identifierIdPredicate(AddressOnIdLoader addressOnIdLoader) {
        return new IdentifierIdPredicate(identityCardProperties.getOpenCodeVerify(), addressOnIdLoader);
    }


}
