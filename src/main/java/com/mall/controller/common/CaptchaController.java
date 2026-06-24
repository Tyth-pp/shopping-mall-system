package com.mall.controller.common;

import com.mall.util.CaptchaUtil;
import com.mall.util.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
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
        String captchaKey = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        redisUtil.set("captcha:" + captchaKey, code, 5, TimeUnit.MINUTES);

        log.info("验证码已生成: captchaKey={}, code={}", captchaKey, code);

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        return result;
    }
}
