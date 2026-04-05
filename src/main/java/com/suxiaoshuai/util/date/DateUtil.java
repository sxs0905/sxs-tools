package com.suxiaoshuai.util.date;


import com.suxiaoshuai.constants.DateFormatConstants;
import com.suxiaoshuai.exception.SxsToolsException;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 日期工具类
 * 提供日期时间相关的常用操作，包括：
 * <ul>
 *     <li>日期格式化和解析</li>
 *     <li>日期时间计算</li>
 *     <li>日期属性获取</li>
 *     <li>时间段判断</li>
 * </ul>
 *
 * @author suxiaoshuai
 * @since 1.0.0
 */
public class DateUtil {
    /**
     * 1秒 = 1000毫秒
     */
    public static final int SECOND_OF_MILLISECOND = 1000;
    /**
     * 1分钟 = 60秒
     */
    public static final int MINUTE_OF_SECONDS = 60;

    /**
     * 1小时 = 60分钟 = 3600秒
     */
    public static final int HOUR_OF_SECONDS = MINUTE_OF_SECONDS * 60;
    /**
     * 1天 = 24小时 = 86400秒
     */
    public static final int DAY_OF_SECONDS = HOUR_OF_SECONDS * 24;


    /**
     * 默认日期时间格式，格式为：yyyy-MM-dd HH:mm:ss
     */
    public static final String DEFAULT_FORMAT = DateFormatConstants.YYYY_MM_DD_HH_MM_SS;

    /**
     * 默认日期格式，格式为：yyyy-MM-dd
     */
    public static final String DEFAULT_YMD = DateFormatConstants.YYYY_MM_DD;

    private static final int FLAG_CHINESE = 1;
    private static final int FLAG_SLASH = 1 << 1;
    private static final int FLAG_DASH = 1 << 2;
    private static final int FLAG_DOT = 1 << 3;
    private static final int FLAG_T = 1 << 4;
    private static final int FLAG_COMMA = 1 << 5;
    private static final int FLAG_AMPM = 1 << 6;

    /**
     * 默认日期格式集合（不可变）
     */
    private static final List<String> DEFAULT_DATE_PATTERNS =
            Collections.unmodifiableList(new ArrayList<>(DateFormatConstants.getAllDateFormats()));

    /**
     * 预编译后的格式特征列表
     */
    private static final List<PatternProfile> DEFAULT_PATTERN_PROFILES = buildPatternProfiles(DEFAULT_DATE_PATTERNS);

    /**
     * 输入签名缓存，加速重复形态的字符串解析
     */
    private static final Map<Integer, List<String>> SIGNATURE_PATTERN_CACHE = new ConcurrentHashMap<>();

    /**
     * 缓存key队列，用于增量淘汰，避免全量clear导致抖动
     */
    private static final Queue<Integer> SIGNATURE_CACHE_KEYS = new ConcurrentLinkedQueue<>();

    private static final int SIGNATURE_CACHE_LIMIT = 512;

    /**
     * 按照指定格式格式化日期
     *
     * @param date   日期
     * @param format 格式
     * @return 格式化之后时间串
     */
    public static String formatDate(Date date, String format) {
        return ThreadSafeDateUtil.format(date, format);
    }


    /**
     * 自动解析日期字符串，尝试多种格式
     * 
     * @param date 字符串格式日期
     * @return 解析后的日期
     */
    public static Date parse(String date) {
        return parse(date, Collections.emptyList());
    }

    /**
     * 按照指定格式解析时间
     *
     * @param date   字符串格式日期
     * @param format 格式
     * @return 解析后的日期
     */
    public static Date parse(String date, String format) {
        return ThreadSafeDateUtil.parse(date, format);
    }

