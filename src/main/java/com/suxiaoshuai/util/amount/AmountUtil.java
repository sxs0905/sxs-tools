package com.suxiaoshuai.util.amount;

import com.suxiaoshuai.exception.SxsToolsException;
import com.suxiaoshuai.util.string.StringUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 金额工具类
 * 提供金额相关的工具方法，包括：
 * <ul>
 * <li>金额中文大写转换</li>
 * <li>金额小写转换</li>
 * <li>金额格式化处理</li>
 * </ul>
 * 中文数位：个，十，百，千，万，十万，百万，千万，亿，十亿，百亿，千亿，万亿，十万亿，百万亿，千万亿，亿亿
 *
 * @author suxiaoshuai
 * @since 1.0.0
 */
public class AmountUtil {

    private static final Logger logger = LoggerFactory.getLogger(AmountUtil.class);

    /**
     * 数字大写
     */
    private static final String[] NUMBER_UPPER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

    /**
     * 金额位数对应的数
     */
    private static final String[] AMOUNT_UNITS = {"元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万亿",
            "拾", "佰", "仟", "亿亿"};

    /**
     * 小数部分
     */
    private static final String[] DECIMAL_UNITS = {"角", "分"};
    /**
     * 小数标识
     */
    private static final String POINT_FLAG = ".";
    /**
     * 元整标识
     */
    private static final String YUAN_JUST_FLAG = ".00";

    /**
     * 金额中单个数字当前值为0
     */
    private static final String AMOUNT_CURRENT_INDEX_VALUE_IS_ZERO = "0";
    /**
     * 元在单位的索引
     */
    private static final int AMOUNT_UNIT_NO_YUAN = 0;

    /**
     * 万在单位的索引
     */

    private static final int AMOUNT_UNIT_NO_TEN_THOUSAND = 4;
    /**
     * 亿在单位的索引
     */
    private static final int AMOUNT_UNIT_NO_ONE_HUNDRED_MILLION = 8;
    /**
     * 亿在单位的索引
     */
    private static final int AMOUNT_UNIT_NO_TEN_THOUSAND_MILLION = 12;
    /**
     * 整数部分只有 0 比如 0.2
     */
    private static final String AMOUNT_INTEGER_PART_ZERO = "0";
    /**
     * 支持的最大金额 99999999999999999.99
     * 玖亿亿玖仟玖佰玖拾玖万亿玖仟玖佰玖拾玖亿玖仟玖佰玖拾玖万玖仟玖佰玖拾玖元玖角玖分
     */
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999999999999999.99");

    private static final Map<Character, Integer> digitMap = new HashMap<>();

    /**
     * 默认保留的小数位数
     */
    private static final int DEFAULT_DECIMAL_PLACES = 2;


    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = ThreadLocal.withInitial(() -> {
        /*
          - format.setGroupingSize(3) ：
          - 设置数字分组的大小为3位
          - 比如：1234567 会被分成 1,234,567
          - 如果设置为4，则会变成 12,3456,7
          - format.setGroupingUsed(true) ：
          - 启用数字分组功能
          - true：启用千分位分隔符（如：1,234,567）
          - false：禁用千分位分隔符（如：1234567）
         */
        DecimalFormat format = new DecimalFormat();
        format.setGroupingSize(3);
        format.setGroupingUsed(true);
        return format;
    });

    static {
        digitMap.put('零', 0);
        digitMap.put('壹', 1);
        digitMap.put('贰', 2);
        digitMap.put('叁', 3);
        digitMap.put('肆', 4);
        digitMap.put('伍', 5);
        digitMap.put('陆', 6);
        digitMap.put('柒', 7);
        digitMap.put('捌', 8);
        digitMap.put('玖', 9);
    }

    /**
     * 金额转中文大写
     * 金额支持小数点后两位
     * 默认带整，不带零角
     *
     * @param amount 待转换金额
     * @return 大写金额
     */
    public static String toUpper(BigDecimal amount) {
        return toUpper(amount, true, false);
    }

