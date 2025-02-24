package com.suxiaoshuai.util.phone;


import com.suxiaoshuai.util.string.StringUtil;

public class PhoneUtil {

    public static final String REGEX_BEGIN = "^";
    public static final String REGEX_END = "$";

    public static final String REGEX_FIXED_PHONE_86 = "((\\+?86-?)?((852|853|(0?(10|21|22|23)))|(0?[1-9]\\d{2})))?(-?)(\\d{7,8})([,，、]\\d{7,8})?(-\\d+)?";

    public static final String REGEX_MOBILE_PHONE_86 = "(\\+?86-?)?(1[3-9]\\d{9})";
    public static final String REGEX_400_86 = "(\\+?86-?)?((400)(-)?(\\d{3,4})(-)?(\\d{3,4})(-\\d{1,6})?)";
    public static final String REGEX_FIXED_PHONE = "((852|853|(0?(10|21|22|23)))|(0?[1-9]\\d{2}))?(-?)(\\d{7,8})([,，、]\\d{7,8})?(-\\d+)?";
    public static final String REGEX_MOBILE_PHONE = "1[3-9]\\d{9}";
    public static final String REGEX_400 = "(400)(-)?(\\d{3,4})(-)?(\\d{3,4})(-\\d{1,6})?";

    public static final String REGEX = "(\\+?86-?)?((" + REGEX_400 + ")|(" + REGEX_MOBILE_PHONE + ")|(" + REGEX_FIXED_PHONE + "))(;(\\+?86-?)?((" + REGEX_400 + ")|(" + REGEX_MOBILE_PHONE + ")|(" + REGEX_FIXED_PHONE + ")))?";

    /**
     * 解析手机号
     *
     * @param tel 手机号
     * @return 手机号
     */
    public static String parse(String tel) {

        if (StringUtil.isBlank(tel)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        String regexAll = REGEX_BEGIN + REGEX + REGEX_END;
        if (RegexUtil.match(tel, regexAll)) {
            String countryCode = RegexUtil.getRegexPortion(tel, 1, regexAll);
            if (StringUtil.isNotBlank(countryCode)) {
                if (countryCode.endsWith("-")) {
                    sb.append(countryCode);
                } else {
                    sb.append(countryCode).append("-");
                }
            }

            String contactInfo = RegexUtil.getRegexPortion(tel, 3, regexAll);
            if (StringUtil.isBlank(contactInfo)) {
                contactInfo = RegexUtil.getRegexPortion(tel, 10, regexAll);
            }
            sb.append(contactInfo);
            if (StringUtil.isBlank(contactInfo)) {
                String areaCode = RegexUtil.getRegexPortion(tel, 12, regexAll);
                String fixedPhoneNumber = RegexUtil.getRegexPortion(tel, 18, regexAll);
                String fixedPhoneSub = RegexUtil.getRegexPortion(tel, 20, regexAll);
                if (StringUtil.isBlank(areaCode)) {
                    sb.append(fixedPhoneNumber);
                } else {
                    sb.append(areaCode).append("-").append(fixedPhoneNumber);
                }
                if (!StringUtil.isBlank(fixedPhoneSub)) {
                    sb.append(fixedPhoneSub);
                }
            }

        }
        return sb.toString();
    }
}
