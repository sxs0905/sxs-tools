package com.suxiaoshuai.util.string;

import com.suxiaoshuai.exception.SxsToolsException;
import com.suxiaoshuai.util.ValidateUtil;

/**
 * 身份证号码工具类
 */
public class IdCardUtil {

    /**
     * 性别常量：女性
     */
    public static final int SEX_FEMALE = 0;

    /**
     * 性别常量：男性
     */
    public static final int SEX_MALE = 1;

    /**
     * 格式化身份证号码，保留前三位和后四位，中间用星号代替
     * 
     * @param idCard 身份证号码
     * @return 格式化后的身份证号码，例如：450***********1779
     */
    public static String formatIdCard(String idCard) {
        if (StringUtil.isBlank(idCard) || StringUtil.length(idCard) < 15) {
            return idCard;
        }
        String formatStr = "***********";
        if (StringUtil.length(idCard) < 18) {
            formatStr = "********";
        }
        return StringUtil.substring(idCard, 0, 3) + formatStr + StringUtil.substring(idCard, StringUtil.length(idCard) - 4);
    }

    /**
     * 根据身份证号码获取性别
     * 
     * @param idCardNo 身份证号码
     * @return 性别代码，1-男性，0-女性
     * @throws SxsToolsException 当身份证号为空或格式不正确时抛出异常
     */
    public static Integer getSex(String idCardNo) {
        if (StringUtil.isBlank(idCardNo)) {
            throw new SxsToolsException("身份证号为空");
        }
        if (!ValidateUtil.isIDNo(idCardNo)) {
            throw new SxsToolsException("身份证号不正确[" + idCardNo + "]");
        }
        String sexFlagNum;
        if (idCardNo.length() == 15) {
            sexFlagNum = idCardNo.substring(14, 15);
        } else {
            sexFlagNum = idCardNo.substring(16, 17);
        }
        return Integer.parseInt(sexFlagNum) % 2 != 0 ? SEX_MALE : SEX_FEMALE;
    }
}
