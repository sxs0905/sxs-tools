/**
 * 日期格式常量类
 * 提供全面的日期时间格式模式，适用于Java日期时间解析和格式化
 * 包含标准格式、常用格式、中文格式等多种格式规范
 * 所有格式已整理到datePartnerList列表中，便于统一获取和使用
 */
package com.suxiaoshuai.constants;

import java.util.Arrays;
import java.util.List;

public class DateFormatConstants {
    /**
     * 标准格式组
     * 包含国际通用的日期时间格式标准
     */
    // ISO 8601标准格式，不含毫秒
    public static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    // ISO 8601标准格式，包含毫秒
    public static final String ISO_8601_WITH_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    // UTC格式，带时区信息
    public static final String UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    
    /**
     * 常用英文格式组
     * 包含使用连字符分隔的标准日期格式
     */
    // 年-月-日格式 (例如：2024-01-15)
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    // 月-日-年格式 (例如：01-15-2024)
    public static final String MM_DD_YYYY = "MM-dd-yyyy";
    // 日-月-年格式 (例如：15-01-2024)
    public static final String DD_MM_YYYY = "dd-MM-yyyy";
    
    /**
     * 带斜杠的日期格式组
     * 使用斜杠作为日期分隔符的格式
     */
    // 年/月/日格式 (例如：2024/01/15)
    public static final String YYYY_SLASH_MM_SLASH_DD = "yyyy/MM/dd";
    // 月/日/年格式 (例如：01/15/2024)
    public static final String MM_SLASH_DD_SLASH_YYYY = "MM/dd/yyyy";
    // 日/月/年格式 (例如：15/01/2024)
    public static final String DD_SLASH_MM_SLASH_YYYY = "dd/MM/yyyy";
    
    /**
     * 日期时间格式组
     * 包含日期和时间信息的完整格式
     */
    // 年-月-日 时:分:秒格式 (例如：2024-01-15 14:30:45)
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    // 年/月/日 时:分:秒格式 (例如：2024/01/15 14:30:45)
    public static final String YYYY_SLASH_MM_SLASH_DD_HH_MM_SS = "yyyy/MM/dd HH:mm:ss";
    // 月/日/年 时:分:秒格式 (例如：01/15/2024 14:30:45)
    public static final String MM_SLASH_DD_SLASH_YYYY_HH_MM_SS = "MM/dd/yyyy HH:mm:ss";
    // 日/月/年 时:分:秒格式 (例如：15/01/2024 14:30:45)
    public static final String DD_SLASH_MM_SLASH_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
    // 月-日-年 时:分:秒格式 (例如：01-15-2024 14:30:45)
    public static final String MM_DD_YYYY_HH_MM_SS = "MM-dd-yyyy HH:mm:ss";
    // 日-月-年 时:分:秒格式 (例如：15-01-2024 14:30:45)
    public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss";
    
    /**
     * 紧凑格式组
     * 不含分隔符的紧凑日期时间格式
     */
    // 紧凑年月日格式 (例如：20240115)
    public static final String YYYYMMDD = "yyyyMMdd";
    // 紧凑年月日时分秒格式 (例如：20240115143045)
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    // 带毫秒的紧凑格式 (例如：20240115143045123)
    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    
    /**
     * 中文格式组
     * 使用中文表示的日期时间格式
     */
    // 中文年月日格式 (例如：2024年01月15日)
    public static final String CHINESE_YYYY_MM_DD = "yyyy年MM月dd日";
    // 中文月日年格式 (例如：01月15日2024年)
    public static final String CHINESE_MM_DD_YYYY = "MM月dd日yyyy年";
    // 中文日月年格式 (例如：15日01月2024年)
    public static final String CHINESE_DD_MM_YYYY = "dd日MM月yyyy年";
    // 中文年月日时分格式 (例如：2024年01月15日 14:30)
    public static final String CHINESE_YYYY_MM_DD_HH_MM = "yyyy年MM月dd日 HH:mm";
    // 中文年月日时分秒格式 (例如：2024年01月15日 14:30:45)
    public static final String CHINESE_YYYY_MM_DD_HH_MM_SS = "yyyy年MM月dd日 HH:mm:ss";
    // 中文年月格式 (例如：2024年01月)
    public static final String CHINESE_YEAR_MONTH = "yyyy年MM月";
    // 中文月日格式 (例如：01月15日)
    public static final String CHINESE_MONTH_DAY = "MM月dd日";
    // 中文完整日期时间格式 (例如：2024年01月15日 14时30分45秒)
    public static final String CHINESE_FULL_DATE_TIME = "yyyy年MM月dd日 HH时mm分ss秒";
    // 带毫秒的中文完整格式 (例如：2024年01月15日 14时30分45秒123毫秒)
    public static final String CHINESE_FULL_DATE_TIME_WITH_MILLIS = "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒";
    
    /**
     * 英语文本格式组
     * 使用英文月份名称的格式
     */
    // 英文月(缩写)日年格式 (例如：Jan 15, 2024)
    public static final String ENGLISH_MMM_DD_YYYY = "MMM dd, yyyy";
    // 英文日月(缩写)年格式 (例如：15 Jan 2024)
    public static final String ENGLISH_DD_MMM_YYYY = "dd MMM yyyy";
    // 英文月(完整)日年格式 (例如：January 15, 2024)
    public static final String ENGLISH_FULL_MONTH_DD_YYYY = "MMMM dd, yyyy";
    // 英文日月(完整)年格式 (例如：15 January 2024)
    public static final String ENGLISH_DD_FULL_MONTH_YYYY = "dd MMMM yyyy";
    
