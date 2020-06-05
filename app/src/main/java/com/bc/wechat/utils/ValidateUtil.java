package com.bc.wechat.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * @author zhou
 */
public class ValidateUtil {


    /**
     * 中国手机号码
     */
    private static Pattern CHINESE_PHONE_PATTERN = Pattern.compile("((13|15|17|18)\\d{9})|(14[57]\\d{8})");


    /**
     * 是否是有效的中国手机号码
     *
     * @param phone 手机号
     * @return true:有效 false:无效
     */
    public static boolean isValidChinesePhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return false;
        }

        Matcher matcher = CHINESE_PHONE_PATTERN.matcher(phone);
        return matcher.matches();
    }
}
