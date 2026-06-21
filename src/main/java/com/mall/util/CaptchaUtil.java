package com.mall.util;

import java.util.Random;

/**
 * 验证码生成工具 —— 生成 4 位数字验证码
 */
public class CaptchaUtil {

    private static final Random RANDOM = new Random();

    /**
     * 生成 4 位数字验证码
     */
    public static String generateCode() {
        int code = RANDOM.nextInt(9000) + 1000;
        return String.valueOf(code);
    }
}