    /**
     * 金额转中文大写
     * 金额支持小数点后两位
     * 只有元不需要整
     * 是否带零角 1.01 --> 壹元零角壹分/壹元壹分
     *
     * @param amount       待转换金额
     * @param withZeroJiao 是否带零角
     * @return 大写金额
     */
    public static String toUpperNoEven(BigDecimal amount, Boolean withZeroJiao) {
        return toUpper(amount, false, withZeroJiao);
    }

    /**
     * 金额转中文大写
     * 金额支持小数点后两位
     * 只有元需要整
     * 是否带零角 1.01 --> 壹元零角壹分/壹元壹分
     *
     * @param amount       待转换金额
     * @param withZeroJiao 是否带零角
     * @return 大写金额
     */
    public static String toUpperWithEven(BigDecimal amount, Boolean withZeroJiao) {
        return toUpper(amount, true, withZeroJiao);
    }

    /**
     * 金额转中文大写的核心实现方法
     *
     * @param amount       待转换金额
     * @param needEven     是否需要在只有整数时添加"整"字，true 表示需要添加
     * @param withZeroJiao 是否在角位为零时显示"零角"，true 表示显示
     * @return 转换后的中文大写金额字符串，如果金额为 null 则返回 null
     * @throws SxsToolsException 当金额超过最大支持金额时抛出异常
     */
    private static String toUpper(BigDecimal amount, Boolean needEven, Boolean withZeroJiao) {
        logger.info("toUpper amount:{}, needEven:{}, withZeroJiao:{}", amount, needEven, withZeroJiao);
        if (amount == null) {
            return null;
        }
        try {
            if (MAX_AMOUNT.compareTo(amount) < 0) {
                throw new SxsToolsException("The amount is too large");
            }
            needEven = needEven != null && needEven;
            StringBuilder sb = new StringBuilder();

            // 只取小数点后两位
            amount = amount.setScale(2, RoundingMode.DOWN);
            // 字符化金额
            String amountStr = String.valueOf(amount);
            // 金额总长度
            int length = amountStr.length();
            int pointIndex = length - 3;

            // 金额整数部分
            String amountIntegerPart = amountStr.substring(0, pointIndex);
            int amountIntegerPartLength = amountIntegerPart.length();
            if (!AMOUNT_INTEGER_PART_ZERO.equals(amountIntegerPart)) {
                for (int i = 0; i < amountIntegerPartLength; i++) {

                    String currentIndexValue = amountIntegerPart.substring(i, i + 1);
                    // 金额单位在单位数组里的索引
                    int amountUnitIndex = amountIntegerPartLength - i - 1;
                    String amountUnit = AMOUNT_UNITS[amountUnitIndex];
                    if (!AMOUNT_CURRENT_INDEX_VALUE_IS_ZERO.equals(currentIndexValue)) {
                        if (i != 0
                                && AMOUNT_CURRENT_INDEX_VALUE_IS_ZERO.equals(amountIntegerPart.substring(i - 1, i))) {
                            sb.append(NUMBER_UPPER[0]);
                        }
                        sb.append(NUMBER_UPPER[Integer.parseInt(currentIndexValue)]).append(amountUnit);
                    } else {
                        if (amountUnitIndex == AMOUNT_UNIT_NO_ONE_HUNDRED_MILLION
                                || amountUnitIndex == AMOUNT_UNIT_NO_TEN_THOUSAND
                                || amountUnitIndex == AMOUNT_UNIT_NO_TEN_THOUSAND_MILLION
                                || amountUnitIndex == AMOUNT_UNIT_NO_YUAN) {
                            sb.append(amountUnit);
                        }
                    }
                }
            }
            // 当小数后两位都不为00时代表该金额包含角分
            String firstUpperAmount = "";
            String lastUpperAmount = "";
            boolean containPoint = amountStr.contains(POINT_FLAG) && !amountStr.contains(YUAN_JUST_FLAG);
            if (containPoint) {
                pointIndex = amountStr.indexOf(POINT_FLAG);
                // 金额小数部分
                String amountDecimalPart = amountStr.substring(pointIndex + 1, length);
                int decimalLength = amountDecimalPart.length();
                // 小数第一位金额值
                String decimalFirstIndexValue = amountDecimalPart.substring(0, 1);
                // 是否带零角
                if (withZeroJiao || !AMOUNT_CURRENT_INDEX_VALUE_IS_ZERO.equals(decimalFirstIndexValue)) {
                    firstUpperAmount = NUMBER_UPPER[Integer.parseInt(decimalFirstIndexValue)] + DECIMAL_UNITS[0];
                }
                // 小数部分最后一位
                String decimalLastIndexValue = amountDecimalPart.substring(decimalLength - 1, decimalLength);
                if (!AMOUNT_CURRENT_INDEX_VALUE_IS_ZERO.equals(decimalLastIndexValue)) {
                    lastUpperAmount = NUMBER_UPPER[Integer.parseInt(decimalLastIndexValue)] + DECIMAL_UNITS[1];
                }
            }
            sb.append(needEven && !containPoint ? "整" : "").append(firstUpperAmount).append(lastUpperAmount);
            return sb.toString().replace("亿万", "亿");
        } catch (Exception e) {
            logger.info("toUpper amount:{}, needEven:{},withZeroJiao:{},exception:", amount, needEven, withZeroJiao, e);
        }
        return null;
    }

