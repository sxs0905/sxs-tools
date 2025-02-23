package com.suxiaoshuai.util;

import com.suxiaoshuai.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author sxs
 */
public class ValidateUtil {

    private static final Logger logger = LoggerFactory.getLogger(ValidateUtil.class);

    private static final String REGEX_MOBILE0 = "((\\+86|0086)?\\s*)(1\\d{10}|(^\\d{3,4}-\\d{7,8}$))";

    private static final String REGEX_ID_NO = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";

    private static final String REGEX_PLATE_NO = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{1}(([A-HJ-Z]{1}[A-HJ-NP-Z0-9]{5})|([A-HJ-Z]{1}(([DF]{1}[A-HJ-NP-Z0-9]{1}[0-9]{4})|([0-9]{5}[DF]{1})))|([A-HJ-Z]{1}[A-D0-9]{1}[0-9]{3}警)))|([0-9]{6}使)|((([沪粤川云桂鄂陕蒙藏黑辽渝]{1}A)|鲁B|闽D|蒙E|蒙H)[0-9]{4}领)|(WJ[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼·•]{1}[0-9]{4}[TDSHBXJ0-9]{1})|([VKHBSLJNGCE]{1}[A-DJ-PR-TVY]{1}[0-9]{5})";
    private static final String REGEX_VEHICLE_LICENSE_NO = "^[0-9A-Za-z]{1,50}$";
    private static final String NUMBER = "(^[1-9]([0-9]{1,9})?(\\.[0-9]{1,4})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9]{1,3})?$)";
    private static final String REGEX_TRANSPORT_CERTIFICATE_NO = "^[0-9A-Za-z]{1,50}$";
    public static final String REGEX_PERSON_NAME = "^[\\u4e00-\\u9fa50-9A-Za-z\\.\\(\\)]{1,50}$";
    public static final String REGEX_BANK_BRANCH_INFO = "^[\\u4e00-\\u9fa50-9A-Za-z\\(\\)\\（\\）]{1,50}$";
    public static final String REGEX_BANK_ACCOUNT_NO = "^\\d{16,23}$";
    public static final String REGEX_NUM_CHAR_COMMON = "^[\\u4e00-\\u9fa50-9A-Za-z]{1,50}$";
    public static final String REGEX_IMAGE_CODE = "[0-9A-Za-z]{4,6}";
    public static final String REGEX_SMS_CODE = "\\d{4,6}";
    public static final String REGEX_PERMISSION_VALUE = "^[a-zA-Z]+([a-zA-Z]+\\.){0,10}[a-zA-Z]+$";
    public static final String REGEX_CONTENT = "^[,，、。()\\.?!0-9a-zA-Z\\u4e00-\\u9fa5]{1,200}$";
    public static final String REGEX_CAR_SIZE = "([1-9]\\d{3}[×Xx]){2}\\d{3,4}(mm|MM)";
    public static final String REGEX_TEL = "^((1[3456789]\\d{9})|(\\d{3,4}-\\d{7,8}))";

    private static final String BASE_CODE_STRING = "0123456789ABCDEFGHJKLMNPQRTUWXY";
    private static final char[] BASE_CODE_ARRAY = BASE_CODE_STRING.toCharArray();
    private static final List<Character> BASE_CODES = new ArrayList<>();
    private static final String BASE_CODE_REGEX = "[" + BASE_CODE_STRING + "]{18}";
    private static final int[] WEIGHT = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};

    static {
        for (char c : BASE_CODE_ARRAY) {
            BASE_CODES.add(c);
        }
    }

    public static boolean verify(String str, String regex) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        return Pattern.matches(regex, str);
    }

    /**
     * 判断是否是手机号
     *
     * @param tel 手机号
     * @return boolean true:是 false:否
     */
    public static boolean mobile(String tel) {
        return verify(tel, REGEX_MOBILE0);
    }

    /**
     * 判断是否是身份证号
     *
     */
    public static boolean isIDNo(String idCardNo) {
        boolean matches = verify(idCardNo, REGEX_ID_NO);

        if (!matches) {
            return false;
        }
        // 判断第18位校验值
        if (idCardNo.length() != 18) {
            return false;
        }
        try {
            char[] charArray = idCardNo.toCharArray();
            // 前十七位加权因子
            int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
            // 这是除以11后，可能产生的11位余数对应的验证码
            String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
            int sum = 0;
            for (int i = 0; i < idCardWi.length; i++) {
                int current = Integer.parseInt(String.valueOf(charArray[i]));
                int count = current * idCardWi[i];
                sum += count;
            }
            char idCardLast = charArray[17];
            int idCardMod = sum % 11;
            return idCardY[idCardMod].equalsIgnoreCase(String.valueOf(idCardLast));
        } catch (Exception e) {
            logger.error("validate id card no:{} error", idCardNo, e);
            return false;
        }
    }

    public static boolean idCardNo(String idCardNo) {
        return verify(idCardNo, REGEX_ID_NO);
    }

    /**
     * 判断是否是车牌号
     */
    public static boolean carLicenseNo(String content) {
        return verify(content, REGEX_PLATE_NO);
    }

    /**
     * 判断是否是车辆识别代号
     */
    public static boolean isVehicleLicenseNo(String content) {
        return verify(content, REGEX_VEHICLE_LICENSE_NO);
    }

    /**
     * 判断是否是道路运输证号
     */
    public static boolean isTransportCertificateNo(String content) {
        return verify(content, REGEX_TRANSPORT_CERTIFICATE_NO);
    }

    /**
     * 是否是有效的统一社会信用代码
     */
    public static boolean isValidSocialCreditCode(String socialCreditCode) {
        if (StringUtil.isBlank(socialCreditCode) || !Pattern.matches(BASE_CODE_REGEX, socialCreditCode)) {
            return false;
        }
        char[] businessCodeArray = socialCreditCode.toCharArray();
        char check = businessCodeArray[17];
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char key = businessCodeArray[i];
            sum += (BASE_CODES.indexOf(key) * WEIGHT[i]);
        }
        int value = 31 - sum % 31;
        return check == BASE_CODE_ARRAY[value % 31];
    }
}