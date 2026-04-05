package com.suxiaoshuai.util.date;

import com.suxiaoshuai.constants.DateFormatConstants;
import com.suxiaoshuai.util.string.StringUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalDateUtil {

    public static final String DEFAULT_FORMAT = DateFormatConstants.YYYY_MM_DD_HH_MM_SS;
    public static final String DEFAULT_YMD = DateFormatConstants.YYYY_MM_DD;
    public static final String DEFAULT_YMD_LONG = DateFormatConstants.YYYYMMDD;

    // 阳历天数
    private final static int[] solarMonths = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    // 生肖
    private final static String[] zodiacs = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
    // 天干
    private final static String[] tGan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    // 地支
    private final static String[] dZhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    // 二十四节气
    private final static String[] solarTerms = new String[]{"小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏",
            "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};

    private final static String[] months = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private final static String[] days = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
            "24", "25", "26", "27", "28", "29", "30", "31"};

    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    private static DateTimeFormatter formatterOf(String format) {
        return FORMATTER_CACHE.computeIfAbsent(format, DateTimeFormatter::ofPattern);
    }

    public static String format(LocalDateTime localDateTime, String format) {
        if (localDateTime == null) {
            return null;
        }
        format = StringUtil.isBlank(format) ? DEFAULT_FORMAT : format;
        DateTimeFormatter formatter = formatterOf(format);
        return localDateTime.format(formatter);
    }

    public static String getMonth(Integer month) {
        if (month == null || month < 1 || month > 12) {
            return null;
        }
        return months[Math.abs(month)];
    }

    public static String getDay(Integer day) {
        if (day == null || day < 1 || day > 31) {
            return null;
        }
        return days[day];
    }

    public static Integer formatToInt(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = formatterOf(DEFAULT_YMD_LONG);
        return Integer.parseInt(localDateTime.format(formatter));
    }

    public static Integer getYear(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.getYear();
    }

    public static Integer getMonth(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.getMonthValue();
    }

    public static Integer getDay(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.getDayOfMonth();
    }

//    public static Date parseToDate(String date, String format) {
//        LocalDateTime localDate = parse(date, format);
//        return toDate(localDate);
//    }

    public static LocalDateTime parse(String localDateTime, String format) {
        if (StringUtil.isBlank(localDateTime)) {
            return null;
        }
        format = StringUtil.isBlank(format) ? DEFAULT_FORMAT : format;

        DateTimeFormatter formatter = formatterOf(format);
        try {
            if (localDateTime.indexOf(':') >= 0 || localDateTime.indexOf('T') >= 0 || localDateTime.indexOf('时') >= 0) {
                return LocalDateTime.parse(localDateTime, formatter);
            }
            return LocalDate.parse(localDateTime, formatter).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDate parseDefaultDate(String localDate) {
        return parseDate(localDate, DEFAULT_FORMAT);
    }

    public static LocalDate parseDate(String localDate, String format) {
        if (StringUtil.isBlank(localDate)) {
            return null;
        }
        format = StringUtil.isBlank(format) ? DEFAULT_YMD : format;

        DateTimeFormatter formatter = formatterOf(format);
        try {
            return LocalDate.parse(localDate, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDateTime addDay(LocalDateTime date, Long days) {
        if (date == null) {
            return null;
        }
        return date.plusDays(days);
    }

    public static Long betweenDays(LocalDate before, LocalDate after) {
        if (before == null || after == null) {
            return 0L;
        }

        return Math.abs(ChronoUnit.DAYS.between(before, after));
    }

    public static int weekday(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        return dayOfWeek.getValue();
    }

    public static String getZodiacs(LocalDateTime date) {
        return zodiacs[(getYear(date) - 1900) % 12];
    }
}
