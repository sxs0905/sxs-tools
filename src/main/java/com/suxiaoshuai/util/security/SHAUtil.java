package com.suxiaoshuai.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

public class SHAUtil {

    private static final Logger logger = LoggerFactory.getLogger(SHAUtil.class);

    public static final String KEY_SHA = "SHA";
    public static final String ALGORITHM = "SHA-256";

    /***
     * SHA加密（比MD5更安全）
     */
    public static byte[] encryptSHA(byte[] data) {
        try {
            MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
            sha.update(data);
            return sha.digest();
        } catch (Exception e) {
            logger.error("encrypt error:", e);
            return null;
        }

    }


    public static String SHAEncrypt(final String content) {
        try {
            MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
            byte[] sha_byte = sha.digest(content.getBytes());
            StringBuilder hexValue = new StringBuilder();
            for (byte b : sha_byte) {
                // 将其中的每个字节转成十六进制字符串：byte类型的数据最高位是符号位，通过和0xff进行与操作，转换为int类型的正整数。
                String toHexString = Integer.toHexString(b & 0xff);
                hexValue.append(toHexString.length() == 1 ? "0" + toHexString : toHexString);
            }
            return hexValue.toString();
        } catch (Exception e) {
            logger.error("encrypt error:", e);
        }
        return null;
    }


    // SHA-256加密
    public static String SHA256Encrypt(String sourceStr) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            if (null != md) {
                md.update(sourceStr.getBytes());
                return getDigestStr(md.digest());
            }
        } catch (Exception e) {
            logger.error("encrypt error:", e);
        }
        return null;
    }

    private static String getDigestStr(byte[] origBytes) {
        String tempStr = null;
        StringBuilder stb = new StringBuilder();
        for (byte origByte : origBytes) {
            tempStr = Integer.toHexString(origByte & 0xff);
            if (tempStr.length() == 1) {
                stb.append("0");
            }
            stb.append(tempStr);

        }
        return stb.toString();
    }
}
