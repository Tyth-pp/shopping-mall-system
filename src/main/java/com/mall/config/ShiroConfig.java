package com.mall.config;

import com.mall.shiro.JwtFilter;
import com.mall.shiro.MallRealm;
import com.mall.util.JwtUtil;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro 核心配置 —— Realm + SecurityManager + Filter 注册
 */
@Configuration
public class ShiroConfig {

    private final JwtUtil jwtUtil;

    public ShiroConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // ==================== JwtFilter 注册（标准 Servlet Filter，跑在 ShiroFilter 之前） ====================

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtFilter(jwtUtil));
        registration.addUrlPatterns("/api/fore/*", "/api/admin/*");
        registration.setName("jwtFilter");
        registration.setOrder(1); // 在 ShiroFilter 之前执行
        return registration;
    }

    // ==================== Shiro Realm ====================

    @Bean
    public MallRealm mallRealm() {
        MallRealm realm = new MallRealm();
        realm.setJwtUtil(jwtUtil);
        return realm;
    }

    // ==================== SecurityManager ====================

    @Bean
    public DefaultWebSecurityManager securityManager(Realm realm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);

        // 关闭 Session，JWT 无状态模式
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator evaluator = new DefaultSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(evaluator);
        manager.setSubjectDAO(subjectDAO);

        return manager;
    }

    // ==================== ShiroFilter（仅保留权限控制，认证由 JwtFilter 完成） ====================

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        // JwtFilter 已完成认证，这里全部放行；细粒度权限由 @RequiresPermissions 控制
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/**", "anon");
        factoryBean.setFilterChainDefinitionMap(filterMap);

        return factoryBean;
    }
}
