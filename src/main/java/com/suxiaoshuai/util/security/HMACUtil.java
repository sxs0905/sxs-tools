package com.suxiaoshuai.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * HMAC加密工具类，提供HMAC-MD5算法的密钥生成和消息认证功能。
 */
public class HMACUtil {
    /** 日志记录器 */
    private static final Logger logger = LoggerFactory.getLogger(HMACUtil.class);
    
    /** HMAC-MD5 算法名称 */
    public static final String KEY_MAC = "HmacMD5";

    /**
     * 初始化HMAC密钥
     * 
     * @return 初始化后的Base64编码密钥字符串，如果发生异常则返回null
     * @throws RuntimeException 如果密钥生成过程中发生错误
     */
    public static String initMacKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);
            SecretKey secreKey = keyGenerator.generateKey();
            return Base64Util.encode(secreKey.getEncoded());
        } catch (Exception e) {
            logger.error("initMacKey error:", e);
            return null;
        }

    }

    /**
     * HMAC加密
     * 
     * @param data 需要加密的字节数组，不能为null
     * @param key  Base64编码的密钥字符串，不能为null
     * @return 加密后的字节数组，如果发生异常则返回null
     * @throws RuntimeException 如果加密过程中发生错误
     */
    public static byte[] encryHMAC(byte[] data, String key) {
        try {
            SecretKey secreKey = new SecretKeySpec(Base64Util.decode(key), KEY_MAC);
            Mac mac = Mac.getInstance(secreKey.getAlgorithm());
            mac.init(secreKey);
            return mac.doFinal();
        } catch (Exception e) {
            logger.error("encryHMAC error:", e);
            return null;
        }

    }
}
