package com.suxiaoshuai.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 格式化工具类
 *
 * 提供各种数据格式化操作，包括：
 * <ul>
 *     <li>数字格式化（百分比转换）</li>
 *     <li>字符串处理（截取、大小写转换）</li>
 *     <li>进制转换（字节数组与十六进制互转）</li>
 * </ul>
 *
 * @author suxiaoshuai
 * @since 1.0.0
 */
public final class FormatUtil {


    private static final Logger logger = LoggerFactory.getLogger(FormatUtil.class);

    /**
     * 小数转百分数
     *
     * @param str 需要转换的小数
     * @return 转换后的百分数字符串，转换失败返回null
     */
    public static String toPercent(Double str) {
        try {
            return String.valueOf(new BigDecimal(String.valueOf(str)).multiply(new BigDecimal("100")).doubleValue());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 百分数转小数
     *
     * @param str 需要转换的百分数字符串（支持带%和不带%的格式）
     * @return 转换后的小数，如果不是有效的数字格式返回0
     */
    public static Double percentToDecimal(String str) {
        try {
            if (str == null || str.isEmpty()) {
                return 0d;
            }
            // 去除所有空格
            str = str.trim();
            // 处理带百分号的情况
            if (str.endsWith("%")) {
                str = str.substring(0, str.length() - 1).trim();
            }
            // 处理不带百分号的情况，直接转换为小数
            return new BigDecimal(str).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP).doubleValue();
        } catch (Exception e) {
            logger.error("format percent:{},error", str, e);
            return 0d;
        }
    }

    /**
     * 获取字符串左边指定长度的子串
     *
     * @param str 源字符串
     * @param len 需要获取的长度
     * @return 截取后的字符串
     */
    public static String getLeft(String str, int len) {
        if (len <= 0)
            return "";
        if (str.length() <= len)
            return str;
        else
            return str.substring(0, len);
    }

    /**
     * 获取字符串右边指定长度的子串
     *
     * @param str 源字符串
     * @param len 需要获取的长度
     * @return 截取后的字符串
     */
    public static String getRight(String str, int len) {
        if (len <= 0) {
            return "";
        }
        return str.length() <= len ? str : str.substring(str.length() - len);
    }

    /**
     * 将字符串的首字母转换为小写
     *
     * @param str 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        String tail = str.substring(1);
        str = Character.toLowerCase(firstChar) + tail;
        return str;
    }

    /**
     * 将字符串的首字母转换为大写
     *
     * @param str 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        String tail = str.substring(1);
        str = Character.toUpperCase(firstChar) + tail;
        return str;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param srcBytes 源字节数组
     * @return 转换后的十六进制字符串
     */
    public static String byte2Hex(byte[] srcBytes) {
        StringBuilder hexRetSB = new StringBuilder();
        for (byte b : srcBytes) {
            String hexString = Integer.toHexString(0x00ff & b);
            hexRetSB.append(hexString.length() == 1 ? 0 : "").append(hexString);
        }
        return hexRetSB.toString();
    }

    /**
     * 数字左边补0
     *
     * @param obj    要补0的数字
     * @param length 补0后的总长度
     * @return 补0后的字符串
     */
    public static String leftPad(Long obj, int length) {
        return String.format("%0" + length + "d", obj);
    }


    /**
     * 将十六进制字符串转换为字节数组
     *
     * @param source 十六进制字符串
     * @return 转换后的字节数组
     */
    public static byte[] hex2Bytes(String source) {
        byte[] sourceBytes = new byte[source.length() / 2];
        for (int i = 0; i < sourceBytes.length; i++) {
            sourceBytes[i] = (byte) Integer.parseInt(source.substring(i * 2, i * 2 + 2), 16);
        }
        return sourceBytes;
    }
}