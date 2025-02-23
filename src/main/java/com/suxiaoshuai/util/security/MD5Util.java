package com.suxiaoshuai.util.security;


import com.suxiaoshuai.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);

    private final static String SHA_256 = "SHA-256";
    private final static String MD5 = "MD5";
    private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    public static String MD5(String input) {
        return MD5(input, Charset.defaultCharset());
    }

    public static String MD5(String input, String charset) {
        return MD5(input, Charset.forName(charset));
    }

    public static String MD5(String input, Charset charset) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            logger.error("input:{},charset:{} ,MD5 error", input, charset, e);
        }

        char[] str = new char[0];
        if (md != null && input != null) {
            md.update(input.getBytes(charset));

            byte[] tmp = md.digest();
            str = new char[32];

            int k = 0;
            for (int i = 0; i < 16; ++i) {
                byte byte0 = tmp[i];
                str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
                str[(k++)] = hexDigits[(byte0 & 0xF)];
            }
        }

        return new String(str);
    }

    /**
     * 对字符串加密,加密算法使用SHA-256
     */
    public static String SHA256(String strSrc, String charsetName) {
        try {
            Charset charset;
            if (StringUtil.isBlank(charsetName)) {
                charset = Charset.defaultCharset();
            } else {
                charset = Charset.forName(charsetName);
            }
            byte[] bt = strSrc.getBytes(charset);
            MessageDigest md = MessageDigest.getInstance(SHA_256);
            md.update(bt);
            return bytes2Hex(md.digest());
        } catch (Exception e) {
            return null;
        }
    }

    private static String bytes2Hex(byte[] bts) {
        StringBuilder des = new StringBuilder();
        String tmp = null;
        for (byte bt : bts) {
            tmp = (Integer.toHexString(bt & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }
}