    /**
     * 使用指定的格式列表解析日期字符串
     * 
     * @param date 字符串格式日期
     * @param datePartnerList 自定义日期格式列表，将与默认格式列表合并
     * @return 解析后的日期
     * @throws SxsToolsException 如果无法用任何格式解析日期
     */
    public static Date parse(String date, List<String> datePartnerList) {
        if (date == null) {
            return null;
        }
        String input = date.trim();
        if (input.isEmpty()) {
            return null;
        }

        List<String> candidates = collectCandidatePatterns(input);
        if (CollectionUtils.isNotEmpty(datePartnerList)) {
            LinkedHashSet<String> mergedCandidates = new LinkedHashSet<>(candidates);
            mergedCandidates.addAll(datePartnerList);
            for (String format : mergedCandidates) {
                Date parsed = ThreadSafeDateUtil.parse(input, format);
                if (parsed != null) {
                    return parsed;
                }
            }
        } else {
            for (String format : candidates) {
                Date parsed = ThreadSafeDateUtil.parse(input, format);
                if (parsed != null) {
                    return parsed;
                }
            }
        }
        throw new SxsToolsException("【" + input + "】 date format not support");
    }

    private static List<String> collectCandidatePatterns(String input) {
        int inputFlags = buildInputFlags(input);
        int signature = buildSignature(input.length(), inputFlags);

        List<String> cachedPatterns = SIGNATURE_PATTERN_CACHE.get(signature);
        if (CollectionUtils.isNotEmpty(cachedPatterns)) {
            return cachedPatterns;
        }

        LinkedHashSet<String> exactMatched = new LinkedHashSet<>();
        LinkedHashSet<String> lengthMatched = new LinkedHashSet<>();
        for (PatternProfile profile : DEFAULT_PATTERN_PROFILES) {
            if (profile.length >= 0 && profile.length != input.length()) {
                continue;
            }
            lengthMatched.add(profile.pattern);
            if (profile.flags == inputFlags) {
                exactMatched.add(profile.pattern);
            }
        }

        LinkedHashSet<String> result = exactMatched.isEmpty() ? lengthMatched : exactMatched;
        if (result.isEmpty()) {
            result.addAll(DEFAULT_DATE_PATTERNS);
        }

        List<String> resolved = Collections.unmodifiableList(new ArrayList<>(result));
        cacheSignaturePatterns(signature, resolved);
        return resolved;
    }

    private static void cacheSignaturePatterns(int signature, List<String> patterns) {
        List<String> existing = SIGNATURE_PATTERN_CACHE.putIfAbsent(signature, patterns);
        if (existing != null) {
            return;
        }
        SIGNATURE_CACHE_KEYS.offer(signature);
        while (SIGNATURE_PATTERN_CACHE.size() > SIGNATURE_CACHE_LIMIT) {
            Integer oldKey = SIGNATURE_CACHE_KEYS.poll();
            if (oldKey == null) {
                break;
            }
            SIGNATURE_PATTERN_CACHE.remove(oldKey);
        }
    }

    private static int buildInputFlags(String input) {
        int flags = 0;
        if (containsAny(input, '年', '月', '日', '时', '分', '秒')) {
            flags |= FLAG_CHINESE;
        }
        if (input.indexOf('/') >= 0) {
            flags |= FLAG_SLASH;
        }
        if (input.indexOf('-') >= 0) {
            flags |= FLAG_DASH;
        }
        if (input.indexOf('.') >= 0) {
            flags |= FLAG_DOT;
        }
        if (input.indexOf('T') >= 0) {
            flags |= FLAG_T;
        }
        if (input.indexOf(',') >= 0) {
            flags |= FLAG_COMMA;
        }
        String lower = input.toLowerCase(Locale.ROOT);
        if (lower.contains(" am") || lower.contains(" pm") || lower.endsWith("am") || lower.endsWith("pm")
                || input.contains("上午") || input.contains("下午")) {
            flags |= FLAG_AMPM;
        }
        return flags;
    }

    private static int buildPatternFlags(String pattern) {
        int flags = 0;
        if (containsAny(pattern, '年', '月', '日', '时', '分', '秒')) {
            flags |= FLAG_CHINESE;
        }
        if (pattern.indexOf('/') >= 0) {
            flags |= FLAG_SLASH;
        }
        if (pattern.indexOf('-') >= 0) {
            flags |= FLAG_DASH;
        }
        if (pattern.indexOf('.') >= 0) {
            flags |= FLAG_DOT;
        }
        if (pattern.indexOf('T') >= 0) {
            flags |= FLAG_T;
        }
        if (pattern.indexOf(',') >= 0) {
            flags |= FLAG_COMMA;
        }
        if (pattern.indexOf('a') >= 0) {
            flags |= FLAG_AMPM;
        }
        return flags;
    }

