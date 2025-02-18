package com.suxiaoshuai;

import com.suxiaoshuai.util.amount.AmountUtil;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        // System.out.println(AmountUtil.toUpper(new BigDecimal("10000000400000")));
        System.out.println(AmountUtil.toUpper(new BigDecimal("99999999999999999.0")));
    }
}