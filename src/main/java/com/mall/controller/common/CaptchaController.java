package com.mall.controller.common;

import com.mall.util.CaptchaUtil;
import com.mall.util.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码接口
 */
@Tag(name = "公共-验证码")
@RestController
@RequestMapping("/api/common/captcha")
public class CaptchaController {

    private final RedisUtil redisUtil;

    public CaptchaController(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/get")
    public Map<String, String> getCaptcha() {
        String code = CaptchaUtil.generateCode();
        String captchaKey = UUID.randomUUID().toString();
        redisUtil.set("captcha:" + captchaKey, code, 5, TimeUnit.MINUTES);

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("code", code); // 开发期间直接返回，生产删除此行
        return result;
    }
}
