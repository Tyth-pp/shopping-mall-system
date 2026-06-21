package com.mall.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API 文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI mallOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("购物商城系统 API 文档")
                        .description("基于 Spring Boot 3 + Vue 3 购物商城系统接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mall Team")));
    }

}
