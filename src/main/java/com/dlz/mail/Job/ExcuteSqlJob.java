package com.dlz.mail.Job;

import com.dlz.mail.Test;
import com.dlz.mail.bean.MailTaskBean;
import com.dlz.mail.db.DBUtil;
import com.dlz.mail.queue.TaskQueue;
import com.dlz.mail.task.ExecuteSQL;
import com.dlz.mail.utils.Constant;
import com.dlz.mail.utils.TextUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.LogManager;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.SQLException;
import java.util.List;

//执行sql查询的job
public class ExcuteSqlJob implements Job {
    private static final org.apache.log4j.Logger logger = LogManager.getLogger(ExecuteSQL.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String emailTaskId = dataMap.getString(Constant.Key.EMAIL_TASK_ID);
        String jobName = dataMap.getString(Constant.Key.TASK_NAME);
        logger.debug("开始执行定时任务：" + jobName);
        if (TextUtil.isEmpty(emailTaskId)) {
            logger.debug("id为空，放弃定时任务的执行");
            return;
        }

        //开始查询对应的任务并执行
        Test.executorService.submit(new ExecuteSQL(emailTaskId));

    }
}
