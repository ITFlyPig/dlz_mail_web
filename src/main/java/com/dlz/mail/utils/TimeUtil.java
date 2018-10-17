package com.dlz.mail.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /*
   * 将时间戳转换为时间
   */
    public static String stampToDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH mm ss");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前的时间
     * @return
     */
    public static String getCurTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}
