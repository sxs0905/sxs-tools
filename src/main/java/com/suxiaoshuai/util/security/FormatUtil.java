package com.suxiaoshuai.util.security;

import com.suxiaoshuai.util.string.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public final class FormatUtil {

    /**
     * 小数 转 百分数
     */
    public static String toPercent(Double str) {
        try {
            return String.valueOf(new BigDecimal(String.valueOf(str)).multiply(new BigDecimal("100")).doubleValue());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 百分数 转 小数
     */
    public static Double toPercent2(String str) {
        if (str.charAt(str.length() - 1) == '%') {
            return new BigDecimal(str.substring(0, str.length() - 1)).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP).doubleValue();
        }
        return 0d;
    }

    /**
     * 获取字符串str 左边len位数
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
     * 获取字符串str 右边len位数
     */
    public static String getRight(String str, int len) {
        if (len <= 0) {
            return "";
        }
        return str.length() <= len ? str : str.substring(str.length() - len);
    }

    /**
     * 首字母变小写
     */
    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        String tail = str.substring(1);
        str = Character.toLowerCase(firstChar) + tail;
        return str;
    }

    /**
     * 首字母变大写
     */
    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        String tail = str.substring(1);
        str = Character.toUpperCase(firstChar) + tail;
        return str;
    }

    /**
     * 将byte[] 转换成字符串
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
     * 数字左边补0 ,obj:要补0的数， length:补0后的长度
     */
    public static String leftPad(Long obj, int length) {
        return String.format("%0" + length + "d", obj);
    }



    /**
     * 将16进制字符串转为转换成字符串
     */
    public static byte[] hex2Bytes(String source) {
        byte[] sourceBytes = new byte[source.length() / 2];
        for (int i = 0; i < sourceBytes.length; i++) {
            sourceBytes[i] = (byte) Integer.parseInt(source.substring(i * 2, i * 2 + 2), 16);
        }
        return sourceBytes;
    }
}