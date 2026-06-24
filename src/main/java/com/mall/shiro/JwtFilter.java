package com.mall.shiro;

import com.mall.util.JwtUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JwtFilter implements Filter {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String token = extractToken(request);
        if (token == null) {
            log.debug("JwtFilter: 无 Authorization 头, path={}", request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        if (!jwtUtil.validateToken(token)) {
            log.warn("JwtFilter: Token 校验失败, path={}", request.getRequestURI());
            writeUnauthorized(response);
            return;
        }

        // Token 有效，设置用户信息
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        log.debug("JwtFilter: Token 有效, userId={}, username={}, path={}", userId, username, request.getRequestURI());

        // Shiro 登录
        try {
            org.apache.shiro.SecurityUtils.getSubject().login(new JwtToken(token));
        } catch (Exception e) {
            log.error("JwtFilter: Shiro login 异常, path={}, error={}", request.getRequestURI(), e.getMessage());
            writeUnauthorized(response);
            return;
        }

        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(TOKEN_HEADER);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\",\"data\":null}");
    }
}
