package com.suxiaoshuai.constants;

/**
 * aes 密钥长度
 */
public enum AesKeyLengthEnum {
    /**
     * 128位
     */
    L_128(128),
    /**
     * 192位
     */
    L_192(192),
    /**
     * 256位
     */
    L_256(256),
    ;

    private AesKeyLengthEnum(int keyLength) {
        this.keyLength = keyLength;
    }

    private Integer keyLength;

    /**
     * 获取key length
     *
     * @return 秘钥长度
     */
    public Integer getKeyLength() {
        return keyLength;
    }

    /**
     * 设置key长度
     *
     * @param keyLength 长度
     * @return 返回实例
     */
    public AesKeyLengthEnum setKeyLength(Integer keyLength) {
        this.keyLength = keyLength;
        return this;
    }
}
