package com.suxiaoshuai.util.security;

import com.suxiaoshuai.util.string.StringUtil;

import java.security.SecureRandom;

public class SaltUtil {
    private static final int SALT_LENGTH = 16;       // Salt 长度（字节）
    /**
     * 获取16字节盐
     */
    public static String salt16() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return StringUtil.newString(salt);
    }
}
