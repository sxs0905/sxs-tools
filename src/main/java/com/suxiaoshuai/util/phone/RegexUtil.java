package com.suxiaoshuai.util.phone;


import com.suxiaoshuai.util.string.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static boolean match(String context, String regex) {
        if (StringUtil.isBlank(context) || StringUtil.isBlank(regex)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(context);
        return matcher.matches();
    }

    public static String getRegexPortion(String context, int portion, String regex) {
        if (StringUtil.isBlank(context) || StringUtil.isBlank(regex)) {
            return "";
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(context);
        return matcher.matches() ? (StringUtil.isBlank(matcher.group(portion)) ? "" : matcher.group(portion)) : "";
    }
}
