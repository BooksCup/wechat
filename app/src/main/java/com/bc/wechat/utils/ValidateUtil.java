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

    /**
     * 密码规则校验
     * 规则: 密码必须是8-16位的数字、字符组合(不能是纯数字)
     *
     * @param password 密码
     * @return true: 校验成功  false: 校验失败
     */
    public static boolean validatePassword(String password) {
        String regEx = "^(?![^a-zA-Z]+$)(?!\\D+$).{8,16}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
