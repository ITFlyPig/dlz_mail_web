package com.dlz.mail.timer;

import com.dlz.mail.task.MonitorTask;
import com.dlz.mail.utils.Log;
import com.dlz.mail.utils.TextUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 任务定时器 用于邮件发送和sql执行（sql的执行时间可以自定义也可以系统决定）
 */
public class CronTriggerUtil {

    private static final Logger logger = LoggerFactory.getLogger(CronTriggerUtil.class);
    /**
     * @param job
     * @param cronExpre
     * @return
     * @throws SchedulerException
     */
    public static boolean addTask(Class<? extends Job> job, String cronExpre, String jobName, String triggerName) throws SchedulerException {
        if (job == null) {
            return false;
        }

        if (!isValidExpression(cronExpre)){
            return false;
        }

        if (TextUtil.isEmpty(jobName)) {
            jobName = "job_" + String.valueOf(System.currentTimeMillis());
        }
        if (TextUtil.isEmpty(triggerName)) {
            triggerName = "trigger_" + String.valueOf(System.currentTimeMillis());
        }


        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail jobDetail = JobBuilder.newJob(job).withIdentity(jobName, "jGroup1").build();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("num", "123");

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpre);
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName, "tGroup1")
                .withSchedule(cronScheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
        scheduler.start();
        return true;
    }

    /**
     * 检查cron表达式是否符合规范
     * @param cronExpression
     * @return
     */
    public static boolean isValidExpression(final String cronExpression){
        logger.debug("开始检查cron表达式是否合法");
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(cronExpression);
            Date date = trigger.computeFirstFireTime(null);
            boolean isOK = date != null;
            if (isOK){
                logger.debug("cron表达式合法");
            }else {
                logger.debug("cron表达式不合法");
            }
            return isOK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("cron表达式不符合规范" );
        return false;
    }

}
