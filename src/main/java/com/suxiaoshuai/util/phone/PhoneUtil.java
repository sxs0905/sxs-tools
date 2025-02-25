package com.suxiaoshuai.util.phone;


import com.suxiaoshuai.util.string.StringUtil;

/**
 * 电话号码工具类，用于解析和处理中国大陆地区的电话号码。
 * 支持以下格式：
 * <ul>
 *     <li>固定电话（支持区号）</li>
 *     <li>手机号码</li>
 *     <li>400电话</li>
 * </ul>
 */
public class PhoneUtil {
    /** 正则表达式起始标记 */
    public static final String REGEX_BEGIN = "^";
    
    /** 正则表达式结束标记 */
    public static final String REGEX_END = "$";

    /** 中国大陆固定电话正则表达式（含国际区号） */
    public static final String REGEX_FIXED_PHONE_86 = "((\\+?86-?)?((852|853|(0?(10|21|22|23)))|(0?[1-9]\\d{2})))?(-?)(\\d{7,8})([,，、]\\d{7,8})?(-\\d+)?";

    /** 中国大陆手机号正则表达式（含国际区号） */
    public static final String REGEX_MOBILE_PHONE_86 = "(\\+?86-?)?(1[3-9]\\d{9})";
    
    /** 中国大陆400电话正则表达式（含国际区号） */
    public static final String REGEX_400_86 = "(\\+?86-?)?((400)(-)?(\\d{3,4})(-)?(\\d{3,4})(-\\d{1,6})?)";
    
    /** 中国大陆固定电话正则表达式 */
    public static final String REGEX_FIXED_PHONE = "((852|853|(0?(10|21|22|23)))|(0?[1-9]\\d{2}))?(-?)(\\d{7,8})([,，、]\\d{7,8})?(-\\d+)?";
    
    /** 中国大陆手机号正则表达式 */
    public static final String REGEX_MOBILE_PHONE = "1[3-9]\\d{9}";
    
    /** 中国大陆400电话正则表达式 */
    public static final String REGEX_400 = "(400)(-)?(\\d{3,4})(-)?(\\d{3,4})(-\\d{1,6})?";

    /** 组合正则表达式，支持所有电话号码格式 */
    public static final String REGEX = "(\\+?86-?)?((" + REGEX_400 + ")|(" + REGEX_MOBILE_PHONE + ")|(" + REGEX_FIXED_PHONE + "))(;(\\+?86-?)?((" + REGEX_400 + ")|(" + REGEX_MOBILE_PHONE + ")|(" + REGEX_FIXED_PHONE + ")))?";

    /**
     * 解析电话号码字符串，将其转换为标准格式。
     * <p>
     * 支持的格式包括：
     * <ul>
     *     <li>固定电话：区号-号码，如 010-12345678</li>
     *     <li>手机号码：11位数字，如 13812345678</li>
     *     <li>400电话：如 400-123-4567</li>
     * </ul>
     *
     * @param tel 需要解析的电话号码字符串
     * @return 标准格式的电话号码，如果输入为空或格式不正确则返回空字符串
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
