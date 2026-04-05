package com.suxiaoshuai.util.string;

import java.util.*;

public class SortUtil {
    /**
     * 核心方法：参数按ASCII字典升序排序（返回排序后的有序Map）
     *
     * @param paramMap 原始参数键值对（建议使用HashMap，无排序的原始容器）
     * @return 按ASCII升序排列的有序Map（LinkedHashMap，保证遍历顺序）
     */
    public static Map<String, Object> sortByAscii(Map<String, Object> paramMap) {
        // 1. 判空，避免空指针
        if (Objects.isNull(paramMap) || paramMap.isEmpty()) {
            return new LinkedHashMap<>();
        }

        // 2. 提取并排序键（ASCII字典升序，String自然排序直接生效）
        List<String> sortedKeys = new ArrayList<>(paramMap.keySet());
        Collections.sort(sortedKeys); // 核心排序：ASCII字典升序

        // 3. 封装为有序Map（LinkedHashMap），按排序后的键遍历
        LinkedHashMap<String, Object> sortedParamMap = new LinkedHashMap<>();
        for (String key : sortedKeys) {
            sortedParamMap.put(key, paramMap.get(key));
        }
        return sortedParamMap;
    }
}
