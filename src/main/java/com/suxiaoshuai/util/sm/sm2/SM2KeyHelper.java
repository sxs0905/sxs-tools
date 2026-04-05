package com.suxiaoshuai.util.sm.sm2;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

/**
 * SM2密钥工具类
 * 1、密钥生成`
 * 2、私钥对象转换解析
 * 3、公钥对象转换解析
 */
public class SM2KeyHelper {
    /**
     * 随机生成SM2 公私钥
     */
    public SM2KeyPair getKeyPair(){
        ECKeyGenerationParameters keyGenerationParams = new ECKeyGenerationParameters(SM2Constants.DOMAIN_PARAMS, new SecureRandom());
        ECKeyPairGenerator keyGen = new ECKeyPairGenerator();
        keyGen.init(keyGenerationParams);
        AsymmetricCipherKeyPair keyPair = keyGen.generateKeyPair();
        ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters) keyPair.getPublic();
        byte[] privateKey = ((ECPrivateKeyParameters) keyPair.getPrivate()).getD().toByteArray();
        byte[] publicKey = ecPublicKeyParameters.getQ().getEncoded(false);
        return new SM2KeyPair(Hex.toHexString(publicKey).substring(2), Hex.toHexString(privateKey));
    }


    /**
     * 构建公钥
     *
     */
    private ECPublicKeyParameters buildECPublicKeyParameters(byte[] publicKeyX, byte[] publicKeyY) {
        ECPoint pointQ = SM2Constants.CURVE.createPoint(new BigInteger(1, publicKeyX), new BigInteger(1, publicKeyY));
        return new ECPublicKeyParameters(pointQ, SM2Constants.DOMAIN_PARAMS);
    }

    /**
     * 构建带UserID参数的公钥
     */
    public ParametersWithID buildPublicParametersWithID (byte[] publicKeyX, byte[] publicKeyY){
        //获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),sm2ECParameters.getG(), sm2ECParameters.getN());
        ECPoint pukPoint = sm2ECParameters.getCurve().createPoint(new BigInteger(1, publicKeyX), new BigInteger(1, publicKeyY));
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);
        return new ParametersWithID(publicKeyParameters, SM2Constants.userId.getBytes());
    }

    /**
     * 构建私钥
     *
     * @param privateKey
     */
    public ECPrivateKeyParameters buildECPrivateKeyParameters(byte[] privateKey) {
        BigInteger d = new BigInteger(1, privateKey);
        return new ECPrivateKeyParameters(d, SM2Constants.DOMAIN_PARAMS);
    }

    /**
     * 构建带UserId参数的私钥
     * @param privateKey
     * @return
     * @throws Exception
     */
    public ParametersWithID buildPrivateParametersWithID (byte[] privateKey) throws NoSuchAlgorithmException{
        //获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),sm2ECParameters.getG(), sm2ECParameters.getN());

        BigInteger privateKeyD = new BigInteger(privateKey);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, domainParameters);
        return new ParametersWithID(new ParametersWithRandom(privateKeyParameters, SecureRandom.getInstance("SHA1PRNG")), Strings.toByteArray(SM2Constants.userId));
    }



    /**
     * 解析公钥字符
     *
     * @param publicKey
     * @return
     * @throws Exception
     */
    public ECPublicKeyParameters getEcPublicKey(byte[] publicKey){
        int length = 32;
        byte[] x = new byte[length];
        byte[] y = new byte[length];
        if(publicKey.length == 68){
            //含头结构的公钥
            System.arraycopy(publicKey, 4, x, 0, length);
            System.arraycopy(publicKey, 36, y, 0, length);
            return buildECPublicKeyParameters(x,y);
        }else if(publicKey.length == 64){
            //不含头结构的公钥
            System.arraycopy(publicKey, 0, x, 0, length);
            System.arraycopy(publicKey, 32, y, 0, length);
            return buildECPublicKeyParameters(x,y);
        }
        return null;
    }

    public ParametersWithID getEcPublicKeyWithID(byte[] publicKey){
        int length = 32;
        byte[] x = new byte[length];
        byte[] y = new byte[length];
        if(publicKey.length == 68){
            //含头结构的公钥
            System.arraycopy(publicKey, 4, x, 0, length);
            System.arraycopy(publicKey, 36, y, 0, length);
            return buildPublicParametersWithID(x,y);
        }else if(publicKey.length == 64){
            //不含头结构的公钥
            System.arraycopy(publicKey, 0, x, 0, length);
            System.arraycopy(publicKey, 32, y, 0, length);
            return buildPublicParametersWithID(x,y);
        }
        return null;
    }

    /**
     * 解析国密证书获取公钥
     *
     * @param cer base64编码证书字符串
     * @return
     * @throws Exception
     */
    public byte[] getPublicKeyByCer(String cer)  throws IOException {
        cer = cer.replaceAll("-----BEGIN CERTIFICATE-----", "").replaceAll("-----END CERTIFICATE-----", "").replaceAll("\r", "").replaceAll("\n", "");
        InputStream inStream = new ByteArrayInputStream(Base64.decode(cer));
        ASN1InputStream aIn = new ASN1InputStream(inStream);
        ASN1Sequence seq = (ASN1Sequence) aIn.readObject();
        Certificate certificate = Certificate.getInstance(seq);
        byte[] publicKey = certificate.getSubjectPublicKeyInfo().getPublicKeyData().getEncoded();
        return publicKey;
    }
    /**
     * 解析证书公钥字符
     *
     * @param publicKey
     * @return
     * @throws Exception
     */
/*    private ECPublicKeyParameters getCerPublicKey(byte[] publicKey) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        BCECPublicKey localECPublicKey = (BCECPublicKey)keyFactory.generatePublic(keySpec);
        return new ECPublicKeyParameters(localECPublicKey.getQ(),SM2Constants.DOMAIN_PARAMS);
    }*/

    /**
     * 转换公钥对象
     * @param publicKey
     * @return
     * @throws Exception
     */
    public PublicKey convertPubKey(byte[] publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException{
        ECPublicKeyParameters publicKeyParameters = getEcPublicKey(publicKey);
        ECParameterSpec ecParameters = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(publicKeyParameters.getQ(),ecParameters);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey pub = keyFactory.generatePublic(publicKeySpec);
        return pub;
    }

    /**
     * 转换私钥对象
     * @param privateKey
     * @return
     * @throws Exception
     */
    public PrivateKey convertPriKey(byte[] privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ECPrivateKeyParameters privateKeyParameters = buildECPrivateKeyParameters(privateKey);
        ECParameterSpec ecParameters = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(privateKeyParameters.getD(),ecParameters);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        PrivateKey pri = keyFactory.generatePrivate(privateKeySpec);
        return pri;
    }

}
