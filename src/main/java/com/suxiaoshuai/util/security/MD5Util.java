package com.suxiaoshuai.util.security;

import com.suxiaoshuai.util.charset.CharsetUtil;
import com.suxiaoshuai.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类，提供MD5和SHA-256加密功能
 * 主要功能：
 * <ul>
 * <li>MD5加密：支持默认字符集和指定字符集</li>
 * <li>SHA-256加密：支持指定字符集</li>
 * </ul>
 */
public class MD5Util {

    /** 日志记录器 */
    private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);

    /** SHA-256 算法名称 */
    private final static String SHA_256 = "SHA-256";

    /** MD5 算法名称 */
    private final static String MD5 = "MD5";

    /** 用于将字节转换为十六进制字符的字符数组 */
    private final static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };

    /**
     * 使用默认字符集对字符串进行MD5加密
     * 
     * @param input 需要加密的字符串，如果为null则返回空字符串
     * @return 加密后的32位小写十六进制字符串
     */
    public static String MD5(String input) {
        return MD5(input, CharsetUtil.DEFAULT_CHARSET);
    }

    /**
     * 使用指定字符集名称对字符串进行MD5加密
     * 
     * @param input   需要加密的字符串，如果为null则返回空字符串
     * @param charset 字符集名称，例如："UTF-8"、"GBK"等
     * @return 加密后的32位小写十六进制字符串
     * @throws IllegalArgumentException 如果指定的字符集名称无效
     */
    public static String MD5(String input, String charset) {
        return MD5(input, Charset.forName(charset));
    }

    /**
     * 使用指定字符集对字符串进行MD5加密
     * 该方法会将输入字符串按照指定的字符集编码后进行MD5加密，并返回32位小写十六进制字符串。
     * 如果输入为null或加密过程发生异常，将返回空字符串。
     * 
     * @param input   需要加密的字符串，如果为null则返回空字符串
     * @param charset 字符集对象，如果为null则使用默认字符集（UTF-8）
     * @return 加密后的32位小写十六进制字符串，如果输入为null或加密失败则返回空字符串
     * @see java.nio.charset.Charset
     * @see java.security.MessageDigest
     */
    public static String MD5(String input, Charset charset) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            logger.error("input:{},charset:{} ,MD5 error", input, charset, e);
        }
        charset = charset == null ? Charset.forName(CharsetUtil.DEFAULT_CHARSET) : charset;
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
     * 对字符串进行SHA-256加密
     * 
     * @param strSrc      需要加密的字符串，不能为null
     * @param charsetName 字符集名称，如果为空则使用系统默认字符集
     * @return 加密后的十六进制字符串，加密失败返回null
     * @throws IllegalArgumentException 如果指定的字符集名称无效
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

    /**
     * 将字节数组转换为十六进制字符串
     * 
     * @param bts 需要转换的字节数组，不能为null
     * @return 转换后的十六进制字符串，每个字节转换为两位十六进制数
     */
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
