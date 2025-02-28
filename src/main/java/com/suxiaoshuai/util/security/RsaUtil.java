package com.suxiaoshuai.util.security;

import com.suxiaoshuai.util.charset.CharsetUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密工具类，提供RSA加密、解密、签名、验签等功能。
 * 
 * @author sxs
 */
public class RsaUtil {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(RsaUtil.class);

    /** RSA密钥长度 */
    private static final int KEY_INIT_SIZE = 1024;

    /** RSA签名算法 */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    /** 默认字符编码 */
    private static final String DEFAULT_ENCODING = CharsetUtil.UTF_8;

    /** RSA加密算法名称 */
    public static final String KEY_ALGORITHM = "RSA";

    /** RSA签名算法名称 */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /** RSA单次加密最大明文长度 */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** RSA单次解密最大密文长度 */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成RSA密钥对
     *
     * @return 包含公钥和私钥的键值对，如果生成失败则返回空值对
     */
    public static Pair<RSAPublicKey, RSAPrivateKey> getKeys() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(KEY_INIT_SIZE);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            return Pair.of(publicKey, privateKey);
        } catch (Exception e) {
            log.error("get keys error:", e);
        }
        return Pair.of(null, null);

    }

    /**
     * 根据私钥文件路径加载pfx私钥文件
     *
     * @param pfxPath    pfx私钥文件路径
     * @param priKeyPass 私钥文件密码
     * @return 私钥对象，如果加载失败则返回null
     */
    public static PrivateKey getPrivateKeyByFile(String pfxPath, String priKeyPass) {
        try {
            return RsaReadUtil.getPrivateKeyFromFile(pfxPath, priKeyPass);
        } catch (Exception e) {
            log.error("get private key error:", e);
            return null;
        }
    }

    /**
     * 通过流加载pfx私钥文件
     *
     * @param pfxBytes   文件流数据
     * @param priKeyPass 私钥密码
     * @return 私钥对象，如果加载失败则返回null
     */
    public static PrivateKey getPrivateKeyByStream(byte[] pfxBytes, String priKeyPass) {
        try {
            return RsaReadUtil.getPrivateKeyByStream(pfxBytes, priKeyPass);
        } catch (Exception e) {
            log.error("get private key error:", e);
            return null;
        }
    }

    /**
     * 通过cer文件路径加载公钥
     *
     * @param pubCerPath 公钥路径
     * @return 公钥对象，如果加载失败则返回null
     */
    public static PublicKey getPublicKeyByCerFile(String pubCerPath) {
        try {
            return RsaReadUtil.getPublicKeyFromFile(pubCerPath);
        } catch (Exception e) {
            log.error("get public key error:", e);
            return null;
        }
    }

    /**
     * 根据公钥内容加载cer格式公钥
     *
     * @param pubKeyText 公钥内容
     * @return 公钥对象，如果加载失败则返回null
     */
    public static PublicKey getPublicKeyByCerContent(String pubKeyText) {
        try {
            return RsaReadUtil.getPublicKeyByText(pubKeyText);
        } catch (Exception e) {
            log.error("get public key error:", e);
            return null;
        }
    }

    /**
     * 根据私钥文件加密
     *
     * @param src     待加密的原文
     * @param pfxPath 私钥证书路径
     * @param pwd     证书密码
     * @return 加密后的字符串，如果加密失败则返回null
     */
    public static String encryptByPriPfxFile(String src, String pfxPath, String pwd) {

        PrivateKey privateKey = getPrivateKeyByFile(pfxPath, pwd);
        if (privateKey == null) {
            return null;
        }
        return encryptByPrivateKey(src, privateKey);
    }

    /**
     * 根据私钥文件流加密pfx私钥文件
     *
     * @param src        待加密原文
     * @param pfxBytes   文件流
     * @param priKeyPass 私钥文件密码
     * @return 加密后的字符串，如果加密失败则返回null
     */
    public static String encryptByPriPfxStream(String src, byte[] pfxBytes, String priKeyPass) {
        PrivateKey privateKey = getPrivateKeyByStream(pfxBytes, priKeyPass);
        return encryptByPrivateKey(src, privateKey);
    }

    /**
     * 指定Cer公钥路径解密
     *
     * @param src        待解密原文
     * @param pubCerPath 公钥cer文件路径
     * @return 解密后的字符串，如果解密失败则返回null
     */
    public static String decryptByPubCerFile(String src, String pubCerPath) {

        return decryptByPublicKey(src, getPublicKeyByCerFile(pubCerPath));
    }

    /**
     * 根据公钥文本解密
     *
     * @param src        待解密原文
     * @param pubKeyText 公钥内容
     * @return 解密后的字符串，如果解密失败则返回null
     */
    public static String decryptByPubCerText(String src, String pubKeyText) {
        return decryptByPublicKey(src, getPublicKeyByCerContent(pubKeyText));
    }

    /**
     * 根据私钥加密
     *
     * @param src        需要加密的原文
     * @param privateKey 私钥对象
     * @return 加密后的十六进制字符串，如果加密失败则返回null
     */
    public static String encryptByPrivateKey(String src, PrivateKey privateKey) {
        try {
            byte[] destBytes = rsaByPrivateKey(src.getBytes(), privateKey, Cipher.ENCRYPT_MODE);

            if (destBytes == null) {
                return null;
            }
            return FormatUtil.byte2Hex(destBytes);
        } catch (Exception e) {
            log.error("encrypt error:", e);
            return null;
        }
    }

    /**
     * RSA私钥签名,默认UTF-8编码
     *
     * @param content    待签名数据
     * @param privateKey 商户私钥
     * @return 签名值
     */
    public static String signWithPriKey(String content, String privateKey) {
        return signWithPriKey(content, privateKey, DEFAULT_ENCODING);
    }

    /**
     * RSA私钥签名
     *
     * @param content    待签名数据
     * @param privateKey 商户私钥
     * @param encode     字符集编码
     * @return 签名值
     */
    public static String signWithPriKey(String content, String privateKey, String encode) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64Util.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(encode));
            return Base64Util.encode(signature.sign(), encode);
        } catch (Exception e) {
            log.error("sign error:", e);
            return null;
        }
    }

    /**
     * 使用公钥加密数据
     *
     * @param data      源数据字节数组
     * @param publicKey 公钥(BASE64编码)
     * @return 加密后的字节数组，如果加密失败则返回null
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) {
        try {
            byte[] keyBytes = Base64Util.decode(publicKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicK = keyFactory.generatePublic(x509KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicK);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return encryptedData;
        } catch (Exception e) {
            log.error("encrypt by public key error:", e);
        }
        return null;
    }

    /**
     * 使用私钥加密数据
     *
     * @param data       源数据字节数组
     * @param privateKey 私钥(BASE64编码)
     * @return 加密后的字节数组，如果加密失败则返回null
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) {
        try {
            byte[] keyBytes = Base64Util.decode(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateK);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return encryptedData;
        } catch (Exception e) {
            log.error("encrypt by private key error:", e);
            return null;
        }

    }

    /**
     * 使用私钥解密数据
     *
     * @param encryptedData 已加密的数据字节数组
     * @param privateKey    私钥(BASE64编码)
     * @return 解密后的字节数组，如果解密失败则返回null
     * @throws Exception 解密过程中可能出现的异常
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        try {
            byte[] keyBytes = Base64Util.decode(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            RSAPrivateKey privateK = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateK);
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (Exception e) {
            log.error("decrypt by private key error:", e);
            return null;
        }

    }

    /**
     * 使用公钥解密数据
     *
     * @param src       待解密的十六进制字符串
     * @param publicKey 公钥对象
     * @return 解密后的字符串，如果解密失败则返回null
     */
    public static String decryptByPublicKey(String src, PublicKey publicKey) {

        try {
            byte[] destBytes = rsaByPublicKey(FormatUtil.hex2Bytes(src), publicKey, Cipher.DECRYPT_MODE);
            if (destBytes == null) {
                return null;
            }
            return new String(destBytes, RsaConst.ENCODE);
        } catch (Exception e) {
            log.error("decrypt by public key error:", e);
        }
        return null;
    }

    /**
     * 使用公钥解密数据
     *
     * @param encryptedData 已加密的数据字节数组
     * @param publicKey     公钥(BASE64编码)
     * @return 解密后的字节数组，如果解密失败则返回null
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] keyBytes = Base64Util.decode(publicKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicK = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicK);
            int inputLen = encryptedData.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            return out.toByteArray();
        } catch (Exception e) {
            log.error("decrypt by public key error:", e);
            return null;
        }

    }

    /**
     * RSA公钥验签，默认UTF-8编码
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 公钥
     * @return 布尔值
     */
    public static boolean verifySignWithPubKey(String content, String sign, String publicKey) {
        return verifySignWithPubKey(content, sign, publicKey, DEFAULT_ENCODING);
    }

    /**
     * RSA公钥验签
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 公钥
     * @param encode    字符集编码
     * @return 布尔值
     */
    public static boolean verifySignWithPubKey(String content, String sign, String publicKey, String encode) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            byte[] encodedKey = Base64Util.decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(encode));
            return signature.verify(Base64Util.decode(sign));
        } catch (Exception e) {
            log.error("verifySignWithPubKey error:", e);
            return false;
        }
    }

    /**
     * 私钥算法
     *
     * @param srcData    源字节数据
     * @param privateKey 私钥对象
     * @param mode       加密模式（Cipher.ENCRYPT_MODE）或解密模式（Cipher.DECRYPT_MODE）
     * @return 处理后的字节数组，如果处理失败则返回null
     */
    public static byte[] rsaByPrivateKey(byte[] srcData, PrivateKey privateKey, int mode) {
        try {
            Cipher cipher = Cipher.getInstance(RsaConst.RSA_CHIPER);
            cipher.init(mode, privateKey);
            // 分段加密
            int blockSize = (mode == Cipher.ENCRYPT_MODE) ? RsaConst.ENCRYPT_KEYSIZE : RsaConst.DECRYPT_KEYSIZE;
            byte[] decryptData = null;

            for (int i = 0; i < srcData.length; i += blockSize) {
                byte[] doFinal = cipher.doFinal(subarray(srcData, i, i + blockSize));

                decryptData = addAll(decryptData, doFinal);
            }
            return decryptData;
        } catch (NoSuchAlgorithmException e) {
            log.error("私钥算法-不存在的解密算法:", e);
        } catch (NoSuchPaddingException e) {
            log.error("私钥算法-无效的补位算法:", e);
        } catch (IllegalBlockSizeException e) {
            log.error("私钥算法-无效的块大小:", e);
        } catch (BadPaddingException e) {
            log.error("私钥算法-补位算法异常:", e);
        } catch (InvalidKeyException e) {
            log.error("私钥算法-无效的私钥:", e);
        } catch (Exception e) {
            log.error("rsaByPrivateKey error:", e);
        }
        return null;
    }

    /**
     * 公钥算法
     *
     * @param srcData   源字节数据
     * @param publicKey 公钥对象
     * @param mode      加密模式（Cipher.ENCRYPT_MODE）或解密模式（Cipher.DECRYPT_MODE）
     * @return 处理后的字节数组，如果处理失败则返回null
     */
    public static byte[] rsaByPublicKey(byte[] srcData, PublicKey publicKey, int mode) {
        try {
            Cipher cipher = Cipher.getInstance(RsaConst.RSA_CHIPER);
            cipher.init(mode, publicKey);
            // 分段加密
            int blockSize = (mode == Cipher.ENCRYPT_MODE) ? RsaConst.ENCRYPT_KEYSIZE : RsaConst.DECRYPT_KEYSIZE;
            byte[] encryptedData = null;
            for (int i = 0; i < srcData.length; i += blockSize) {
                // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
                byte[] doFinal = cipher.doFinal(subarray(srcData, i, i + blockSize));
                encryptedData = addAll(encryptedData, doFinal);
            }
            return encryptedData;

        } catch (NoSuchAlgorithmException e) {
            log.error("公钥算法-不存在的解密算法:", e);
        } catch (NoSuchPaddingException e) {
            log.error("公钥算法-无效的补位算法:", e);
        } catch (IllegalBlockSizeException e) {
            log.error("公钥算法-无效的块大小:", e);
        } catch (BadPaddingException e) {
            log.error("公钥算法-补位算法异常:", e);
        } catch (InvalidKeyException e) {
            log.error("公钥算法-无效的私钥:", e);
        } catch (Exception e) {
            log.error("rsaByPublicKey error:", e);
        }
        return null;
    }

    /**
     * 合并两个字节数组
     *
     * @param array1 第一个字节数组
     * @param array2 第二个字节数组
     * @return 合并后的新字节数组，如果其中一个数组为null，则返回另一个数组的副本
     */
    public static byte[] addAll(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 复制字节数组
     *
     * @param array 待复制的字节数组
     * @return 复制后的新字节数组，如果输入为null则返回null
     */
    public static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 截取字节数组的指定部分
     *
     * @param array               源字节数组
     * @param startIndexInclusive 起始位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 截取后的新字节数组，如果输入为null则返回null；
     *         如果起始位置小于0则从0开始；
     *         如果结束位置大于数组长度则取到数组末尾；
     *         如果计算得到的新数组长度小于等于0则返回空数组
     */
    public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;

        if (newSize <= 0) {
            return new byte[0];
        }

        byte[] subarray = new byte[newSize];

        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);

        return subarray;
    }
}
