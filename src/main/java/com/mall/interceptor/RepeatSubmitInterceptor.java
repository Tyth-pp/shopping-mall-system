package com.mall.interceptor;

import com.mall.annotation.RepeatSubmit;
import com.mall.exception.BusinessException;
import com.mall.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 防重复提交拦截器 —— 配合 @RepeatSubmit 注解
 * 原理：请求前检查 Redis 是否有该 key，没有则写入，有则拦截
 */
@Slf4j
@Component
public class RepeatSubmitInterceptor implements HandlerInterceptor {

    private final RedisUtil redisUtil;

    public RepeatSubmitInterceptor(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RepeatSubmit annotation = handlerMethod.getMethodAnnotation(RepeatSubmit.class);
        if (annotation == null) {
            return true;
        }

        String key = buildKey(request);
        if (redisUtil.hasKey(key)) {
            throw new BusinessException("请勿重复提交，请稍后再试");
        }

        redisUtil.set(key, "1", annotation.expire(), TimeUnit.MILLISECONDS);
        return true;
    }

    private String buildKey(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        String uid = userId != null ? String.valueOf(userId) : "anon";
        return "repeat_submit:" + uid + ":"
                + request.getRequestURI() + ":" + request.getMethod();
    }
}
