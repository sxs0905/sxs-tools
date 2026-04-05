package com.suxiaoshuai.util.sm.sm4;


import com.suxiaoshuai.util.httpclient.HttpUtils;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

public class Sm4CbcUtils {
    private static final Logger logger = LoggerFactory.getLogger(Sm4CbcUtils.class);

    /**
     * SM4密钥长度：16字节（128位，国密标准）
     */
    public static final int SM4_KEY_LENGTH = 16;

    /**
     * IV长度：16字节（CBC模式要求与分组长度一致，SM4分组长度为16字节）
     */
    public static final int SM4_IV_LENGTH = 16;

    /**
     * 字符编码：UTF-8（文档指定报文编码）
     */
    private static final String CHARSET = "UTF-8";

    /**
     * 生成符合国密要求的SM4密钥（16字节，安全随机数）
     * 支付场景：用于加密敏感数据的临时密钥，单次使用后立即销毁
     *
     * @return 16字节SM4密钥字节数组
     */
    public static byte[] key() {
        byte[] key = new byte[SM4_KEY_LENGTH];
        new SecureRandom().nextBytes(key);
        return key;
    }


    /**
     * 生成随机IV（16字节）
     * 文档场景：每次加密需生成新IV，与密文一同传输
     *
     * @return 16字节随机IV
     */
    public static byte[] iv() {
        byte[] iv = new byte[SM4_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * SM4-CBC加密（PKCS#7填充）
     * 文档场景：加密敏感字段（如billOwnerCertifId、billOwnerMobileNo）
     *
     * @param plainText 明文（敏感数据）
     * @param sm4Key    SM4密钥（16字节）
     * @param iv        随机IV（16字节）
     * @return Base64编码后的密文
     * @throws Exception 加密异常（编码错误、参数非法）
     */
    public static String encrypt(String plainText, byte[] sm4Key, byte[] iv) throws Exception {
        // 校验参数合法性（符合文档数据类型要求）
        if (sm4Key == null || sm4Key.length != SM4_KEY_LENGTH) {
            throw new IllegalArgumentException("SM4密钥必须为16字节");
        }
        if (iv == null || iv.length != SM4_IV_LENGTH) {
            throw new IllegalArgumentException("IV必须为16字节");
        }
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("明文不可为空");
        }

        // 初始化SM4-CBC加密器（PKCS#7填充）
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(CBCBlockCipher.newInstance(new SM4Engine()), new PKCS7Padding());
        ParametersWithIV params = new ParametersWithIV(new KeyParameter(sm4Key), iv);
        cipher.init(true, params);

        // 执行加密
        byte[] plainBytes = plainText.getBytes(CHARSET);
        byte[] cipherBytes = new byte[cipher.getOutputSize(plainBytes.length)];
        int len = cipher.processBytes(plainBytes, 0, plainBytes.length, cipherBytes, 0);
        len += cipher.doFinal(cipherBytes, len);

        // 密文Base64编码（文档要求传输编码）
        return Base64.toBase64String(cipherBytes, 0, len);
    }


    /**
     * SM4-CBC解密（PKCS#7填充）
     * 文档场景：解密平台返回的加密字段（如tokenCVN2、tkEndExpr）
     *
     * @param cipherTextBase64 Base64编码的密文
     * @param sm4Key           SM4密钥（16字节）
     * @param iv               加密时使用的IV（16字节，需与加密方一致）
     * @return 解密后的明文
     * @throws Exception 解密异常（密文篡改、密钥不匹配、编码错误）
     */
    public static String decrypt(String cipherTextBase64, byte[] sm4Key, byte[] iv) throws Exception {
        // 校验参数合法性
        if (sm4Key == null || sm4Key.length != SM4_KEY_LENGTH) {
            throw new IllegalArgumentException("SM4密钥必须为16字节");
        }
        if (iv == null || iv.length != SM4_IV_LENGTH) {
            throw new IllegalArgumentException("IV必须为16字节");
        }
        if (cipherTextBase64 == null || cipherTextBase64.isEmpty()) {
            throw new IllegalArgumentException("密文不可为空");
        }

        // 初始化SM4-CBC解密器（PKCS#7填充）
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(CBCBlockCipher.newInstance(new SM4Engine()), new PKCS7Padding());
        ParametersWithIV params = new ParametersWithIV(new KeyParameter(sm4Key), iv);
        cipher.init(false, params);

        // Base64解码密文（文档要求传输编码）
        byte[] cipherBytes = Base64.decode(cipherTextBase64);
        byte[] plainBytes = new byte[cipher.getOutputSize(cipherBytes.length)];
        int len = cipher.processBytes(cipherBytes, 0, cipherBytes.length, plainBytes, 0);
        len += cipher.doFinal(plainBytes, len);

        // 转换为明文
        return new String(plainBytes, 0, len, CHARSET);
    }

    /**
     * 辅助方法：字节数组转Base64字符串（文档传输要求）
     *
     * @param bytes 字节数组（如密钥、IV）
     * @return Base64编码字符串
     */
    public static String bytesToBase64(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Base64.toBase64String(bytes);
    }

    public static byte[] encryptCbc(String data, String key) {
        try {
            logger.info("encrypt Cbc begin。 paramStr:{},key:{}", data, key);
            byte[] keyData = key.getBytes(StandardCharsets.UTF_8);
            byte[] srcData = data.getBytes(StandardCharsets.UTF_8);
            return encrypt_CBC_NO_Padding(keyData, srcData);
        } catch (Exception var6) {
            // logger.error("encrypt Cbc error", var6);
            return null;
        }
    }

    private static byte[] encrypt_CBC_NO_Padding(byte[] key, byte[] data) throws Exception {
        if (data.length % key.length != 0) {
            byte[] tmpData = new byte[data.length + key.length - data.length % key.length];
            byte[] fillData = new byte[key.length - data.length % key.length];
            Arrays.fill(fillData, (byte) 0);
            System.arraycopy(data, 0, tmpData, 0, data.length);
            System.arraycopy(fillData, 0, tmpData, data.length, fillData.length);
            data = tmpData;
        }
        Cipher cipher = generateCbcCipher("SM4/CBC/NoPadding", 1, key);
        return cipher.doFinal(data);
    }

    private static Cipher generateCbcCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, "BC");
        Key sm4Key = new SecretKeySpec(key, "SM4");
        cipher.init(mode, sm4Key, generateIV());
        return cipher;
    }

    private static AlgorithmParameters generateIV() throws Exception {
        byte[] iv = new byte[16];
        Arrays.fill(iv, (byte) 0);
        AlgorithmParameters params = AlgorithmParameters.getInstance("SM4");
        params.init(new IvParameterSpec(iv));
        return params;
    }
}
