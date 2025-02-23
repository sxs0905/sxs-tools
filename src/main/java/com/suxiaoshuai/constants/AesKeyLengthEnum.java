package com.suxiaoshuai.constants;

public enum AesKeyLengthEnum {
    L_128(128),

    L_192(192),

    L_256(256),
    ;

    private AesKeyLengthEnum(int keyLength) {
        this.keyLength = keyLength;
    }

    private Integer keyLength;

    public Integer getKeyLength() {
        return keyLength;
    }

    public AesKeyLengthEnum setKeyLength(Integer keyLength) {
        this.keyLength = keyLength;
        return this;
    }
}