    /**
     * 大写金额转小写
     *
     * @param upperCaseAmount 大写金额
     * @return 小写金额
     */
    public static BigDecimal toLower(String upperCaseAmount) {
        logger.info("amount toLower: {}", upperCaseAmount);
        try {
            if (StringUtil.isBlank(upperCaseAmount)) {
                return null;
            }
            upperCaseAmount = upperCaseAmount.replaceAll("\\s+", "").replaceAll("[^\\u4E00-\\u9FFF]", "");
            upperCaseAmount = upperCaseAmount.replaceAll("整", "");
            int index = 0;
            Pair<Integer, BigDecimal> pairMillionUp = handleMillionUp(upperCaseAmount);
            index = pairMillionUp.getLeft() + index;
            BigDecimal result = pairMillionUp.getRight();
            String lastPart = upperCaseAmount.substring(index);
            if (StringUtil.isNotBlank(lastPart)) {
                Pair<Integer, BigDecimal> pair = handlePart(lastPart);
                index = pair.getLeft() + index;
                result = result.add(pair.getRight());
            }
            String pointPart = upperCaseAmount.substring(index);
            if (StringUtil.isNotBlank(pointPart)) {
                result = result.add(handlePointPart(pointPart));
            }
            return new BigDecimal(formatDecimal(result));
        } catch (Exception e) {
            logger.error("amount toLower:{} exception:", upperCaseAmount, e);
            return null;
        }
    }

    /**
     * 格式化金额，每三位用逗号分隔,默认保留两位（直接舍弃多余位数）
     * 例如：
     * 1234.56 -> 1,234.56
     * 1234567.89 -> 1,234,567.89
     *
     * @param amount 待格式化的金额
     * @return 格式化后的金额字符串，如果金额为null则返回null
     */
    public static String format(BigDecimal amount) {
        return format(amount, DEFAULT_DECIMAL_PLACES);
    }

    /**
     * 格式化金额，每三位用逗号分隔，可指定小数位数（直接舍弃多余位数）
     * 例如：
     * format(1234.56, 2) -> 1,234.56
     * format(1234.567, 2) -> 1,234.56
     * format(1234567.89, 1) -> 1,234,567.8
     *
     * @param amount        待格式化的金额
     * @param decimalNumber 要保留的小数位数
     * @return 格式化后的金额字符串，如果金额为null则返回null
     */
    public static String format(BigDecimal amount, Integer decimalNumber) {
        if (amount == null) {
            return null;
        }
        try {
            amount = amount.setScale(decimalNumber, RoundingMode.DOWN);
            DecimalFormat decimalFormat = DECIMAL_FORMAT.get();
            decimalFormat.setMinimumFractionDigits(decimalNumber);
            decimalFormat.setMaximumFractionDigits(decimalNumber);
            return decimalFormat.format(amount);
        } catch (Exception e) {
            logger.error("format amount with comma error: {}", amount, e);
            return null;
        }
    }

