package com.mall.shiro;

import com.mall.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * 自定义 Realm —— JWT 认证 + RBAC 授权
 */
public class MallRealm extends AuthorizingRealm {

    private JwtUtil jwtUtil;

    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /** 授权 —— 从数据库查角色和权限 */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // TODO: 从数据库查询当前用户的角色和权限，构建 SimpleAuthorizationInfo 返回
        return null;
    }

    /** 认证 —— 校验 JWT Token */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String jwtToken = (String) token.getCredentials();
        if (jwtToken == null || !jwtUtil.validateToken(jwtToken)) {
            throw new AuthenticationException("Token 无效或已过期");
        }
        return new SimpleAuthenticationInfo(jwtToken, jwtToken, getName());
    }
}
