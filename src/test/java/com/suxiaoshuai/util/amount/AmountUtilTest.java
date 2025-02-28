package com.suxiaoshuai.util.amount;

import com.suxiaoshuai.exception.SxsToolsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 金额转换工具类
 * 转大写基本规则
 *  汉字使用
 *      必须使用正楷或行书体，不可用简化字、谐音字替代（如 “零” 不可写作 “〇” 或 “另”）。
 *  标准大写数字：
 *      零、壹、贰、叁、肆、伍、陆、柒、捌、玖、拾、佰、仟、万、亿、元、角、分、整。
 *  金额单位
 *      主单位：元、角、分，不可省略（如 “¥100.00” 写作 “人民币壹佰元整”，不可写作 “壹佰整”）。
 *  整数结尾：元后无角、分时需加 “整” 或 “正” 字（如 “¥500.00” 写作 “人民币伍佰元整”）。
 *  “零” 的使用 连续多个零时：只写一个 “零”（如 “¥10005.00” 写作 “人民币壹万零伍元整”）。
 *  角分位为零：无需重复写 “零”（如 “¥123.00” 写作 “人民币壹佰贰拾叁元整”）。
 */
class AmountUtilTest {

    @Nested
    @DisplayName("金额大写转换测试")
    class ToUpperTest {
        @Test
        @DisplayName("测试空输入")
        void testNullInput() {
            assertNull(AmountUtil.toUpper(null, true, false));
        }

        @Test
        @DisplayName("测试超大金额")
        void testTooLargeAmount() {
            BigDecimal amount = new BigDecimal("100000000000000000");
            assertThrows(SxsToolsException.class, () -> AmountUtil.toUpper(amount, true, false));
        }

        @ParameterizedTest(name = "金额：{0}，期望结果：{1}")
        @MethodSource("toUpperTestCases")
        @DisplayName("测试常规金额转换")
        void testNormalAmount(BigDecimal amount, String expected) {
            assertEquals(expected, AmountUtil.toUpper(amount, true, true));
        }

        private static Stream<Arguments> toUpperTestCases() {
            return Stream.of(
                    Arguments.of(new BigDecimal("0"), "零元整"),
                    Arguments.of(new BigDecimal("1"), "壹元整"),
                    Arguments.of(new BigDecimal("10"), "壹拾元整"),
                    Arguments.of(new BigDecimal("100"), "壹佰元整"),
                    Arguments.of(new BigDecimal("1000"), "壹仟元整"),
                    Arguments.of(new BigDecimal("10000"), "壹万元整"),
                    Arguments.of(new BigDecimal("100000000"), "壹亿元整"),
                    Arguments.of(new BigDecimal("1.23"), "壹元贰角叁分"),
                    Arguments.of(new BigDecimal("1.03"), "壹元零叁分"),
                    Arguments.of(new BigDecimal("10005.00"), "壹万零伍元整"),
                    Arguments.of(new BigDecimal("1.30"), "壹元叁角"),
                    Arguments.of(new BigDecimal("1200005000"), "壹拾贰亿零伍仟元整"),
                    Arguments.of(new BigDecimal("10001.23"), "壹万零壹元贰角叁分")
            );
        }
    }

    @Nested
    @DisplayName("金额小写转换测试")
    class ToLowerTest {
        @Test
        @DisplayName("测试空输入")
        void testNullInput() {
            assertNull(AmountUtil.toLower(null));
        }

        @Test
        @DisplayName("测试空字符串")
        void testEmptyString() {
            assertNull(AmountUtil.toLower(""));
        }

        @ParameterizedTest(name = "大写金额：{0}，期望结果：{1}")
        @MethodSource("toLowerTestCases")
        @DisplayName("测试常规金额转换")
        void testNormalAmount(String amount, BigDecimal expected) {
            assertEquals(expected, AmountUtil.toLower(amount));
        }

        private static Stream<Arguments> toLowerTestCases() {
            return Stream.of(
                    Arguments.of("零元整", new BigDecimal("0")),
                    Arguments.of("壹元整", new BigDecimal("1")),
                    Arguments.of("壹拾元整", new BigDecimal("10")),
                    Arguments.of("壹佰元整", new BigDecimal("100")),
                    Arguments.of("壹仟元整", new BigDecimal("1000")),
                    Arguments.of("壹万元整", new BigDecimal("10000")),
                    Arguments.of("壹亿元整", new BigDecimal("100000000")),
                    Arguments.of("壹元贰角叁分", new BigDecimal("1.23")),
                    Arguments.of("壹元零叁分", new BigDecimal("1.03")),
                    Arguments.of("壹元叁角", new BigDecimal("1.3")),
                    Arguments.of("壹万零壹元贰角叁分", new BigDecimal("10001.23"))
            );
        }
    }

    @Nested
    @DisplayName("金额格式化测试")
    class FormatTest {
        @Test
        @DisplayName("测试空输入")
        void testNullInput() {
            assertNull(AmountUtil.format(null));
            assertNull(AmountUtil.format(null, 2));
        }

        @ParameterizedTest(name = "金额：{0}，小数位：{1}，期望结果：{2}")
        @MethodSource("formatTestCases")
        @DisplayName("测试常规金额格式化")
        void testNormalAmount(BigDecimal amount, Integer scale, String expected) {
            assertEquals(expected, AmountUtil.format(amount, scale));
        }

        private static Stream<Arguments> formatTestCases() {
            return Stream.of(
                    Arguments.of(new BigDecimal("1234.56"), 2, "1,234.56"),
                    Arguments.of(new BigDecimal("1234.567"), 2, "1,234.56"),
                    Arguments.of(new BigDecimal("1234567.89"), 1, "1,234,567.8"),
                    Arguments.of(new BigDecimal("1000000.00"), 2, "1,000,000.00"),
                    Arguments.of(new BigDecimal("0.123"), 3, "0.123"),
                    Arguments.of(new BigDecimal("1.23"), 0, "1"),
                    Arguments.of(new BigDecimal("-1234.56"), 2, "-1,234.56")
            );
        }

        @Test
        @DisplayName("测试默认格式化")
        void testDefaultFormat() {
            assertEquals("1,234.56", AmountUtil.format(new BigDecimal("1234.56")));
            assertEquals("1,234.50", AmountUtil.format(new BigDecimal("1234.5")));
            assertEquals("1,234.00", AmountUtil.format(new BigDecimal("1234")));
        }
    }
}