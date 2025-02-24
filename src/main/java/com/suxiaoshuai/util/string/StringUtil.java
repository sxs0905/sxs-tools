package com.suxiaoshuai.util.string;

import com.suxiaoshuai.util.charset.CharsetUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil extends StringUtils {

    /**
     * 手机号验证
     */
    private static final Pattern mobilePattern = Pattern.compile("(^1\\d{10}$)|(^\\d{3,4}-\\d{7,8}$)");

    /**
     * Check mobile boolean.
     * 判断手机号输入是否合法
     *
     * @param mobile the mobile
     * @return the boolean
     */
    public static boolean checkMobile(String mobile) {
        boolean passFlag = true;
        Matcher m = mobilePattern.matcher(mobile);
        if (StringUtils.isBlank(mobile) || !m.matches()) {
            passFlag = false;
        }
        return passFlag;
    }

    /**
     * Gets suffix.获取文件的后缀名(注：a.jpg -> jpg)
     *
     * @param str the str
     * @return the suffix
     */
    public static String getSuffix(String str) {
        String result = null;
        if (StringUtils.isNotBlank(str)) {
            result = StringUtils.substring(str, str.lastIndexOf('.') + 1, str.length());
        }
        return result;
    }

    public static boolean isBlank(String s) {
        return StringUtils.isBlank(s) || "null".equalsIgnoreCase(s);
    }


    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }


    /**
     * 格式化手机号码 131****2222
     */
    public static String formatMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return mobile;
        }
        Matcher m = mobilePattern.matcher(mobile);
        if (!m.matches()) {
            return mobile;
        }
        return StringUtils.substring(mobile, 0, 3) + "****" + StringUtils.substring(mobile, StringUtils.length(mobile) - 4);
    }


    /**
     * 银行卡格式化
     *
     * @param bankCard
     * @return
     */
    public static String formatBankCard(String bankCard) {
        if (StringUtils.isBlank(bankCard)) {
            return bankCard;
        }
        String formatStr = "**** ****";
        return StringUtils.substring(bankCard, 0, 4) + formatStr + StringUtils.substring(bankCard, StringUtils.length(bankCard) - 4);
    }

    public static boolean stringIsNull(String s) {
        return StringUtils.isBlank(s) || "null".equalsIgnoreCase(s);
    }

    /**
     * 从数字+单位中获取数字 如：700 公里-->700 0005.2吨 --> 0005.2
     *
     * @param numWithUnit 原始数据 700 公里
     */
    public static String getNumFromNumWithUnit(String numWithUnit) {
        if (isBlank(numWithUnit)) {
            return "";
        }
        numWithUnit = trimAll(numWithUnit);
        String regex = "^\\d*\\.?\\d+";
        Pattern pattern = Pattern.compile(regex);
        // 使用Matcher去保存匹配到的内容，matcher()方法去匹配字符串
        Matcher matcher = pattern.matcher(numWithUnit);
        String numStr = null;
        while (matcher.find()) {
            numStr = matcher.group();
        }
        return numStr;
    }

    /**
     * 从数字+单位中获取单位 如：700 公里-->公里
     *
     * @param numWithUnit 原始数据 700 公里
     */
    public static String getUnit(String numWithUnit) {
        if (isBlank(numWithUnit)) {
            return null;
        }
        String unit = null;
        numWithUnit = trimAll(numWithUnit);
        String numStr = getNumFromNumWithUnit(numWithUnit);
        if (numWithUnit.length() > numStr.length()) {
            unit = numWithUnit.substring(numStr.length());
        }
        return unit;
    }

    public static String trimAll(String str) {
        return str.replaceAll("\\s+", "");
    }


    public static String newString(byte[] data) {
        return newString(data, CharsetUtil.UTF_8);
    }

    public static String newString(byte[] data, String encode) {
        return org.apache.commons.codec.binary.StringUtils.newString(Base64.encodeBase64(data), encode);
    }


    /**
     * 生成与原字符串长度相同的占位字符串
     *
     * @param original    原字符串
     * @param placeholder 占位字符（必须是单个字符）
     */
    public static String generatePlaceholder(String original, char placeholder) {
        return isBlank(original) ? null : String.valueOf(placeholder).repeat(original.length());
    }
}
