package com.suxiaoshuai.util.date;


import com.suxiaoshuai.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import java.util.Map;

/**
 * 线程安全的日期工具类
 * 使用 ThreadLocal 实现线程安全的日期格式化和解析功能。
 * 通过 Map 缓存不同格式的 SimpleDateFormat 实例，确保每个线程使用独立的格式化器，
 * 避免多线程并发访问时的安全问题。
 */
public class ThreadSafeDateUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadSafeDateUtil.class);

    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static final Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new ConcurrentHashMap<>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern 日期格式模式
     * @return 返回一个线程安全的SimpleDateFormat实例
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        return sdfMap.computeIfAbsent(pattern, key -> ThreadLocal.withInitial(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat(key);
            // 自动解析场景下使用严格模式，避免例如 2024-02-31 被纠正为 3 月份日期。
            sdf.setLenient(false);
            return sdf;
        })).get();
    }

    /**
     * 使用ThreadLocal格式化日期，确保线程安全
     *
     * @param date    待格式化的日期
     * @param pattern 日期格式模式
     * @return 格式化后的日期字符串，如果date或pattern为空则返回空字符串
     */
    public static String format(Date date, String pattern) {

        if (date == null || StringUtil.isBlank(pattern)) {
            return null;
        }

        return getSdf(pattern).format(date);
    }

    /**
     * 解析时间
     *
     * @param dateStr 字符化时间
     * @param pattern 解析格式
     * @return 解析结果
     */
    public static Date parse(String dateStr, String pattern) {
        try {
            if (StringUtil.isBlank(dateStr) || StringUtil.isBlank(pattern)) {
                return null;
            }
            SimpleDateFormat sdf = getSdf(pattern);
            ParsePosition position = new ParsePosition(0);
            Date parsed = sdf.parse(dateStr, position);
            if (parsed == null || position.getIndex() != dateStr.length()) {
                return null;
            }
            return parsed;
        } catch (Exception e) {
            logger.info("date :{},pattern:{} parse failed", dateStr, pattern);
            return null;
        }
    }
}