package com.suxiaoshuai.util.sm.sm4;

import com.suxiaoshuai.util.string.StringUtil;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.GCMModeCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class SM4GcmUtil {

    private static final Logger logger = LoggerFactory.getLogger(SM4GcmUtil.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // -------------------------- 国密/支付场景固定参数（禁止修改） --------------------------
    /**
     * SM4密钥长度：16字节（128位，国密强制标准）
     */
    public static final int SM4_KEY_LENGTH = 16;
    /**
     * IV长度：12字节（金融/支付行业推荐，GCM模式标准适配，避免计数器溢出）
     */
    public static final int SM4_GCM_IV_LENGTH = 12;
    /**
     * 认证标签长度：16字节（国密标准，完整性校验强度最高）
     */
    public static final int SM4_GCM_TAG_LENGTH = 16;
    /**
     * 字符编码：UTF-8（与支付接口文档统一）
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
        SECURE_RANDOM.nextBytes(key);
        return key;
    }

    /**
     * 生成GCM模式推荐的IV（12字节，安全随机数）
     * 支付场景：每次加密生成新IV，随请求/响应上送，解密时必须使用相同IV
     *
     * @return 12字节随机IV字节数组
     */
    public static byte[] iv() {
        byte[] iv = new byte[SM4_GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }

    /**
     * SM4-GCM 加密（核心方法，支持AAD附加认证）
     * 加密结果：密文 + 16字节认证标签 → 拼接后Base64编码，可直接上送支付平台
     *
     * @param plainText 明文（支付场景敏感数据：身份证号、手机号、tokenCVN2等）
     * @param sm4Key    16字节SM4密钥（generateSm4Key()生成）
     * @param iv        12字节IV（generateGcmIv()生成）
     * @param aad       附加认证数据（支付场景传入公共参数：如merCode+requestNo+timestamp，可为null/空）
     * @return Base64编码的「密文+标签」拼接串（可直接存入接口参数）
     * @throws Exception 加密异常（参数非法、编码错误等）
     */
    public static byte[] encrypt(String plainText, byte[] sm4Key, byte[] iv, String aad) throws Exception {
        logger.info("SM4-GCM 加密开始,ori:{},aad:{}", plainText, aad);
        // 1. 严格参数校验（符合国密/支付规范，避免非法参数）
        if (sm4Key == null || sm4Key.length != SM4_KEY_LENGTH) {
            throw new IllegalArgumentException("SM4密钥必须为16字节（国密强制要求）");
        }
        if (iv == null || iv.length != SM4_GCM_IV_LENGTH) {
            throw new IllegalArgumentException("GCM模式IV必须为12字节（支付行业推荐）");
        }
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("敏感数据明文不可为空");
        }

        // 2. 初始化SM4-GCM加密器
        GCMModeCipher gcmCipher = GCMBlockCipher.newInstance(new SM4Engine());
        ParametersWithIV params = new ParametersWithIV(new KeyParameter(sm4Key), iv);
        gcmCipher.init(true, params);

        // 3. 处理附加认证数据AAD（若有，参与完整性校验）
        if (StringUtil.isNotBlank(aad)) {
            byte[] aadBytes = aad.getBytes(StandardCharsets.UTF_8);
            gcmCipher.processAADBytes(aadBytes, 0, aadBytes.length);
        }

        // 4. 执行加密
        byte[] plainBytes = plainText.getBytes(CHARSET);
        byte[] cipherBytes = new byte[gcmCipher.getOutputSize(plainBytes.length)];
        int cipherLen = gcmCipher.processBytes(plainBytes, 0, plainBytes.length, cipherBytes, 0);
        // 完成加密，生成16字节认证标签（自动追加到密文后）
        gcmCipher.doFinal(cipherBytes, cipherLen);
        return cipherBytes;
    }

    /**
     * SM4-GCM 解密（核心方法，自动校验完整性）
     * 解密时自动校验认证标签，若密文/标签/AAD/IV被篡改，直接抛异常
     *
     * @param dataBytes Base64解码后的数据
     * @param sm4Key    16字节SM4密钥（SM2解密encryptKey得到）
     * @param iv        12字节IV（接口上送的IV，与加密时一致）
     * @param aad       附加认证数据（与加密时完全一致：如merCode+requestNo+timestamp，可为null/空）
     * @return 解密后的敏感数据明文
     * @throws Exception 解密/校验异常（参数非法、密文篡改、标签不匹配等）
     */
    public static String decrypt(byte[] dataBytes, byte[] sm4Key, byte[] iv, String aad) throws Exception {
        // 1. 严格参数校验
        if (sm4Key == null || sm4Key.length != SM4_KEY_LENGTH) {
            throw new IllegalArgumentException("SM4密钥必须为16字节（国密强制要求）");
        }
        if (iv == null || iv.length != SM4_GCM_IV_LENGTH) {
            throw new IllegalArgumentException("GCM模式IV必须为12字节（支付行业推荐）");
        }
        if (dataBytes == null || dataBytes.length == 0) {
            throw new IllegalArgumentException("密文（含标签）不可为空");
        }

        // 2. Base64解码「密文+标签」拼接串
        //        byte[] cipherTagBytes = Base64.decode(cipherTagBase64);
        // 校验密文长度：至少大于标签长度（16字节），否则为非法密文
        if (dataBytes.length <= SM4_GCM_TAG_LENGTH) {
            throw new IllegalArgumentException("密文（含标签）长度非法，可能被篡改/截断");
        }

        // 3. 初始化SM4-GCM解密器
        GCMModeCipher gcmCipher = GCMBlockCipher.newInstance(new SM4Engine());
        ParametersWithIV params = new ParametersWithIV(new KeyParameter(sm4Key), iv);
        gcmCipher.init(false, params);

        // 4. 处理附加认证数据AAD（必须与加密时完全一致，否则校验失败）
        if (StringUtil.isNotBlank(aad)) {
            byte[] aadBytes = aad.getBytes(StandardCharsets.UTF_8);
            gcmCipher.processAADBytes(aadBytes, 0, aadBytes.length);
        }

        // 5. 执行解密（自动校验标签，标签不匹配直接抛InvalidCipherTextException）
        byte[] plainBytes = new byte[gcmCipher.getOutputSize(dataBytes.length)];
        int plainLen = gcmCipher.processBytes(dataBytes, 0, dataBytes.length, plainBytes, 0);
        plainLen += gcmCipher.doFinal(plainBytes, plainLen);

        // 6. 转换为明文并返回
        return new String(plainBytes, 0, plainLen, CHARSET);
    }
}