    /**
     * 短格式组
     * 使用两位年份的简短格式
     */
    // 短年-月-日格式 (例如：24-01-15)
    public static final String YY_MM_DD = "yy-MM-dd";
    // 短月-日-年格式 (例如：01-15-24)
    public static final String MM_DD_YY = "MM-dd-yy";
    // 短日-月-年格式 (例如：15-01-24)
    public static final String DD_MM_YY = "dd-MM-yy";
    // 中文短年月日格式 (例如：24年01月15日)
    public static final String CHINESE_YY_MM_DD = "yy年MM月dd日";
    
    /**
     * 时段格式组
     * 区分12小时制和24小时制的格式
     */
    // 24小时制年月日时分秒格式 (例如：2024-01-15 14:30:45)
    public static final String YYYY_MM_DD_HH_MM_SS_24H = "yyyy-MM-dd HH:mm:ss";
    // 12小时制年月日时分秒格式 (例如：2024-01-15 02:30:45 PM)
    public static final String YYYY_MM_DD_HH_MM_SS_12H = "yyyy-MM-dd hh:mm:ss a";
    // 中文12小时制年月日时分格式 (例如：2024年01月15日 02:30 下午)
    public static final String CHINESE_YYYY_MM_DD_HH_MM_12H = "yyyy年MM月dd日 hh:mm a";
    // 中文12小时制年月日时分秒格式 (例如：2024年01月15日 02:30:45 下午)
    public static final String CHINESE_YYYY_MM_DD_HH_MM_SS_12H = "yyyy年MM月dd日 hh:mm:ss a";
    
    /**
     * 时间格式组
     * 仅包含时间部分的格式
     */
    // 24小时制时分秒格式 (例如：14:30:45)
    public static final String HH_MM_SS = "HH:mm:ss";
    // 24小时制时分格式 (例如：14:30)
    public static final String HH_MM = "HH:mm";
    // 12小时制时分秒格式 (例如：02:30:45 PM)
    public static final String HH_MM_SS_12H = "hh:mm:ss a";
    // 12小时制时分格式 (例如：02:30 PM)
    public static final String HH_MM_12H = "hh:mm a";
    // 中文时分秒格式 (例如：14时30分45秒)
    public static final String CHINESE_HH_MM_SS = "HH时mm分ss秒";
    // 中文时分格式 (例如：14时30分)
    public static final String CHINESE_HH_MM = "HH时mm分";
    
    /**
     * 所有日期格式列表
     * 集中管理本类中定义的所有日期格式字符串常量
     * 便于统一访问和遍历所有可用格式
     */
    public static final List<String> datePartnerList = Arrays.asList(
        // 标准格式
        ISO_8601,
        ISO_8601_WITH_MILLIS,
        UTC,
        
        // 常用英文格式
        YYYY_MM_DD,
        MM_DD_YYYY,
        DD_MM_YYYY,
        // 带斜杠的格式
        YYYY_SLASH_MM_SLASH_DD,
        MM_SLASH_DD_SLASH_YYYY,
        DD_SLASH_MM_SLASH_YYYY,
        YYYY_MM_DD_HH_MM_SS,
        // 带斜杠和时间的格式
        YYYY_SLASH_MM_SLASH_DD_HH_MM_SS,
        MM_SLASH_DD_SLASH_YYYY_HH_MM_SS,
        DD_SLASH_MM_SLASH_YYYY_HH_MM_SS,
        MM_DD_YYYY_HH_MM_SS,
        DD_MM_YYYY_HH_MM_SS,
        YYYYMMDD,
        YYYYMMDDHHMMSS,
        YYYYMMDDHHMMSSSSS,
        
        // 中文格式
        CHINESE_YYYY_MM_DD,
        CHINESE_MM_DD_YYYY,
        CHINESE_DD_MM_YYYY,
        CHINESE_YYYY_MM_DD_HH_MM,
        CHINESE_YYYY_MM_DD_HH_MM_SS,
        CHINESE_YEAR_MONTH,
        CHINESE_MONTH_DAY,
        CHINESE_FULL_DATE_TIME,
        CHINESE_FULL_DATE_TIME_WITH_MILLIS,
        
        // 其他语言格式
        ENGLISH_MMM_DD_YYYY,
        ENGLISH_DD_MMM_YYYY,
        ENGLISH_FULL_MONTH_DD_YYYY,
        ENGLISH_DD_FULL_MONTH_YYYY,
        
        // 短格式
        YY_MM_DD,
        MM_DD_YY,
        DD_MM_YY,
        CHINESE_YY_MM_DD,
        
        // 24小时制和12小时制
        YYYY_MM_DD_HH_MM_SS_24H,
        YYYY_MM_DD_HH_MM_SS_12H,
        CHINESE_YYYY_MM_DD_HH_MM_12H,
        CHINESE_YYYY_MM_DD_HH_MM_SS_12H,
        
        // 时间格式
        HH_MM_SS,
        HH_MM,
        HH_MM_SS_12H,
        HH_MM_12H,
        CHINESE_HH_MM_SS,
        CHINESE_HH_MM
    );
    
    /**
     * 获取所有日期格式的列表
     * @return 包含所有已定义日期格式的不可变List集合
     */
    public static List<String> getAllDateFormats() {
        return datePartnerList;
    }
    
    /**
     * 获取日期格式的数量
     * @return 已定义的日期格式总数
     */
    public static int getDateFormatCount() {
        return datePartnerList.size();
    }
}