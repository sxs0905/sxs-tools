package com.suxiaoshuai.util.date;


import com.suxiaoshuai.constants.DateFormConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    /**
     * 一分钟毫秒数
     */
    public static final int SECOND_OF_MILLISECOND = 1000;
    /**
     * 每分钟秒数
     */
    public static final int MINUTE_OF_SECONDS = 60;

    /**
     * 一小时秒数
     */
    public static final int HOUR_OF_SECONDS = MINUTE_OF_SECONDS * 60;
    /**
     * 一天总共秒数
     */
    public static final int DAY_OF_SECONDS = HOUR_OF_SECONDS * 24;


    public static final String DEFAULT_FORMAT = DatePattern.NORM_DATETIME_PATTERN;
    public static final String DEFAULT_YMD = DatePattern.NORM_DATE_PATTERN;

    public static String formatDate(Date date, String format) {
        return ThreadSafeDateUtil.format(date, format);
    }

    public static Date parseDate(String date, String format) {
        return ThreadSafeDateUtil.parse(date, format);
    }

    // 获得本月第一天0点时间
    public static Date getCurrentMouthOfFirstDayBegin() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getCurrentMouthOfLastDayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 59, 59);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        return cal.getTime();
    }

    /**
     * 获取指定日期及间隔时间的一天开始时间
     *
     * @param date 指定日期
     * @param days 间隔天数,指定日期之前使用负数,之后的使用正数
     */
    public static Date getDayBeginTime(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定日期及间隔时间的一天结束时间
     *
     * @param date 指定日期
     * @param days 间隔天数,指定日期之前使用负数,之后的使用正数
     */
    public static Date getDayOfEndTime(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * 判断传入的时间是否为周末
     */
    public static boolean isWeekend(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int result = c.get(Calendar.DAY_OF_WEEK);
        // 1：周日，7：周六
        return result == 1 || result == 7;
    }

    /**
     * 计算两个时间之间相隔的天数
     */
    public static Long betweenDays(Date before, Date after) {
        if (before == null || after == null) {
            return 0L;
        }
        long l = Math.abs(before.getTime() - after.getTime());
        return l / (1000 * 60 * 60 * 24);
    }

    /**
     * Gets time stamp.
     *
     * @return the time stamp
     */
    public static String getTimeStamp() {
        return ThreadSafeDateUtil.format(new Date(), DateFormConstant.YYYYMMDDHHMMSS);
    }

    public static String today() {
        return formatDate(new Date(), DEFAULT_YMD);
    }

    public static boolean isIn(Date date, Date start, Date end) {
        if (date == null || start == null || end == null) {
            return false;
        }
        if (start.before(end)) {
            return (date.after(start) || date.equals(start)) && (date.before(end) || date.equals(end));
        }
        return (date.before(start) || date.equals(start)) && (date.after(end) || date.equals(end));
    }

    /**
     * 返回年份
     *
     * @param date 日期
     * @return 返回年份
     */
    public static Integer getYear(Date date) {
        return calendarGet(date, Calendar.YEAR);
    }

    /**
     * 返回月份
     *
     * @param date 日期
     * @return 返回月份
     */
    public static Integer getMonth(Date date) {
        Integer month = calendarGet(date, Calendar.MONTH);
        return month == null ? null : month + 1;
    }

    /**
     * 返回日份
     *
     * @param date 日期
     * @return 返回日份
     */
    public static Integer getDay(Date date) {
        return calendarGet(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回小时
     *
     * @param date 日期
     * @return 返回小时
     */
    public static Integer getHour(Date date) {
        return calendarGet(date, Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回分钟
     *
     * @param date 日期
     * @return 返回分钟
     */
    public static Integer getMinute(Date date) {
        return calendarGet(date, Calendar.MINUTE);
    }

    /**
     * 返回秒钟
     *
     * @param date 日期
     * @return 返回秒钟
     */
    public static Integer getSecond(Date date) {
        return calendarGet(date, Calendar.SECOND);
    }

    /**
     * 返回毫秒
     *
     * @param date 日期
     * @return 返回毫秒
     */
    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    private static Integer calendarGet(Date date, int unit) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(unit);
    }
}
