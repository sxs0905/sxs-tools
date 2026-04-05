package cqpay.demo.util;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

public final class Base64Util {
    private Base64Util() {
    }
    public static byte[] encode(byte[] binaryData) {
        return Base64.encodeBase64(binaryData);
    }

    public static byte[] encode(byte[] binaryData, boolean isChunked) {
        return Base64.encodeBase64(binaryData, isChunked);
    }
    public static byte[] decode(String base64Data) {
        return Base64.decodeBase64(base64Data);
    }
    public static byte[] decode(byte[] base64Data) {
        return Base64.decodeBase64(base64Data);
    }

    public static boolean isArrayByteBase64(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException();
        } else {
            return Base64.isBase64(data);
        }
    }

    public static boolean isStringBase64(String base64String) {
        if (base64String == null) {
            throw new IllegalArgumentException();
        } else {
            return Base64.isBase64(base64String.getBytes());
        }
    }

    public static String encodeToStr(String str) {
        try {
            return encodeToStr(str.getBytes(StandardCharsets.UTF_8));
        } catch (Exception var2) {
            return "";
        }
    }

    /**
     * 将 s 进行 BASE64 编码
     *
     * @param s
     * @return
     */
    public static String encodeToStr(byte[] s) {
        if (s == null)
            return null;
        return Base64.encodeBase64String(s);
    }

    public static String decodeToStr(String str) {
        try {

            return new String(Base64.decodeBase64(str), StandardCharsets.UTF_8);
        } catch (Exception var2) {
            return "";
        }
    }
}
