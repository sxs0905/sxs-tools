package com.suxiaoshuai.util.security;

import com.suxiaoshuai.util.string.StringUtil;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

public class Base64Util {

    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * BASE64字符串解码为二进制数据
     */
    public static byte[] decode(String base64) throws Exception {
        return new Base64().decode(base64.getBytes());
    }

    public static byte[] encode(String base64) {
        return new Base64().encode(base64.getBytes());
    }

    public static String encode(byte[] data, String encode) throws EncoderException, UnsupportedEncodingException {
        if (StringUtil.isBlank(encode)) {
            encode = DEFAULT_ENCODING;
        }
        return new String(new Base64().encode(data), encode);
    }
}
