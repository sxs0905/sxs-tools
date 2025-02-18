package com.suxiaoshuai.util.date;


import com.suxiaoshuai.constants.DateFormConstant;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author sxs
 * @date 2017/9/8
 */
public class DateUtil {
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


    public static String formatDate(Date date, String format) {
        return ThreadSafeDateUtil.format(date, format);
    }

    public static Date parseDate(String date, String format) throws ParseException {
        return ThreadSafeDateUtil.parse(date, format);
    }

    public static Date format(Date date, String format) throws ParseException {
        String formatDateStr = formatDate(date, format);
        return parseDate(formatDateStr, "yyyy-MM-dd");
    }


    //获得本月第一天0点时间
    public static Date getCurrentMouthOfFirstDayBegin() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    //获得本月最后一天24点时间
    public static Date getCurrentMouthOfLastDayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 59, 59);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        return cal.getTime();
    }

    /**
     * 获取指定日期及间隔时间的一天开始时间
     *
     * @param date 指定日期
     * @param days 间隔天数,指定日期之前使用负数,之后的使用正数
     * @return
     */
    public static Date getAppointDayBeginTime(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        c.set(Calendar.HOUR_OF_DAY, 00);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定日期及间隔时间的一天结束时间
     *
     * @param date 指定日期
     * @param days 间隔天数,指定日期之前使用负数,之后的使用正数
     * @return
     */
    public static Date getAppointDayOfEndTime(Date date, int days) {
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
     *
     * @param date
     * @return
     */
    public static boolean isWeekend(Date date) {
        boolean flag = false;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int result = c.get(Calendar.DAY_OF_WEEK);
        // 1：周日，7：周六
        if (result == 1 || result == 7) {
            flag = true;
        }
        return flag;
    }

    /**
     * 计算两个时间之间相隔的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static Integer daysBetween(Date before, Date after) throws ParseException {
        Integer day = 0;
        Date date1 = format(before, DateFormConstant.YYYY_MM_DD);
        Date date2 = format(after, DateFormConstant.YYYY_MM_DD);

        Long a = date1.getTime();
        Long b = date2.getTime();
        if (a - b <= 0) {
            day = (int) ((b - a) / (SECOND_OF_MILLISECOND * DAY_OF_SECONDS));
        } else {
            day = (int) ((a - b) / (SECOND_OF_MILLISECOND * DAY_OF_SECONDS));

        }
        return day;
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(getCurrentMouthOfLastDayEnd());
        Date before = new Date();
        Date after = new Date(before.getTime() - DAY_OF_SECONDS * SECOND_OF_MILLISECOND * 5);
        System.out.println(daysBetween(after, after));
    }

    /**
     * Gets time stamp.
     *
     * @return the time stamp
     */
    public static String getTimeStamp() {
        try {
            return ThreadSafeDateUtil.format(new Date(), DateFormConstant.YYYYMMDDHHMMSS);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 返回年份
     *
     * @param date 日期
     * @return 返回年份
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 返回月份
     *
     * @param date 日期
     * @return 返回月份
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 返回日份
     *
     * @param date 日期
     * @return 返回日份
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回小时
     *
     * @param date 日期
     * @return 返回小时
     */
    public static int getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回分钟
     *
     * @param date 日期
     * @return 返回分钟
     */
    public static int getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 返回秒钟
     *
     * @param date 日期
     * @return 返回秒钟
     */
    public static int getSecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.SECOND);
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
}
