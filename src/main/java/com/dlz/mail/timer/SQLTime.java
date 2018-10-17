package com.dlz.mail.timer;

/**
 * 表示是否开始执行SQL
 */
public class SQLTime {

    private static  long AHEAD_OF_SCHEDULE = 60 * 60 * 1000;//默认提前一小时1小时

    /**
     * 是否应该执行sql
     * @param sendTime 邮件的发送时间
     * @param endTime 邮件的结束时间
     * @param mailNum 邮件的数量
     * @param mins 分钟为单位 每个sql执行花费的时间，用来预估需要提前大约多久执行
     * @return
     */
    public static boolean shouldExecuteSQL(long sendTime, long endTime, int mailNum, int mins){
        if (mailNum > 0 && mins > 0){
            AHEAD_OF_SCHEDULE = mailNum * mins * 60 * 1000;
        }

        long curTime = System.currentTimeMillis();
        if (sendTime > curTime && sendTime - curTime < AHEAD_OF_SCHEDULE){//属于提前时间内，开始执行sql
            return true;
        }

        if (curTime >= sendTime && curTime < endTime ){//已经应该发送邮件，但是还没有执行完成，也没到邮件的结束时间
            return true;
        }

        return false;
    }

    /**
     * 邮件是否应该开始执行
     * @param sendTime
     * @param endTime
     * @return
     */
    public static boolean shouldExecuteSQL(long sendTime, long endTime){
        return shouldExecuteSQL(sendTime, endTime, 0 ,0);
    }



}