    private static int buildSignature(int length, int flags) {
        return (length << 8) | flags;
    }

    private static boolean containsAny(String source, char... chars) {
        for (char c : chars) {
            if (source.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }

    private static List<PatternProfile> buildPatternProfiles(List<String> patterns) {
        List<PatternProfile> profiles = new ArrayList<>(patterns.size());
        for (String pattern : patterns) {
            int length = estimatePatternLength(pattern);
            profiles.add(new PatternProfile(pattern, buildPatternFlags(pattern), length));
        }
        return profiles;
    }

    private static int estimatePatternLength(String pattern) {
        if (isVariableLengthPattern(pattern)) {
            return -1;
        }
        int length = 0;
        boolean inQuote = false;
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (ch == '\'') {
                inQuote = !inQuote;
                continue;
            }
            if (inQuote) {
                length++;
                continue;
            }
            if (Character.isLetter(ch)) {
                if (ch == 'a') {
                    length += 2;
                } else if (ch == 'X') {
                    int run = 1;
                    while (i + 1 < pattern.length() && pattern.charAt(i + 1) == 'X') {
                        run++;
                        i++;
                    }
                    length += run == 3 ? 6 : run;
                } else if (ch == 'Z') {
                    length += 1;
                } else {
                    length++;
                    while (i + 1 < pattern.length() && pattern.charAt(i + 1) == ch) {
                        i++;
                    }
                }
            } else {
                length++;
            }
        }
        return length;
    }

    private static boolean isVariableLengthPattern(String pattern) {
        return pattern.contains("MMM") || pattern.contains("EEEE") || pattern.contains("EEE")
                || pattern.contains("MMMM") || pattern.contains(" z") || pattern.endsWith("z");
    }

    private static final class PatternProfile {
        private final String pattern;
        private final int flags;
        private final int length;

        private PatternProfile(String pattern, int flags, int length) {
            this.pattern = pattern;
            this.flags = flags;
            this.length = length;
        }
    }

    /**
     * 获得本月第一天0点时间
     *
     * @return 返回时间
     */
    public static Date getCurrentMouthOfFirstDayBegin() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获得本月最后一天24点时间
     *
     * @return 返回具体时间
     */
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
     * @return 返回具体时间
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
     * @return 返回具体时间
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
     *
     * @param date 日期
     * @return 返回结果
     */
    public static boolean isWeekend(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int result = c.get(Calendar.DAY_OF_WEEK);
        // 1：周日，7：周六
        return result == 1 || result == 7;
    }

    /**
     * 将java.util.Date转换为java.time.LocalDateTime
     * 
     * @param date 日期对象
     * @return 转换后的LocalDateTime对象，如果date为null则返回null
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将java.time.LocalDateTime转换为java.util.Date
     * 
     * @param date LocalDateTime对象
     * @return 转换后的Date对象，如果date为null则返回null
     */
    public static Date toDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 计算两个时间之间相隔的天数
     *
     * @param before 第一个日期
     * @param after  第二个日期
     * @return 返回结果
     */
    public static Long betweenDays(Date before, Date after) {
        if (before == null || after == null) {
            return 0L;
        }
        long l = Math.abs(before.getTime() - after.getTime());
        return l / (1000 * 60 * 60 * 24);
    }

    /**
     * 获取时间戳
     *
     * @return 返回时间戳字符串
     */
    public static String getTimeStamp() {
        return ThreadSafeDateUtil.format(new Date(), DateFormatConstants.YYYYMMDDHHMMSS);
    }

    /**
     * 当日 yyyy-MM-dd 格式时间串
     *
     * @return 返回结果
     */
    public static String today() {
        return formatDate(new Date(), DEFAULT_YMD);
    }

    /**
     * 判断日期是否在一个时间段里
     *
     * @param date  待判断时间
     * @param start 开始时间（包含）
     * @param end   结束时间（包含）
     * @return 返回结果
     */
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

    /**
     * 获取日历字段值
     *
     * @param date 日期
     * @param unit 日历字段单位 {@link Calendar}
     * @return 返回对应字段的值，如果日期为空则返回 null
     */
    private static Integer calendarGet(Date date, int unit) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(unit);
    }
}
