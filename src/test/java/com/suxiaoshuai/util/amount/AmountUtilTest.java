package com.suxiaoshuai.util.amount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
        String amount = "玖亿亿玖仟玖佰玖拾玖万亿玖仟玖佰玖拾玖亿玖仟玖佰玖拾玖万玖仟玖佰玖拾玖元玖角玖分";
        BigDecimal lower = AmountUtil.toLower(amount);
        System.out.println(lower);
    }

    @Test
    void format() {
        BigDecimal bigDecimal = new BigDecimal("99999999999999999.921");
        String amount = AmountUtil.format(bigDecimal);
        System.out.println(amount);
    }

    @Test
    void testFormat() {
        BigDecimal bigDecimal = new BigDecimal("99999999999999999.99");
        String amount = AmountUtil.format(bigDecimal, 2);
        System.out.println(amount);
    }
}