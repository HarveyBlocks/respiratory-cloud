package org.harvey.respiratory.cloud.di.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan(basePackages = "org.harvey.respiratory.cloud.di.server.dao")
//@com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo
@org.apache.dubbo.config.spring.context.annotation.EnableDubbo
public class ServiceDoctorInterviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDoctorInterviewApplication.class, args);
    }

}
