package com.suxiaoshuai.util.string;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * XSS 防护工具类
 * 提供对用户输入内容的 XSS 防护功能，支持多种转义模式和富文本白名单
 *
 * @author sxs
 */
public class XssUtil {
    /** 富文本内容白名单配置 */
    public static final Safelist RICH_TEXT_WHITELIST = Safelist.relaxed()
            // 结构标签
            .addTags("div", "p", "br", "hr", "pre", "blockquote", "h1", "h2", "h3", "h4", "h5", "h6", "ul", "ol", "li",
                    "dl", "dt", "dd")
            // 文本格式
            .addTags("span", "b", "strong", "i", "em", "u", "s", "strike", "sub", "sup", "code", "kbd", "samp", "var",
                    "mark",
                    "small", "abbr", "cite", "q", "time")
            // 表格支持
            .addTags("table", "thead", "tbody", "tfoot", "tr", "td", "th", "caption")
            // 媒体元素
            .addTags("img", "figure", "figcaption")
            // 语义化标签
            .addTags("header", "footer", "section", "article", "aside", "main", "nav")
            // 全局属性（所有标签）
            .addAttributes(":all", "style", "class", "id", "title", "lang", "dir", "data-*", "aria-*")// 允许自定义数据属性和ARIA属性
            // 链接增强
            .addAttributes("a", "href", "target", "rel", "type", "download", "hreflang")
            // 图片属性
            .addAttributes("img", "src", "alt", "width", "height", "loading", "srcset", "sizes")
            // 表格属性
            .addAttributes("table", "border", "cellpadding", "cellspacing", "summary")
            .addAttributes("td", "th", "colspan", "rowspan", "scope", "headers", "align", "valign")
            // 协议控制
            .addProtocols("a", "href", "http", "https", "mailto", "tel", "ftp", "news")
            .addProtocols("img", "src", "http", "https", "data") // 允许Base64图片
            .addProtocols("blockquote", "cite", "http", "https")
            // 内容过滤规则
            .preserveRelativeLinks(true) // 保留相对链接
            .addEnforcedAttribute("a", "rel", "nofollow noopener") // 自动添加安全属性
            .addEnforcedAttribute("img", "loading", "lazy") // 默认启用懒加载
            ;

    /**
     * 对输入字符串进行转义，防止 XSS 攻击
     *
     * @param input 需要转义的字符串
     * @return 转义后的安全字符串，如果输入为空则返回空字符串
     */
    public static String escape(String input) {
        if (StringUtil.isBlank(input)) {
            return "";
        }
        return process(input, EscapeModeEnum.DEFAULT);
    }

    // 基础处理逻辑（同前）
    /**
     * 根据指定的转义模式处理输入字符串
     *
     * @param input 需要处理的字符串
     * @param mode  转义模式
     * @return 处理后的安全字符串，如果输入为空则返回空字符串
     */
    public static String process(String input, EscapeModeEnum mode) {
        // 实现同之前的process方法
        return process(input, mode, null);
    }

    /**
     * 根据指定的转义模式和白名单处理输入字符串
     *
     * @param input    需要处理的字符串
     * @param mode     转义模式
     * @param safelist 自定义的安全标签白名单，如果为null则使用默认白名单
     * @return 处理后的安全字符串，根据不同模式进行相应的转义处理
     */
    public static String process(String input, EscapeModeEnum mode, Safelist safelist) {
        safelist = safelist == null ? RICH_TEXT_WHITELIST : safelist;
        // 实现同之前的process方法
        return switch (mode) {
            case HTML -> StringEscapeUtils.escapeHtml4(input);
            case RICH_TEXT -> Jsoup.clean(input, safelist);
            case JS -> StringEscapeUtils.escapeEcmaScript(input);
            case XML -> StringEscapeUtils.escapeXml11(input);
            case JSON -> StringEscapeUtils.escapeJson(input);
            default -> stripXSS(input);
        };
    }

    /**
     * 去除字符串中的尖括号，用于简单的 XSS 防护
     *
     * @param value 需要处理的字符串
     * @return 处理后的字符串，如果输入为空则返回原值
     */
    private static String stripXSS(String value) {
        if (StringUtil.isBlank(value)) {
            return value;
        }
        return value.replaceAll("<", "").replaceAll(">", "");
    }
}
