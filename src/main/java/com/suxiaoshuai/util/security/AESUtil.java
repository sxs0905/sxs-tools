package com.suxiaoshuai.util.security;

import com.suxiaoshuai.constants.AesKeyLengthEnum;
import com.suxiaoshuai.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class AESUtil {
    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);

    public static final String ALGORITHM = "AES";
    private static final String DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final int DEFAULT_ITERATIONS = 65536;     // PBKDF2 迭代次数
    private static final int DEFAULT_KEY_LENGTH = 256;       // 派生密钥长度（位）

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/GCM/NoPadding"; // 默认使用GCM模式
    private static final int GCM_TAG_LENGTH = 128; // GCM模式校验位长度
    private static final int IV_LENGTH = 12;

    // AES/CBC/NOPaddin
    // AES 默认模式
    // 使用CBC模式, 在初始化Cipher对象时, 需要增加参数, 初始化向量IV : IvParameterSpec iv = new IvParameterSpec(key.getBytes());
    // NOPadding: 使用NOPadding模式时, 原文长度必须是8byte的整数倍
    public static final String CBC = "AES/CBC/PKCS5Padding";
    public static final String ECB = "AES/ECB/PKCS5Padding";

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * 生成随机盐值（Base64编码）
     *
     * @param byteLength 盐值字节长度（推荐>=16）
     * @return salt
     */
    public static String generateSalt(int byteLength) {
        byte[] salt = new byte[byteLength];
        secureRandom.nextBytes(salt);
        return Base64Util.encode(salt);
    }

    /**
     * @return 返回128位密钥
     */
    public static String key128() {
        return generator(128);
    }

    /**
     * @return 返回192位密钥
     */
    public static String key192() {
        return generator(192);
    }

    /**
     * @return 返回256位密钥
     */
    public static String key256() {
        return generator(256);
    }

    /**
     * 生成AES密钥（Base64编码格式）
     *
     * @param keySize 密钥长度（128/192/256）
     * @return 返回具体密钥
     */
    private static String generator(int keySize) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(keySize, secureRandom);
            SecretKey secretKey = keyGen.generateKey();
            return Base64Util.encode(secretKey.getEncoded());
        } catch (Exception e) {
            logger.error("generate key error", e);
            return "";
        }
    }


    /**
     * 通过密码和盐派生密钥（Base64编码）
     *
     * @param baseKey    用户密码
     * @param saltBase64 Base64编码的盐值
     * @param iterations 迭代次数
     * @param keyLength  密钥长度（128/192/256）
     * @return 派生密钥
     */
    public static String driveKey(String baseKey, String saltBase64, Integer iterations, AesKeyLengthEnum keyLength) {
        try {
            iterations = iterations == null || iterations > DEFAULT_ITERATIONS ? DEFAULT_ITERATIONS : iterations;
            keyLength = keyLength == null ? AesKeyLengthEnum.L_256 : keyLength;
            byte[] salt = Base64Util.decode(saltBase64);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(DERIVATION_ALGORITHM);
            KeySpec spec = new PBEKeySpec(baseKey.toCharArray(), salt, iterations, keyLength.getKeyLength());
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
            return Base64Util.encode(secretKey.getEncoded());
        } catch (Exception e) {
            logger.error("derive key error", e);
            return null;
        }
    }


    /**
     * GCM模式加密
     *
     * @param content   明文
     * @param base64Key 加密密钥
     * @return 加密后密文
     */
    public static String encrypt(String content, String base64Key) {
        return encrypt(content, base64Key, null, DEFAULT_ITERATIONS, AesKeyLengthEnum.L_256);
    }


    /**
     * GCM模式加密
     */
    public static String encrypt(String content, String base64Key, String saltBase64) {
        return encrypt(content, base64Key, saltBase64, DEFAULT_ITERATIONS, AesKeyLengthEnum.L_256);
    }

    /**
     * GCM模式加密
     */
    public static String encrypt(String content, String baseKey, String saltBase64, Integer iterations, AesKeyLengthEnum keyLength) {
        try {
            iterations = iterations == null ? DEFAULT_ITERATIONS : iterations;
            keyLength = keyLength == null ? AesKeyLengthEnum.L_256 : keyLength;
            // 派生密钥
            byte[] key = Base64Util.decode(StringUtil.isNotBlank(saltBase64) ? driveKey(baseKey, saltBase64, iterations, keyLength) : baseKey);
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

            // 生成随机IV
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            // 合并IV和密文
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return Base64Util.encode(combined);
        } catch (Exception e) {
            logger.error("encrypt gcm error:", e);
            return null;
        }
    }

    /**
     * GCM模式解密
     */
    public static String decrypt(String content, String base64Key) {
        return decrypt(content, base64Key, null, DEFAULT_ITERATIONS, AesKeyLengthEnum.L_256);
    }

    /**
     * GCM模式解密
     */
    public static String decrypt(String content, String base64Key, String saltBase64) {
        return decrypt(content, base64Key, saltBase64, DEFAULT_ITERATIONS, AesKeyLengthEnum.L_256);
    }

    /**
     * 解密（使用默认参数）
     */
    public static String decrypt(String content, String base64Key, String saltBase64, Integer iterations, AesKeyLengthEnum keyLength) {
        try {
            iterations = iterations == null ? DEFAULT_ITERATIONS : iterations;
            keyLength = keyLength == null ? AesKeyLengthEnum.L_256 : keyLength;
            byte[] key = Base64Util.decode(StringUtil.isNotBlank(saltBase64) ? driveKey(base64Key, saltBase64, iterations, keyLength) : base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

            byte[] combined = Base64Util.decode(content);

            // 分离IV和密文
            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("decrypt gcm error:", e);
            return null;
        }
    }

    // 从 Base64 字符串恢复 SecretKey
    public static SecretKey stringToKey(String keyStr) {
        byte[] keyBytes = Base64Util.decode(keyStr);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /***
     * 加密
     */
    public static String encryptCbc(String data, String base64Key) {
        try {
            byte[] key = Base64Util.decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // 合并IV和密文
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64Util.encode(combined);
        } catch (Exception e) {
            logger.error("encrypt error:", e);
            return null;
        }

    }

    /**
     * 解密
     */
    public static String decryptCbc(String data, String base64Key) {
        try {
            byte[] key = Base64Util.decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

            byte[] combined = Base64Util.decode(data);

            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(CBC);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("decrypt error:", e);
            return null;
        }
    }

    /**
     * 将传入的明文转换为密文
     */
    public static String encryptEcb(String str, String key) {
        try {
            if (StringUtil.isBlank(str) || StringUtil.isBlank(key)) {
                return null;
            }
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, ALGORITHM);

            // 创建密码器
            Cipher cipher = Cipher.getInstance(ECB);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] byteContent = str.getBytes();
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result);
        } catch (Exception e) {
            logger.error("encrypt error:", e);
            return null;
        }

    }

    /**
     * 将传入的密文转换为明文
     */
    public static String decryptEcb(String str, String key) {
        try {
            byte[] content = parseHexStr2Byte(str);
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, ALGORITHM);

            // 创建密码器
            Cipher cipher = Cipher.getInstance(ECB);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            if (content != null) {
                byte[] result = cipher.doFinal(content);
                return new String(result);
            }
        } catch (Exception e) {
            logger.error("decrypt error:", e);
        }
        return null;
    }


    /**
     * 将二进制转换成十六进制
     */
    private static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将十六进制转换为二进制
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.isEmpty()) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
        String generator = "JFPtXJ26tvmkqtkBJkz0St+Lpbf3XqZeTSOdNKMfVUk=";
        String encrypt = "6nURf9sL+JsD53Jpv3E7Rj8VEZbeTatQ40vKKg8NmpTOXuijnQpJ5tqwptYmpmXdgA==";
        System.out.println(decrypt(encrypt, generator, "111", 12, AesKeyLengthEnum.L_256));
    }
}
