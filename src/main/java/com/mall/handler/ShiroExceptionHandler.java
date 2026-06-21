package com.mall.handler;

import com.mall.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shiro 异常处理器 —— 捕获 Shiro 抛出的认证/授权异常
 */
@Slf4j
@RestControllerAdvice
public class ShiroExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("认证失败 [{}] {}", request.getRequestURI(), e.getMessage());
        return Result.fail(401, "未登录或登录已过期");
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleUnauthorizedException(UnauthorizedException e, HttpServletRequest request) {
        log.warn("权限不足 [{}] {}", request.getRequestURI(), e.getMessage());
        return Result.fail(403, "无权限访问");
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAuthorizationException(AuthorizationException e, HttpServletRequest request) {
        log.warn("授权异常 [{}] {}", request.getRequestURI(), e.getMessage());
        return Result.fail(403, "授权失败");
    }
}
