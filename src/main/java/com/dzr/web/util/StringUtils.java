package com.dzr.web.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author wanglei
 * @since 05.17.2017
 */
public final class StringUtils {

    /**
     * 判断是否为空
     *
     * @param str 字符串
     * @return true表示为空，false表示不为空
     */
    public static boolean isNull(String str) {
        return str == null;
    }

    /**
     * 判断是否为空
     *
     * @param str 字符串
     * @return true表示为空，false表示不为空
     */
    public static boolean isEmpty(String str) {
        return null == str || "".equals(str) || "".equals(str.trim()) || str.length() == 0;
    }

    /**
     * 判断是否不为空值
     *
     * @param str 字符串
     * @return true表示不为空值，false表示为空值
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    public static final String PHONE_DETAIL = "^1[3|4|5|7|8][0-9]{9}$";
    public static final String PHONE = "^1[0-9]{10}$";

    /**
     * 验证是手机号码
     *
     * @param inputNo
     * @return
     */
    public static boolean isMobileNo(String inputNo) {
        boolean isChecked = false;
        if (Pattern.matches(PHONE, inputNo)) {
            isChecked = true;
        }
        return isChecked;
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断时间格式 格式必须为“YYYY-MM-dd”
     * 2004-2-30 是无效的
     * 2003-2-29 是无效的
     *
     * @return
     */
    public static boolean isValidDate(String str) {
        //String str = "2007-01-02";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = (Date) formatter.parse(str);
            return str.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String a = "/login/receiveCardPage?telphone=31111111112&cardId=99";
        System.err.println(a.substring(32, 43));
    }

}
