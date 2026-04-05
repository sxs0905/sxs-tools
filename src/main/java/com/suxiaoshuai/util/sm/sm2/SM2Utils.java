package com.ly.fn.biz.newpay.emob.common.utils.sm.platform.sm2;


import com.ly.fn.biz.newpay.emob.common.utils.sm.platform.sm3.SM3Helper;
import com.ly.fn.inf.matrix.logger.api.Logger;
import com.ly.fn.inf.matrix.logger.api.LoggerFactory;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;

public class SM2Utils {
    private static final Logger logger = LoggerFactory.getLogger(SM2Utils.class);

    public static byte[] sign(String data, String pri_key) throws Exception {
        SM2Helper sM2Helper = new SM2Helper();
        //国密加签之前对签名字符串进行sm3摘要
        byte[] sm3SignData = SM3Helper.hash(data.getBytes(StandardCharsets.UTF_8));
        return sM2Helper.sign(sm3SignData, pri_key);
    }

    /**
     * 验证签名
     *
     * @param sign    签名数据
     * @param oriData 原数据
     */
    public static boolean verySign(byte[] sign, String oriData, String center_pub_key) throws IOException {

        logger.info("软件验证签名开始----------------pub：{}", center_pub_key);
        SM2Helper sm2Helper = new SM2Helper();
        byte[] oriHash = SM3Helper.hash(oriData.getBytes(StandardCharsets.UTF_8));
        logger.info("对元数据进行hash：{}", Hex.toHexString(oriHash));
        return sm2Helper.verifySign(oriHash, sign, center_pub_key);
    }


    public static byte[] encrypt(byte[] data, String center_pub_key) throws InvalidCipherTextException {
        SM2Helper sm2Helper = new SM2Helper();
        byte[] pub_key = Hex.decode(center_pub_key);
        return sm2Helper.encrypt(data, pub_key);
    }

    public static byte[] decrypt(byte[] encryptedData, String pri_key) throws InvalidCipherTextException {
        SM2Helper sm2Helper = new SM2Helper();
        return sm2Helper.decrypt(encryptedData, Hex.decode(pri_key));
    }


    public static byte[] encryptUnion(String data, PublicKey publicKey) {
        return changeC1C2C3ToC1C3C2(sm2EncryptUnion(data.getBytes(StandardCharsets.UTF_8), publicKey));
    }


    private static byte[] changeC1C2C3ToC1C3C2(byte[] c1c2c3) {
        int c1Len = (SM2Constants.x9ECParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1;
        byte[] result = new byte[c1c2c3.length];
        System.arraycopy(c1c2c3, 0, result, 0, c1Len);
        System.arraycopy(c1c2c3, c1c2c3.length - 32, result, c1Len, 32);
        System.arraycopy(c1c2c3, c1Len, result, c1Len + 32, c1c2c3.length - c1Len - 32);
        return result;
    }

    private static byte[] sm2EncryptUnion(byte[] data, PublicKey key) {
        BCECPublicKey localECPublicKey = (BCECPublicKey) key;
        ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(localECPublicKey.getQ(), SM2Constants.ecDomainParameters);
        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(ecPublicKeyParameters, new SecureRandom()));
        try {
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }
}
