package com.dlz.mail.db;

import com.dlz.mail.bean.MailTaskBean;
import com.dlz.mail.utils.Log;
import com.dlz.mail.utils.TextUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class CommonUtil {
    private static final Logger logger = LogManager.getLogger(CommonUtil.class);
    /**
     * 是否应该放弃当前的任务
     * 放弃的情况：
     * 1.任务已被更新----------对比当前的updatetime是否和数据库的一样，且重要的字段已更新
     *
     * @param bean
     * @return
     */
    public static boolean isShouldAbandonTask(MailTaskBean bean) {
        if (bean == null) {
            return false;
        }
        try {
            ComboPooledDataSource dataSource = DBUtil.getDataSource();
            QueryRunner queryRunner = new QueryRunner(dataSource);
            List<MailTaskBean> tasks = queryRunner.query("select * from mail where id = ?", new BeanListHandler<MailTaskBean>(MailTaskBean.class), bean.getId());
            if (tasks != null){
                for (MailTaskBean  mailTaskBean : tasks){
                    if (!timeEquals(mailTaskBean.getUpdate_time(), bean.getUpdate_time())){//更新的时间不相等在判断需要的字段值是否相等
                        if (!strEquals(mailTaskBean.getSql(), bean.getSql())){
                            logger.debug("sql语句不一样");
                            return true;
                        }

                        if (!timeEquals(mailTaskBean.getEnd_time(), bean.getEnd_time())){
                            logger.debug("endtime不一样");
                            return true;
                        }
                        if (!strEquals(mailTaskBean.getSend_time(), bean.getSend_time())){
                            logger.debug("发送时间不一样");
                            return true;
                        }
                        if (!strEquals(mailTaskBean.getTask_name(), bean.getTask_name())){
                            logger.debug("task_name不一样");
                            return true;
                        }
                        if (!strEquals(mailTaskBean.getManagerEmail(), bean.getManagerEmail())){
                            logger.debug("管理者邮件不一样");
                            return true;
                        }
                        if (!strEquals(mailTaskBean.getSubject(), bean.getSubject())){
                            logger.debug("邮件主题不一样");
                            return true;
                        }
                        if (!strEquals(mailTaskBean.getCopy_to_mails(), bean.getCopy_to_mails())){
                            logger.debug("抄送者不一样");
                            return true;
                        }
                        if (!strEquals(mailTaskBean.getReceptions(), bean.getReceptions())){
                            logger.debug("接受者邮件不一样");
                            return true;
                        }


                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 判断两个字符串是否相等
     * @param str1
     * @param str2
     * @return
     */
    private static boolean strEquals(String str1, String str2){
        boolean result = false;
        if (str1 == null && str2 == null){
            result = true;
        }else if (str1 != null && str2 == null){
            result = false;
        }else if (str1 == null && str2 != null){
            result = false;
        }else  if (str1.equalsIgnoreCase(str2)){
            result = true;
        }

        return result;
    }


    private static boolean timeEquals(Timestamp time1, Timestamp time2){
        boolean result = false;
        if (time1 == null && time2 == null){
            result = true;
        }else if (time1 != null && time2 == null){
            result = false;
        }else if (time1 == null && time2 != null){
            result = false;
        }else if (time2.compareTo(time1) == 0){
            result = true;
        }
        return result;
    }

}
