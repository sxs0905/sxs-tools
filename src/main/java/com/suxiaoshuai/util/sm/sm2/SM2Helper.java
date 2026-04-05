package com.suxiaoshuai.util.sm.sm2;

import org.bouncycastle.asn1.*;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 国密sm2非对称加解密算法帮助类
 *
 * @Author: HJY
 * @CreateDate: 2020/9/16 16:57
 */
public class SM2Helper {
    // private static final Logger logger = LoggerFactory.getLogger(SM2Helper.class);

    /**
     * 公钥加密
     *
     * @param input                 待加密数据
     * @param ecPublicKeyParameters 公钥参数
     *                              //     * @param mode 加密方式
     * @return
     * @throws Exception
     */
    private byte[] encrypt(byte[] input, ECPublicKeyParameters ecPublicKeyParameters) throws InvalidCipherTextException {
        SM2Engine engine = new SM2Engine();
        ParametersWithRandom parametersWithRandom = new ParametersWithRandom(ecPublicKeyParameters, new SecureRandom());
        engine.init(true, parametersWithRandom);
        return engine.processBlock(input, 0, input.length);
    }

    /**
     * 公钥加密
     *
     * @param indata    明文数据
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public byte[] encrypt(byte[] indata, byte[] publicKey) throws InvalidCipherTextException {
        ECPublicKeyParameters ecPublicKeyParameters = new SM2KeyHelper().getEcPublicKey(publicKey);
        byte[] enData = encrypt(indata, ecPublicKeyParameters);
        byte[] newEnData = new byte[enData.length - 1];
        System.arraycopy(enData, 1, newEnData, 0, newEnData.length);
        return newEnData;
    }

    /**
     * 私钥解密
     *
     * @param input                  待解密数据
     * @param ecPrivateKeyParameters 私钥参数
     *                               //     * @param mode 加密方式
     * @return
     * @throws Exception
     */
    private byte[] decrypt(byte[] input, ECPrivateKeyParameters ecPrivateKeyParameters) throws InvalidCipherTextException {
        byte[] encryptData = input;
        String head = "04";
        String inputHex = Hex.toHexString(input);
        if (!inputHex.startsWith(head)) {
            encryptData = Hex.decode(head.concat(inputHex));
        }
        SM2Engine engine = new SM2Engine();
        engine.init(false, ecPrivateKeyParameters);
        return engine.processBlock(encryptData, 0, encryptData.length);
    }

    /**
     * 私钥解密
     *
     * @param encryptData 加密密文
     * @param privateKey  私钥
     */
    public byte[] decrypt(byte[] encryptData, byte[] privateKey) throws InvalidCipherTextException {
        ECPrivateKeyParameters ecPrivateKeyParameters = new SM2KeyHelper().buildECPrivateKeyParameters(privateKey);
        return decrypt(encryptData, ecPrivateKeyParameters);
    }

    /**
     * SM2 签名运算
     *
     * @param indata     待签名的明文
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public byte[] sign(byte[] indata, String privateKey) throws NoSuchAlgorithmException, CryptoException, IOException {
        // logger.info("签名密钥key：{}", privateKey);
        byte[] priKey = Hex.decode(privateKey);
        SM2Signer signer = new SM2Signer();
        ParametersWithID parametersWithID = new SM2KeyHelper().buildPrivateParametersWithID(priKey);
        signer.init(true, parametersWithID);
        signer.update(indata, 0, indata.length);
        byte[] sign = signer.generateSignature();
        ASN1Sequence var2 = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(sign));
        if (var2.size() != 2) {
            return null;
        } else {
            BigInteger var3 = ASN1Integer.getInstance(var2.getObjectAt(0)).getValue();
            BigInteger var4 = ASN1Integer.getInstance(var2.getObjectAt(1)).getValue();
            byte[] rBytes = modifyRSFixedBytes(var3.toByteArray());
            byte[] sBytes = modifyRSFixedBytes(var4.toByteArray());
            sign = Arrays.concatenate(rBytes, sBytes);
        }
        return sign;
    }

    private static byte[] modifyRSFixedBytes(byte[] rs) {
        int length = rs.length;
        int fixedLength = 32;
        byte[] result = new byte[fixedLength];
        if (length < 32) {
            System.arraycopy(rs, 0, result, fixedLength - length, length);
        } else {
            System.arraycopy(rs, length - fixedLength, result, 0, fixedLength);
        }
        return result;
    }

    /**
     * 公钥验证签名
     *
     * @param indata    原始明文数据
     * @param signData  签名数据
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public boolean verifySign(byte[] indata, byte[] signData, String publicKey) throws IOException {
        byte[] pubKey = Hex.decode(publicKey);
        //获取签名
        byte[] rBy = new byte[33];
        System.arraycopy(signData, 0, rBy, 1, 32);
        rBy[0] = 0x00;
        byte[] sBy = new byte[33];
        System.arraycopy(signData, 32, sBy, 1, 32);
        sBy[0] = 0x00;
        BigInteger R = new BigInteger(rBy);
        BigInteger S = new BigInteger(sBy);
        signData = derEncode(R, S);

        SM2Signer signer = new SM2Signer();

        ParametersWithID parametersWithID = new SM2KeyHelper().getEcPublicKeyWithID(pubKey);
        signer.init(false, parametersWithID);
        signer.update(indata, 0, indata.length);
        return signer.verifySignature(signData);
    }

    private byte[] derEncode(BigInteger var1, BigInteger var2) throws IOException {
        ASN1EncodableVector var3 = new ASN1EncodableVector();
        var3.add(new ASN1Integer(var1));
        var3.add(new ASN1Integer(var2));
        return (new DERSequence(var3)).getEncoded("DER");
    }
}
