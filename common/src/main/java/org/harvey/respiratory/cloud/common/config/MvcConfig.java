package org.harvey.respiratory.cloud.common.config;

import org.harvey.respiratory.cloud.common.interceptor.LoginThreadLocalInterceptor;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-27 16:15
 */
@Configuration
@ComponentScan("org.harvey.respiratory.cloud.common.advice.exception")
@Import(ApplicationConfig.class)
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private JacksonUtil jacksonUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        LoginThreadLocalInterceptor loginInterceptor = new LoginThreadLocalInterceptor(jacksonUtil);
        registry.addInterceptor(loginInterceptor);
    }
}