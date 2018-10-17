package com.dlz.mail.Job;

import com.dlz.mail.bean.MailTaskBean;
import com.dlz.mail.db.CSVResultHandler;
import com.dlz.mail.db.CommonUtil;
import com.dlz.mail.db.DBUtil;
import com.dlz.mail.utils.Constant;
import com.dlz.mail.utils.EmailUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * 邮件定时发送的任务
 */
public class EmailJob implements Job {
    private static final Logger logger = LogManager.getLogger(CSVResultHandler.class);


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String emailTaskId = dataMap.getString("email_task_id");
        String jobName = dataMap.getString("jobName");
        logger.debug("开始执行发送邮件的定时任务：" + jobName);
        //据id查询对应的邮件发送任务
        ComboPooledDataSource dataSource = DBUtil.getDataSource();
        QueryRunner queryRunner = new QueryRunner(dataSource);
        MailTaskBean task = null;

        try {
            task = queryRunner.query(Constant.SQL.GET_TASKS, new BeanHandler<MailTaskBean>(MailTaskBean.class), emailTaskId);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("定时任务失败，任务id为----" +  emailTaskId + " \n" + e.getLocalizedMessage());
        }
        if (task == null){
            logger.error("定时任务失败，任务id为----" +  emailTaskId + " 查询数据库对应的任务为空");
            return;
        }
        sendMail(task);



    }

    /**
     * 发送邮件
     * @param task
     */
    private void sendMail(MailTaskBean task){
        logger.debug("sendMail begin");
        if (task == null){
            logger.debug("sendMail 返回，因为任务为空");
            return;
        }

        //判断任务是否应该丢弃
        boolean isAbandon = CommonUtil.isShouldAbandonTask(task);
        if (isAbandon){
            logger.debug("邮件任务有更新，放弃当前的任务");
            return;
        }

        long curTime = System.currentTimeMillis();
        boolean isSend = false;
        Timestamp sendT = null;
        Timestamp endT = task.getEnd_time();
        if (endT == null && sendT != null){
            isSend = true;
        }else if (endT != null && sendT != null){
            long sendTime = 0;
            long endTime = task.getEnd_time().getTime();
            if (curTime >= sendTime &&  curTime < endTime && task.getStatus() == Constant.EmailStatus.WAIT_SEND){
                isSend = true;
            }
        }
        if (isSend){
            ArrayList<String> paths = new ArrayList<>();
            paths.add(task.filePath);
            boolean result = EmailUtil.sendSQLEmail(paths,  task.getSubject(),
                    task.getMailContent(), task.parseReceptions(), task.parseCopyTos(), task.getSql_result_store());
            //对于已发送的邮件，更新状态
            logger.debug("邮件：" + task.getTask_name() + " 发送" + (result ? "成功" : "失败"));
            if (result){
                DBUtil.update(Constant.SQL.UPDATE_TASK_STATUS, Constant.EmailStatus.SEND_SUCCESS, task.getId());
            }else {
                DBUtil.update(Constant.SQL.UPDATE_TASK_STATUS, Constant.EmailStatus.SEND_FAIL, task.getId());
            }


            //将发送的结果告诉管理员
            String tip = "邮件：" + task.getTask_name() + " 发送" + ( result ? "成功 ^_^"  : "失败 ::>_<::");
            EmailUtil.sendMail(task.getManagerEmail(), "邮件发送结果", tip);
        }else {//查询到的邮件不满足发送的条件
            logger.debug("查询到的邮件不满足发送的条件，邮件为：" + task.getTask_name());
        }

    }


}
