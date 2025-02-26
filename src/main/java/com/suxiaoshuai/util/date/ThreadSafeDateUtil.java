package com.suxiaoshuai.util.date;


import com.suxiaoshuai.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程安全的日期工具类
 * <p>
 * 使用 ThreadLocal 实现线程安全的日期格式化和解析功能。
 * 通过 Map 缓存不同格式的 SimpleDateFormat 实例，确保每个线程使用独立的格式化器，
 * 避免多线程并发访问时的安全问题。
 */
public class ThreadSafeDateUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadSafeDateUtil.class);

    /**
     * 锁对象
     */
    private static final Object lockObj = new Object();

    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static final Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern 日期格式模式
     * @return 返回一个线程安全的SimpleDateFormat实例
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
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
            return getSdf(pattern).parse(dateStr);
        } catch (Exception e) {
            logger.error("date :{},pattern:{} parse error", dateStr, pattern, e);
            return null;
        }
    }
}