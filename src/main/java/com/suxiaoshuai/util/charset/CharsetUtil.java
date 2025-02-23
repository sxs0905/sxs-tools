package com.suxiaoshuai.util.charset;


import com.suxiaoshuai.util.string.StringUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author sxs
 */
public class CharsetUtil {
    /**
     * uft-8 编码集
     */
    public static final String UTF_8 = StandardCharsets.UTF_8.name();
    /**
     * GBK
     */
    public static final String GBK = "GBK";
    /**
     * ISO-8859-1
     */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /**
     * 默认编码集为UTF-8
     */
    public static final String DEFAULT_CHARSET = UTF_8;

    /**
     * 将数据由一种编码转换为目标编码
     * 原编码为空时默认为ISO-8859-1
     * 目标编码为空时默认为UTF-8
     *
     * @param source        待转换的数据
     * @param originCharset 原始编码
     * @param targetCharset 目标编码
     */
    public static String dataCharsetConvert(String source, String originCharset, String targetCharset) {
        try {
            if (StringUtil.isBlank(originCharset)) {
                originCharset = ISO_8859_1;
            }
            if (StringUtil.isBlank(targetCharset)) {
                targetCharset = DEFAULT_CHARSET;
            }
            if (StringUtil.isBlank(source) || originCharset.equals(targetCharset)) {
                return source;
            }
            return new String(source.getBytes(originCharset), targetCharset);
        } catch (Exception e) {
            return null;
        }
    }
}
