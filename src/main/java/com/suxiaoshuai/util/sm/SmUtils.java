package com.suxiaoshuai.util.sm;

import com.suxiaoshuai.util.Base64Util;
import com.suxiaoshuai.util.sm.sm2.SM2Helper;
import com.suxiaoshuai.util.sm.sm2.SM2Utils;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class SmUtils {
    private static final Logger logger = LoggerFactory.getLogger(SmUtils.class);

    public static String sign(String signData, String merPrivateKey) throws Exception {
        logger.info("收到签名请求：signData={};merPrivateKey={}", signData, merPrivateKey);

        String signMsg;
        try {
            byte[] sign = SM2Utils.sign(signData, merPrivateKey);
            signMsg = Base64Util.encodeToStr(sign);
            signMsg = signMsg.replace("\n", "").replace("\r", "");
        } catch (Exception e) {
            logger.error("签名失败", e);
            throw e;
        }
        logger.info("signData={},sign={}", signData, signMsg);
        return signMsg;
    }

    public static boolean verify(String data, String signVal, String hnapayPublicKey) throws Exception {
        logger.info("收到验签请求：data={};signVal={},key={}", data, signVal, hnapayPublicKey);
        boolean result = false;
        try {
            logger.info("HEX={}", signVal);
            byte[] sign_data = Base64Util.decode(signVal);
            result = SM2Utils.verySign(sign_data, data, hnapayPublicKey);
        } catch (Exception e) {
            logger.error("验签异常：", e);
            throw e;
        }
        logger.info("{}:data={}", result, data);
        return result;
    }

    public static String encrypt(String data, String publicKeyStr) throws Exception {
        logger.info("received encrypted request：data={}", data);
        logger.info("received encrypted request：publicKeyStr={}", publicKeyStr);
        String base64;
        try {
            byte[] pKey = Hex.decode(publicKeyStr);
            SM2Helper helper = new SM2Helper();
            byte[] cipherByte = helper.encrypt(data.getBytes(StandardCharsets.UTF_8), pKey);
            base64 = Base64Util.encodeToStr(cipherByte);
            base64 = base64.replace("\n", "").replace("\r", "");
            logger.info("加密返回：{}", base64);
        } catch (Exception e) {
            logger.error("加密异常", e);
            throw e;
        }
        logger.info("data={},encrypt={}", data, base64);
        return base64;
    }

    public static String decrypt(String data, String privateKey) throws Exception {
        logger.info("received decrypt request：data={}", data);
        logger.info("received decrypt request：privateKey={}", privateKey);
        String decryptData;
        try {
            byte[] pKey = Hex.decode(privateKey);
            SM2Helper helper = new SM2Helper();
            byte[] cipherByte = helper.decrypt(Base64Util.decode(data), pKey);
            decryptData = new String(cipherByte, StandardCharsets.UTF_8);
            logger.info("加密返回：{}", decryptData);
        } catch (Exception e) {
            logger.error("加密异常", e);
            throw e;
        }
        logger.info("data={},encrypt={}", data, decryptData);
        return decryptData;
    }
}
