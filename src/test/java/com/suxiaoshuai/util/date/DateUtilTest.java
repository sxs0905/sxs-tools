package com.suxiaoshuai.util.date;

import com.suxiaoshuai.constants.DatePatternConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("日期工具类测试")
class DateUtilTest {
    
    private Calendar calendar;
    private Date testDate;

    @BeforeEach
    void setUp() {
        calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JANUARY, 15, 10, 30, 45); // 2024-01-15 10:30:45
        testDate = calendar.getTime();
    }

    @Nested
    @DisplayName("日期格式化测试")
    class FormatDateTest {
        @Test
        @DisplayName("测试空日期格式化")
        void testNullDate() {
            assertNull(DateUtil.formatDate(null, DatePatternConstant.NORM_DATETIME_PATTERN));
        }

        @Test
        @DisplayName("测试正常日期格式化")
        void testNormalDate() {
            assertEquals("2024-01-15 10:30:45", 
                DateUtil.formatDate(testDate, DatePatternConstant.NORM_DATETIME_PATTERN));
        }
    }

    @Nested
    @DisplayName("日期解析测试")
    class ParseDateTest {
        @Test
        @DisplayName("测试空字符串解析")
        void testNullString() {
            assertNull(DateUtil.parseDate(null, DatePatternConstant.NORM_DATETIME_PATTERN));
        }

        @Test
        @DisplayName("测试正常日期解析")
        void testNormalString() {
            Date date = DateUtil.parseDate("2024-01-15 10:30:45", DatePatternConstant.NORM_DATETIME_PATTERN);
            assertEquals(testDate.getTime() / 1000, date.getTime() / 1000);
        }
    }

    @Nested
    @DisplayName("获取日期开始时间测试")
    class GetDayBeginTimeTest {
        @Test
        @DisplayName("测试当天开始时间")
        void testCurrentDayBegin() {
            Date beginTime = DateUtil.getDayBeginTime(testDate, 0);
            Calendar expected = Calendar.getInstance();
            expected.setTime(testDate);
            expected.set(Calendar.HOUR_OF_DAY, 0);
            expected.set(Calendar.MINUTE, 0);
            expected.set(Calendar.SECOND, 0);
            assertEquals(expected.getTimeInMillis() / 1000, beginTime.getTime() / 1000);
        }

        @Test
        @DisplayName("测试前一天开始时间")
        void testPreviousDayBegin() {
            Date beginTime = DateUtil.getDayBeginTime(testDate, -1);
            Calendar expected = Calendar.getInstance();
            expected.setTime(testDate);
            expected.add(Calendar.DATE, -1);
            expected.set(Calendar.HOUR_OF_DAY, 0);
            expected.set(Calendar.MINUTE, 0);
            expected.set(Calendar.SECOND, 0);
            assertEquals(expected.getTimeInMillis() / 1000, beginTime.getTime() / 1000);
        }
    }

    @Nested
    @DisplayName("获取日期结束时间测试")
    class GetDayEndTimeTest {
        @Test
        @DisplayName("测试当天结束时间")
        void testCurrentDayEnd() {
            Date endTime = DateUtil.getDayOfEndTime(testDate, 0);
            Calendar expected = Calendar.getInstance();
            expected.setTime(testDate);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            assertEquals(expected.getTimeInMillis() / 1000, endTime.getTime() / 1000);
        }

        @Test
        @DisplayName("测试后一天结束时间")
        void testNextDayEnd() {
            Date endTime = DateUtil.getDayOfEndTime(testDate, 1);
            Calendar expected = Calendar.getInstance();
            expected.setTime(testDate);
            expected.add(Calendar.DATE, 1);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            assertEquals(expected.getTimeInMillis() / 1000, endTime.getTime() / 1000);
        }
    }

    @Nested
    @DisplayName("周末判断测试")
    class IsWeekendTest {
        @Test
        @DisplayName("测试工作日")
        void testWeekday() {
            // 2024-01-15 是星期一
            assertFalse(DateUtil.isWeekend(testDate));
        }

        @Test
        @DisplayName("测试周六")
        void testSaturday() {
            calendar.set(2024, Calendar.JANUARY, 20); // 2024-01-20 周六
            assertTrue(DateUtil.isWeekend(calendar.getTime()));
        }

        @Test
        @DisplayName("测试周日")
        void testSunday() {
            calendar.set(2024, Calendar.JANUARY, 21); // 2024-01-21 周日
            assertTrue(DateUtil.isWeekend(calendar.getTime()));
        }
    }

    @Nested
    @DisplayName("计算天数差测试")
    class BetweenDaysTest {
        @Test
        @DisplayName("测试空日期")
        void testNullDates() {
            assertEquals(0L, DateUtil.betweenDays(null, null));
            assertEquals(0L, DateUtil.betweenDays(testDate, null));
            assertEquals(0L, DateUtil.betweenDays(null, testDate));
        }

        @Test
        @DisplayName("测试同一天")
        void testSameDay() {
            assertEquals(0L, DateUtil.betweenDays(testDate, testDate));
        }

        @Test
        @DisplayName("测试相邻天")
        void testAdjacentDays() {
            Calendar nextDay = Calendar.getInstance();
            nextDay.setTime(testDate);
            nextDay.add(Calendar.DATE, 1);
            assertEquals(1L, DateUtil.betweenDays(testDate, nextDay.getTime()));
        }
    }

    @Nested
    @DisplayName("时间段判断测试")
    class IsInTest {
        @Test
        @DisplayName("测试空值")
        void testNullValues() {
            assertFalse(DateUtil.isIn(null, testDate, testDate));
            assertFalse(DateUtil.isIn(testDate, null, testDate));
            assertFalse(DateUtil.isIn(testDate, testDate, null));
        }

        @Test
        @DisplayName("测试正常时间段")
        void testNormalRange() {
            Calendar start = Calendar.getInstance();
            start.setTime(testDate);
            start.add(Calendar.HOUR, -1);

            Calendar end = Calendar.getInstance();
            end.setTime(testDate);
            end.add(Calendar.HOUR, 1);

            assertTrue(DateUtil.isIn(testDate, start.getTime(), end.getTime()));
            assertFalse(DateUtil.isIn(start.getTime(), testDate, end.getTime()));
        }

        @Test
        @DisplayName("测试边界值")
        void testBoundaryValues() {
            assertTrue(DateUtil.isIn(testDate, testDate, testDate));
        }

        @Test
        @DisplayName("测试反向时间段")
        void testReverseRange() {
            Calendar start = Calendar.getInstance();
            start.setTime(testDate);
            start.add(Calendar.HOUR, 1);

            Calendar end = Calendar.getInstance();
            end.setTime(testDate);
            end.add(Calendar.HOUR, -1);

            assertTrue(DateUtil.isIn(testDate, start.getTime(), end.getTime()));
        }
    }
}