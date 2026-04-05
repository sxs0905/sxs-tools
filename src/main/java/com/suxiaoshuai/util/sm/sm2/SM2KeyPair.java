package com.ly.fn.biz.newpay.emob.common.utils.sm.platform.sm2;

import lombok.Getter;

/**
 * SM2 非对称密钥对
 */
@Getter
public class SM2KeyPair {
    private final String privateKey;
    private final String publicKey;


    public SM2KeyPair(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

}
