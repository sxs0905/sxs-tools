package com.suxiaoshuai.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HMACUtil {

    private static final Logger logger = LoggerFactory.getLogger(HMACUtil.class);
    public static final String KEY_MAC = "HmacMD5";

    /***
     * 初始化HMAC密钥
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
     */
    public static byte[] encryHMAC(byte[] data, String key) throws Exception {
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
