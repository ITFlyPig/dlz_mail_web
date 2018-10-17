package com.dlz.mail.timer;

import com.dlz.mail.utils.Constant;

/**
 * 邮件相关时间
 */
public class MailTime {

    /**
     * 据时间返回邮件应该如何处理
     * @param sendTime
     * @param endTime
     * @return
     */
    public static int getMailShouldStatus(long sendTime, long endTime){
        long cur = System.currentTimeMillis();
        if (cur < sendTime){
            return Constant.MailShouleStatus.TIMER;
        }else if (cur >= endTime && cur < endTime){
            return Constant.MailShouleStatus.SEND;
        }else {
            return Constant.MailShouleStatus.ABANDON;
        }

    }
}
