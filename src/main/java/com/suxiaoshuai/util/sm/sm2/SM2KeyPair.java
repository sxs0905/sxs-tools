package com.suxiaoshuai.util.sm.sm2;


/**
 * SM2 非对称密钥对
 */
public class SM2KeyPair {
    private final String privateKey;
    private final String publicKey;


    public SM2KeyPair(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

}
