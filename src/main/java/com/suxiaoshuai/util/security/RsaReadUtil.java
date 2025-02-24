package com.suxiaoshuai.util.security;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;


/**
 * <b>公私钥读取工具</b><br>
 * <br>
 *
 * @author 行者
 * @version 4.1.0
 */
public final class RsaReadUtil {

    private static final Logger logger = LoggerFactory.getLogger(RsaReadUtil.class);

    /**
     * 根据Cer文件读取公钥
     */
    public static PublicKey getPublicKeyFromFile(String pubCerPath) {
        try (FileInputStream pubKeyStream = new FileInputStream(pubCerPath)) {
            byte[] reads = new byte[pubKeyStream.available()];
            pubKeyStream.read(reads);
            return getPublicKeyByText(new String(reads));
        } catch (Exception e) {
            logger.error("公钥证书文件:{}读取失败", pubCerPath, e);
        }
        return null;
    }

    /**
     * 根据公钥Cer文本串读取公钥
     */
    public static PublicKey getPublicKeyByText(String pubKeyText) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(RsaConst.KEY_X509);
            BufferedReader br = new BufferedReader(new StringReader(pubKeyText));
            String line = null;
            StringBuilder keyBuffer = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("-")) {
                    keyBuffer.append(line);
                }
            }
            Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(new Base64().decode(
                    keyBuffer.toString())));
            return certificate.getPublicKey();
        } catch (Exception e) {
            logger.error("解析公钥内容:{}失败", pubKeyText, e);
        }
        return null;
    }

    /**
     * 根据私钥路径读取私钥
     */
    public static PrivateKey getPrivateKeyFromFile(String pfxPath, String priKeyPass) {
        try (InputStream priKeyStream = new FileInputStream(pfxPath)) {
            byte[] reads = new byte[priKeyStream.available()];
            priKeyStream.read(reads);
            return getPrivateKeyByStream(reads, priKeyPass);
        } catch (Exception e) {
            logger.error("解析文件:{}，读取私钥:{} 失败", pfxPath, priKeyPass, e);
        }
        return null;
    }

    /**
     * 根据PFX私钥字节流读取私钥
     */
    public static PrivateKey getPrivateKeyByStream(byte[] pfxBytes, String priKeyPass) {
        try {
            KeyStore ks = KeyStore.getInstance(RsaConst.KEY_PKCS12);
            char[] charPriKeyPass = priKeyPass.toCharArray();
            ks.load(new ByteArrayInputStream(pfxBytes), charPriKeyPass);
            Enumeration<String> aliasEnum = ks.aliases();
            String keyAlias = null;
            if (aliasEnum.hasMoreElements()) {
                keyAlias = aliasEnum.nextElement();
            }
            return (PrivateKey) ks.getKey(keyAlias, charPriKeyPass);
        } catch (IOException e) {
            // 加密失败
            logger.error("解析文件，读取私钥失败:", e);
        } catch (KeyStoreException e) {
            logger.error("私钥存储异常:", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("不存在的解密算法:", e);
        } catch (CertificateException e) {
            logger.error("证书异常:", e);
        } catch (UnrecoverableKeyException e) {
            logger.error("不可恢复的秘钥异常:", e);
        } catch (Exception e) {
            logger.error("私钥读取异常:", e);
        }
        return null;
    }
}