    /**
     * 处理万及以上单位的金额部分
     *
     * @param part 待处理的金额字符串
     * @return Pair对象，left为处理后的索引位置，right为处理后的金额值
     */
    private static Pair<Integer, BigDecimal> handleMillionUp(String part) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal temp;
        Pattern pattern = Pattern.compile("(万万亿|亿亿|万亿|亿|万)");
        Matcher matcher = pattern.matcher(part);
        int index = 0;
        while (matcher.find()) {
            String digitString = part.substring(index, matcher.start());
            Pair<Integer, BigDecimal> pair = handlePart(digitString);
            temp = pair.getRight();
            String group = matcher.group();
            if (group.equals("亿亿") || group.equals("万万亿")) {
                result = result.add(temp.multiply(new BigDecimal("10000000000000000")));
            } else if (group.equals("万亿")) {
                result = result.add(temp.multiply(new BigDecimal("1000000000000")));
            } else if (group.equals(AMOUNT_UNITS[8])) {
                result = result.add(temp.multiply(new BigDecimal("100000000")));
            } else if (group.equals(AMOUNT_UNITS[4])) {
                result = result.add(temp.multiply(new BigDecimal("10000")));
            }
            index = matcher.end();
        }
        return Pair.of(index, result);
    }

    /**
     * 处理万以下单位的金额部分
     *
     * @param part 待处理的金额字符串
     * @return Pair对象，left为处理后的索引位置，right为处理后的金额值
     */
    private static Pair<Integer, BigDecimal> handlePart(String part) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal temp;
        Pattern pattern = Pattern.compile("[拾佰仟元]");
        Matcher matcher = pattern.matcher(part);
        int index = 0;
        while (matcher.find()) {
            String digitString = part.substring(index, matcher.start());
            temp = new BigDecimal(String.valueOf(handleDigit(digitString)));
            if (matcher.group().equals(AMOUNT_UNITS[1])) {
                result = result.add(temp.multiply(new BigDecimal("10")));
            } else if (matcher.group().equals(AMOUNT_UNITS[2])) {
                result = result.add(temp.multiply(new BigDecimal("100")));
            } else if (matcher.group().equals(AMOUNT_UNITS[3])) {
                result = result.add(temp.multiply(new BigDecimal("1000")));
            } else if (matcher.group().equals(AMOUNT_UNITS[0])) {
                result = result.add(temp);
            }

            index = matcher.end();
        }
        return Pair.of(index, result);
    }

    private static BigDecimal handlePointPart(String part) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal temp;
        Pattern patternPoint = Pattern.compile("[角|分]");
        Matcher matcherPoint = patternPoint.matcher(part);
        int indexPoint = 0;
        while (matcherPoint.find()) {
            String digitString = part.substring(indexPoint, matcherPoint.start());
            temp = new BigDecimal(String.valueOf(handleDigit(digitString)));
            if (matcherPoint.group().equals(DECIMAL_UNITS[0])) {

                result = result.add(temp.multiply(new BigDecimal("0.1")));
            } else if (matcherPoint.group().equals(DECIMAL_UNITS[1])) {
                result = result.add(temp.multiply(new BigDecimal("0.01")));
            }
            indexPoint = matcherPoint.end();
        }
        return result;
    }

    /**
     * 处理数字字符串转换
     *
     * @param digitString 待处理的数字字符串
     * @return 转换后的整数值
     */
    private static int handleDigit(String digitString) {
        int result = 0;
        if (digitString.length() == 1) {
            result = digitMap.get(digitString.charAt(0));
        } else if (digitString.length() == 2) {
            result = digitMap.get(digitString.charAt(0)) * 10 + digitMap.get(digitString.charAt(1));
        }

        return result;
    }

    /**
     * 格式化 BigDecimal，去除多余的小数零
     *
     * @param bd 待格式化的 BigDecimal 值
     * @return 格式化后的字符串
     */
    private static String formatDecimal(BigDecimal bd) {
        // 去除末尾所有零
        bd = bd.stripTrailingZeros();
        // 判断是否是整数（小数位数为0或缩放后小数位数为0）
        if (bd.scale() <= 0) {
            return bd.toBigInteger().toString(); // 返回整数形式
        } else {
            return bd.toPlainString(); // 返回原始数值的非科学计数法字符串
        }
    }
}
