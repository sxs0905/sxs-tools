package com.suxiaoshuai.util.amount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;

class AmountUtilTest {

    @Test
    void toUpper() {
        String upper = AmountUtil.toUpper(new BigDecimal("99999999999999999.99"));
        System.out.println(upper);
    }

    @Test
    void toUpperNoEven() {
    }

    @Test
    void toUpperWithEven() {
    }

    @Test
    void toLower() {
    }

    @Test
    void format() {
        BigDecimal bigDecimal = new BigDecimal("99999999999999999.921");
        String amount = AmountUtil.format(bigDecimal);
        System.out.println(amount);
    }

    @Test
    void testFormat() {
        BigDecimal bigDecimal = new BigDecimal("1000000.1");
        String amount = AmountUtil.format(bigDecimal, 2);
        System.out.println(amount);
    }
}