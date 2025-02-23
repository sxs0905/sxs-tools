package com.suxiaoshuai.util.security;

import com.suxiaoshuai.util.charset.CharsetUtil;
import com.suxiaoshuai.util.string.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base64Util {

    private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);

    private static final String DEFAULT_ENCODING = CharsetUtil.UTF_8;


    public static String decodeAsString(String data) {
        return StringUtils.newString(Base64.decodeBase64(data), DEFAULT_ENCODING);
    }

    /**
     * BASE64字符串解码为二进制数据
     */
    public static byte[] decode(String data) {
        return Base64.decodeBase64(data);
    }

    /**
     * BASE64字符串解码为二进制数据
     */
    public static byte[] decode(byte[] data) {
        return Base64.decodeBase64(data);
    }

    public static String encode(String data) {
        return encode(data.getBytes());
    }

    public static String encode(byte[] data) {
        return encode(data, DEFAULT_ENCODING);
    }

    public static String encode(byte[] data, String encode) {
        try {
            if (StringUtil.isBlank(encode)) {
                encode = DEFAULT_ENCODING;
            }
            return StringUtils.newString(Base64.encodeBase64(data), encode);
        } catch (Exception e) {
            logger.error("encode error:", e);
            return null;
        }

    }
}
