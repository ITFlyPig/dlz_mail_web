package com.dlz.mail.queue;

import com.dlz.mail.Job.ExcuteSqlJob;
import com.dlz.mail.bean.MailTaskBean;
import com.dlz.mail.db.CommonUtil;
import com.dlz.mail.db.DBUtil;
import com.dlz.mail.timer.QuartzManager;
import com.dlz.mail.utils.Constant;
import com.dlz.mail.utils.TextUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TaskQueue {
    private static final Logger logger = LogManager.getLogger(TaskQueue.class);

    private BlockingQueue<MailTaskBean> sqlQueue;//sql执行的队列

    private BlockingQueue<MailTaskBean> sendMailQueue;//邮件发送的队列

    public TaskQueue() {
        sqlQueue = new ArrayBlockingQueue<MailTaskBean>(30);
        sendMailQueue = new ArrayBlockingQueue<MailTaskBean>(30);
    }

    public BlockingQueue<MailTaskBean> getSqlQueue() {
        return sqlQueue;
    }

    public void setSqlQueue(BlockingQueue<MailTaskBean> sqlQueue) {
        this.sqlQueue = sqlQueue;
    }

    public BlockingQueue<MailTaskBean> getSendMailQueue() {
        return sendMailQueue;
    }

    public void setSendMailQueue(BlockingQueue<MailTaskBean> sendMailQueue) {
        this.sendMailQueue = sendMailQueue;
    }

    /**
     * 将sql查询任务添加到队列
     * @param tasks
     */
    public synchronized void addSqlTask(List<MailTaskBean> tasks) {
        if (tasks == null || tasks.size() == 0) {
            logger.debug("要添加的任务列表为空，线程进入阻塞");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.debug("要添加的任务列表为空，线程进入阻塞  阻塞异常：" + e.getLocalizedMessage());
            }
            return;
        }

        logger.debug("查询到的任务数：" + tasks.size());

        try {
            int size = tasks.size();
            for (int i = 0; i < size; i++) {
                MailTaskBean bean = tasks.get(i);

                //判断任务是否应该丢弃
                boolean isAbandon = CommonUtil.isShouldAbandonTask(bean);
                if (isAbandon){
                    logger.debug("任务不合法，丢弃");
                    continue;
                }

                //excuteTime为空表示立即执行
                if (bean.getExcuteTime() == null || bean.getExcuteTime().getTime() <= System.currentTimeMillis()) {
                    sqlQueue.put(bean);
                    DBUtil.update("update mail set status = ? where id = ?", Constant.EmailStatus.EXECUTE_ING, bean.getId());
                    logger.debug("任务：" + bean.getTask_name() + " 立即执行");
                } else {
                    QuartzManager.addJob(bean.getTask_name() + "sql查询", String.valueOf(bean.getId()), "excute_sql" + String.valueOf(bean.getId()), ExcuteSqlJob.class, bean.generateExecutSQlCron(), this);
                    logger.debug("任务：" + bean.getTask_name() + " 暂时不执行，将其定时");
                    DBUtil.update("update mail set status = ? where id = ?", Constant.EmailStatus.SQL_EXCUTE_TIMER, bean.getId());

                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始从数据库查询sql任务
     */
    public synchronized void startGetSQlTasks(){

        notifyAll();
    }

    /**
     * 移除和任务有关的所有的定时器
     * @param tasks
     */
    public void removeOldTimers( List<MailTaskBean> tasks){
        if (tasks == null || tasks.size() == 0){
            return;
        }
        for (MailTaskBean mailTaskBean:tasks){
            if (TextUtil.isEmpty(mailTaskBean.getTask_name())){
                logger.debug(mailTaskBean.getId() + "任务名称为空，跳过删除定时器的步骤");
                continue;
            }
            logger.debug("删除定时器：" + mailTaskBean.getTask_name());
            QuartzManager.removeJob(mailTaskBean.getTask_name() + "邮件发送", "send_email" + mailTaskBean.getId());
            QuartzManager.removeJob(mailTaskBean.getTask_name() + "sql查询", "excute_sql" + mailTaskBean.getId());

        }

    }

    public synchronized void waitTask(){
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
