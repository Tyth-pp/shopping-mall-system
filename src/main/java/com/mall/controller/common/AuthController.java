package com.mall.controller.common;

import com.mall.annotation.Log;
import com.mall.service.AdminService;
import com.mall.service.UserService;
import com.mall.util.RedisUtil;
import com.mall.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "公共-登录认证")
@RestController
@RequestMapping("/api/common/auth")
public class AuthController {

    private final UserService userService;
    private final AdminService adminService;
    private final RedisUtil redisUtil;

    public AuthController(UserService userService, AdminService adminService, RedisUtil redisUtil) {
        this.userService = userService;
        this.adminService = adminService;
        this.redisUtil = redisUtil;
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @Log(value = "用户登录", type = "LOGIN")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        validateCaptcha(params.get("captchaKey"), params.get("captchaCode"));
        Map<String, Object> result = userService.login(
                params.get("username"), params.get("password"));
        return Result.success(result);
    }

    @Operation(summary = "管理员登录")
    @PostMapping("/adminLogin")
    @Log(value = "管理员登录", type = "LOGIN")
    public Result<Map<String, Object>> adminLogin(@RequestBody Map<String, String> params) {
        validateCaptcha(params.get("captchaKey"), params.get("captchaCode"));
        Map<String, Object> result = adminService.login(
                params.get("username"), params.get("password"));
        return Result.success(result);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @Operation(summary = "未登录提示")
    @GetMapping("/unLogin")
    public Result<Void> unLogin() {
        return Result.fail(401, "未登录或登录已过期");
    }

    @Operation(summary = "无权限提示")
    @GetMapping("/unauthorized")
    public Result<Void> unauthorized() {
        return Result.fail(403, "无权限访问");
    }

    /** 验证码校验：从 Redis 读取 → 比对 → 一次性删除 */
    private void validateCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            throw new com.mall.exception.BusinessException("验证码不能为空");
        }
        String cachedCode = (String) redisUtil.get("captcha:" + captchaKey);
        if (cachedCode == null) {
            throw new com.mall.exception.BusinessException("验证码已过期，请重新获取");
        }
        if (!cachedCode.equalsIgnoreCase(captchaCode)) {
            throw new com.mall.exception.BusinessException("验证码错误");
        }
        redisUtil.delete("captcha:" + captchaKey);
    }
}
