package com.suxiaoshuai.util.string;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 随机字符串工具类
 * 提供生成随机字符串的功能，支持多种模式和长度。
 */
public class RandomUtil {
    // 字符集定义
    private static final String LOWER_CASE = "abcdefghijkmnopqrstuvwxyz"; // 排除小写l
    private static final String UPPER_CASE = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // 排除大写I
    private static final String DIGITS = "23456789"; // 排除0和1

    private static final SecureRandom RANDOM = new SecureRandom();

    // 预定义模式枚举
    public enum Pattern {
        LOWER_CASE(Collections.singletonList(CharType.LOWER)),
        UPPER_CASE(Collections.singletonList(CharType.UPPER)),
        DIGITS(Collections.singletonList(CharType.DIGIT)),
        LOWER_UPPER(Arrays.asList(CharType.LOWER, CharType.UPPER)),
        LOWER_DIGIT(Arrays.asList(CharType.LOWER, CharType.DIGIT)),
        UPPER_DIGIT(Arrays.asList(CharType.UPPER, CharType.DIGIT)),
        ALL(Arrays.asList(CharType.LOWER, CharType.UPPER, CharType.DIGIT));

        private final List<CharType> charTypes;

        Pattern(List<CharType> charTypes) {
            this.charTypes = charTypes;
        }

        private List<CharType> getCharTypes() {
            return charTypes;
        }
    }

    // 定义字符类型
    private enum CharType {
        LOWER, UPPER, DIGIT
    }


    /**
     * 生成指定长度的随机字符串，默认使用包含所有三种字符类型的模式
     */
    public static String generate(int length) {
        return generate(length, Pattern.ALL);
    }

    /**
     * 生成指定长度和模式的随机字符串
     */
    public static String generate(int length, Pattern pattern) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("字符模式不能为空");
        }

        List<CharType> charTypes = pattern.getCharTypes();
        StringBuilder sb = new StringBuilder(length);
        List<Character> chars = new ArrayList<>(length);

        // 确保每种字符类型至少出现一次
        for (CharType type : charTypes) {
            if (chars.size() < length) {
                chars.add(getRandomCharForType(type));
            }
        }

        // 填充剩余字符
        for (int i = chars.size(); i < length; i++) {
            CharType randomType = charTypes.get(RANDOM.nextInt(charTypes.size()));
            chars.add(getRandomCharForType(randomType));
        }

        // 打乱字符顺序
        Collections.shuffle(chars, RANDOM);

        // 构建最终字符串
        for (char c : chars) {
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * 根据字符类型获取随机字符
     */
    private static char getRandomCharForType(CharType type) {
        return switch (type) {
            case LOWER -> getRandomChar(LOWER_CASE);
            case UPPER -> getRandomChar(UPPER_CASE);
            case DIGIT -> getRandomChar(DIGITS);
        };
    }

    private static char getRandomChar(String chars) {
        int index = RANDOM.nextInt(chars.length());
        return chars.charAt(index);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 8; i++) {
            System.out.println(RandomUtil.generate(16, Pattern.UPPER_DIGIT));
        }
    }
}
