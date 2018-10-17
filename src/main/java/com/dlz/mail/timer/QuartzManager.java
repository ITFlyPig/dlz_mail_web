package com.dlz.mail.timer;

import com.dlz.mail.utils.TextUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Calendar;

import static com.dlz.mail.utils.Constant.Key.TASK_NAME;

/**
 * 定时任务的管理类
 */
public class QuartzManager {
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";
    private static String TRIGGER_GROUP_NAME = "EXTJWEB_TRIGGERGROUP_NAME";

    /**
     * @Description: 添加一个定时任务
     *
     * @param jobName 任务名
     * @param triggerName 触发器名
     * @param jobClass  任务
     * @param cron   时间设置，参考quartz说明文档
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void addJob(String jobName, String emailTaskId,  String triggerName, Class jobClass, String cron, Object obj) {
        try {
            removeJob(jobName, triggerName);


            Scheduler sched = schedulerFactory.getScheduler();
            sched.deleteJob(JobKey.jobKey(jobName, JOB_GROUP_NAME));// 删除任务,相同的任务只需要定时一次
            // 任务名，任务组，任务执行类
            JobDetail jobDetail= JobBuilder.newJob(jobClass).withIdentity(jobName, JOB_GROUP_NAME).build();

            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.put("email_task_id", emailTaskId);
            jobDataMap.put("jobName", jobName);
            jobDataMap.put(TASK_NAME, jobName);
            if (obj != null){
                jobDataMap.put("obj", obj);
            }

            // 触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, TRIGGER_GROUP_NAME);
            triggerBuilder.startNow();
            // 触发器时间设定
            //将yyyy-mm-dd hh:mm:ss 的时间格式转换为quartz_CronExpression
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();

            // 调度容器设置JobDetail和Trigger
            sched.scheduleJob(jobDetail, trigger);

            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * @Description: 修改一个任务的触发时间
     *
     * @param jobName
     * @param triggerName 触发器名
     * @param cron   时间设置，参考quartz说明文档
     */
    public static void modifyJobTime(String jobName, String triggerName, String cron) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, TRIGGER_GROUP_NAME);
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }

            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                /** 方式一 ：调用 rescheduleJob 开始 */
                // 触发器
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组
                triggerBuilder.withIdentity(triggerName, TRIGGER_GROUP_NAME);
                triggerBuilder.startNow();
                // 触发器时间设定
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 方式一 ：修改一个任务的触发时间
                sched.rescheduleJob(triggerKey, trigger);
                /** 方式一 ：调用 rescheduleJob 结束 */

                /** 方式二：先删除，然后在创建一个新的Job  */
                //JobDetail jobDetail = sched.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
                //Class<? extends Job> jobClass = jobDetail.getJobClass();
                //removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
                //addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cron);
                /** 方式二 ：先删除，然后在创建一个新的Job */
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 移除一个任务
     *
     * @param jobName
     * @param triggerName
     */
    public static void removeJob(String jobName, String triggerName) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();

            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, TRIGGER_GROUP_NAME);

            sched.pauseTrigger(triggerKey);// 停止触发器
            sched.unscheduleJob(triggerKey);// 移除触发器
            sched.deleteJob(JobKey.jobKey(jobName, JOB_GROUP_NAME));// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:启动所有定时任务
     */
    public static void startJobs() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:关闭所有定时任务
     */
    public static void shutdownJobs() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
