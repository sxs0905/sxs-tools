package com.suxiaoshuai.util.string;

import com.suxiaoshuai.exception.SxsToolsException;
import com.suxiaoshuai.util.ValidateUtil;

public class IdCardUtil {

    /**
     * 性别 0-女 1-男
     */
    public static final int SEX_FEMALE = 0;
    public static final int SEX_MALE = 1;

    /**
     * 格式化身份证 450***********1779
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
