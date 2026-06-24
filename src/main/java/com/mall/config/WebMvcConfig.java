package com.mall.config;

import com.mall.interceptor.LoginInterceptor;
import com.mall.interceptor.RepeatSubmitInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * WebMvc 配置 —— 拦截器注册、静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${mall.upload.path}")
    private String uploadPath;

    private final LoginInterceptor loginInterceptor;
    private final RepeatSubmitInterceptor repeatSubmitInterceptor;

    public WebMvcConfig(LoginInterceptor loginInterceptor,
                        RepeatSubmitInterceptor repeatSubmitInterceptor) {
        this.loginInterceptor = loginInterceptor;
        this.repeatSubmitInterceptor = repeatSubmitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器（前台 + 后台均需登录校验）
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/fore/**", "/api/admin/**")
                .excludePathPatterns("/api/common/**",
                        "/api/fore/product/**",
                        "/api/fore/index/**",
                        "/api/fore/user/register",
                        "/api/fore/user/resetPassword");

        // 防重复提交拦截器
        registry.addInterceptor(repeatSubmitInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 上传文件的静态资源映射
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath)
                .setCacheControl(CacheControl.noStore());
    }
}
