package com.suxiaoshuai.util.security;

import com.suxiaoshuai.util.charset.CharsetUtil;

/**
 * RSA加密算法相关常量定义
 *
 * @author sxs
 */
public final class RsaConst {

    /** 默认字符编码 */
    public final static String ENCODE = CharsetUtil.UTF_8;

    /** X.509证书标准 */
    public final static String KEY_X509 = "X509";
    
    /** PKCS#12私钥格式标准 */
    public final static String KEY_PKCS12 = "PKCS12";
    
    /** RSA加密算法名称 */
    public final static String KEY_ALGORITHM = "RSA";
    
    /** RSA数字签名算法 */
    public final static String CER_ALGORITHM = "MD5WithRSA";

    /** RSA加密填充方式 */
    public final static String RSA_CHIPER = "RSA/ECB/PKCS1Padding";

    /** RSA密钥长度 */
    public final static int KEY_SIZE = 1024;
    
    /** RSA单次加密数据块大小（1024位密钥） */
    public final static int ENCRYPT_KEYSIZE = 117;
    
    /** RSA单次解密数据块大小（1024位密钥） */
    public final static int DECRYPT_KEYSIZE = 128;
}
