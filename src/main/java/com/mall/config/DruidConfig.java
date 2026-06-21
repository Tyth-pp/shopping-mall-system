package com.mall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Druid 数据源配置 —— 核心参数已在 application.yml 中配置
 * 此类用于类型提示，Spring Boot 自动装配 Druid
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.druid")
public class DruidConfig {
    // Druid 参数由 spring-boot-starter 自动读取 yml 装配，不需要额外代码
}
