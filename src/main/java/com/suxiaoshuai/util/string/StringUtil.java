package com.suxiaoshuai.util.string;

import com.suxiaoshuai.util.charset.CharsetUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * 扩展自 Apache Commons Lang 的 StringUtils，提供了更多的字符串处理工具方法，
 * 包括手机号码格式化、银行卡号格式化、文件后缀获取等功能。
 */
public class StringUtil extends StringUtils {

    /**
     * 手机号验证
     */
    private static final Pattern mobilePattern = Pattern.compile("(^1\\d{10}$)|(^\\d{3,4}-\\d{7,8}$)");

    /**
     * 判断手机号是否合法
     *
     * @param mobile 待验证的手机号码
     * @return 如果手机号格式正确返回true，否则返回false
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
     * 获取文件的后缀名
     *
     * @param str 文件名或文件路径
     * @return 文件后缀名（不含点号），如"a.jpg"返回"jpg"；如果输入为空或没有后缀则返回null
     */
    public static String getSuffix(String str) {
        String result = null;
        if (StringUtils.isNotBlank(str)) {
            result = StringUtils.substring(str, str.lastIndexOf('.') + 1, str.length());
        }
        return result;
    }

    /**
     * 判断字符串是否为空或为"null"字符串
     *
     * @param s 待检查的字符串
     * @return 如果字符串为空或为"null"字符串则返回true，否则返回false
     */
    public static boolean isBlank(String s) {
        return StringUtils.isBlank(s) || "null".equalsIgnoreCase(s);
    }

    /**
     * 判断字符串是否非空且不为"null"字符串
     *
     * @param s 待检查的字符串
     * @return 如果字符串非空且不为"null"字符串则返回true，否则返回false
     */
    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    /**
     * 格式化手机号码，将中间四位数字替换为星号
     * 
     * @param mobile 待格式化的手机号码
     * @return 格式化后的手机号码，格式如：131****2222
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
     * 银行卡格式化，将中间8位数字替换为星号
     *
     * @param bankCard 待格式化的银行卡号
     * @return 格式化后的银行卡号，格式如：6222****8888，如果输入为空则返回原值
     */
    public static String formatBankCard(String bankCard) {
        if (StringUtils.isBlank(bankCard)) {
            return bankCard;
        }
        String formatStr = "**** ****";
        return StringUtils.substring(bankCard, 0, 4) + formatStr + StringUtils.substring(bankCard, StringUtils.length(bankCard) - 4);
    }

    /**
     * 判断字符串是否为空或为"null"字符串
     * 
     * @param s 待检查的字符串
     * @return 如果字符串为空或为"null"字符串则返回true，否则返回false
     * @deprecated 请使用 {@link #isBlank(String)} 方法替代
     */
    public static boolean stringIsNull(String s) {
        return StringUtils.isBlank(s) || "null".equalsIgnoreCase(s);
    }

    /**
     * 从数字+单位的字符串中提取数字部分
     *
     * @param numWithUnit 包含数字和单位的字符串，例如"700 公里"、"0005.2吨"
     * @return 提取出的数字字符串，如果输入为空则返回空字符串
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
     * 从数字+单位的字符串中提取单位部分
     *
     * @param numWithUnit 包含数字和单位的字符串，例如"700 公里"、"0005.2吨"
     * @return 提取出的单位字符串，如果输入为空或没有单位部分则返回null
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

    /**
     * 移除字符串中的所有空白字符（包括空格、制表符、换行符等）
     *
     * @param str 待处理的字符串
     * @return 移除所有空白字符后的字符串，如果输入为null可能抛出NullPointerException
     * @throws NullPointerException 如果输入字符串为null
     */
    public static String trimAll(String str) {
        return str.replaceAll("\\s+", "");
    }

    /**
     * 使用UTF-8编码对字节数组进行Base64编码并创建字符串
     *
     * @param data 待编码的字节数组
     * @return 使用UTF-8编码的Base64字符串，如果输入为null则可能返回null
     */
    public static String newString(byte[] data) {
        return newString(data, CharsetUtil.UTF_8);
    }

    /**
     * 使用指定编码对字节数组进行Base64编码并创建字符串
     *
     * @param data   待编码的字节数组
     * @param encode 字符编码，如UTF-8、GBK等
     * @return 使用指定编码的Base64字符串，如果输入为null则可能返回null
     */
    public static String newString(byte[] data, String encode) {
        return org.apache.commons.codec.binary.StringUtils.newString(Base64.encodeBase64(data), encode);
    }

    /**
     * 生成与原字符串长度相同的占位字符串
     *
     * @param original    原字符串
     * @param placeholder 占位字符（必须是单个字符）
     * @return 由占位字符重复组成的字符串，长度与原字符串相同；如果原字符串为空则返回null
     */
    public static String generatePlaceholder(String original, char placeholder) {
        return isBlank(original) ? null : String.valueOf(placeholder).repeat(original.length());
    }
}
