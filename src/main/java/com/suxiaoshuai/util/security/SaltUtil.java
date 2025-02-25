package com.suxiaoshuai.util.security;

import com.suxiaoshuai.util.string.StringUtil;

import java.security.SecureRandom;

/**
 * 盐值生成工具类
 * 提供安全的随机盐值生成功能，用于加密和哈希处理。
 * 使用 {@link SecureRandom} 生成加密安全的随机数。
 */
public class SaltUtil {
    private static final int SALT_LENGTH = 16;       // Salt 长度（字节）
    /**
     * 获取16字节盐
     * 
     * @return 返回16字节的随机盐值字符串
     */
    public static String salt16() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return StringUtil.newString(salt);
    }
}
