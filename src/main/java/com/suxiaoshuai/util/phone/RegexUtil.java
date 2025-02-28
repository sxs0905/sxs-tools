package com.suxiaoshuai.util.phone;


import com.suxiaoshuai.util.string.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 */
public class RegexUtil {
    /**
     * 是否匹配
     *
     * @param context 数据
     * @param regex   匹配规则
     * @return 匹配结果
     */
    public static boolean match(String context, String regex) {
        if (StringUtil.isBlank(context) || StringUtil.isBlank(regex)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(context);
        return matcher.matches();
    }

    /**
     * 找到符合规则的数据
     *
     * @param context 数据
     * @param portion 分组位置
     * @param regex   匹配规则
     * @return 具体结果
     */
    public static String getRegexPortion(String context, int portion, String regex) {
        if (StringUtil.isBlank(context) || StringUtil.isBlank(regex)) {
            return "";
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(context);
        return matcher.matches() ? (StringUtil.isBlank(matcher.group(portion)) ? "" : matcher.group(portion)) : "";
    }
}
