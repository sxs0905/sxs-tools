package com.suxiaoshuai.util.string;

/**
 * 字符串转义模式枚举
 * 定义了不同场景下的字符串转义模式，包括：
 * HTML、富文本、JavaScript、URL、XML、JSON等
 * @author suxiaoshuai
 * @since 1.0.0
 */
public enum EscapeModeEnum {
    /**
     * HTML 转义模式
     */
    HTML,
    /**
     * 富文本转义模式
     */
    RICH_TEXT,
    /**
     * JavaScript 转义模式
     */
    JS,
    /**
     * URL 转义模式
     */
    URL,
    /**
     * XML 转义模式
     */
    XML,
    /**
     * JSON 转义模式
     */
    JSON,
    /**
     * 默认转义模式
     */
    DEFAULT
}
