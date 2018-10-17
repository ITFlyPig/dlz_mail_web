package com.dlz.mail.utils;

public class StringUtil {

    /**
     * 将字符转为数字
     * @param num
     * @return
     */
    public static int parseToInt(String num) {
        int result = -1;
        if (TextUtil.isEmpty(num)) {
            return result;
        }

        try {

            result = Integer.valueOf(num);
            return result;

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return result;

    }


}
