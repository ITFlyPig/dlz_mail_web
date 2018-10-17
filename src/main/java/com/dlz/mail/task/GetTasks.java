package com.dlz.mail.task;

import com.dlz.mail.Job.ExcuteSqlJob;
import com.dlz.mail.bean.MailTaskBean;
import com.dlz.mail.db.DBUtil;
import com.dlz.mail.queue.TaskQueue;
import com.dlz.mail.timer.QuartzManager;
import com.dlz.mail.utils.Constant;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

/**
 * 连接数据库，获得要执行的sql，并转换为相应的任务放入队列中
 * 需要考虑的情况：
 * 1.正常的情况，也就是距离发送邮件时间未到，提前执行sql
 * 2.紧急情况，在发送时间到了和应该提前执行的这段时间里，怎么快速的检测有新的sql任务,使用检测文件的方式来间接检测
 * 如何监听sql任务的添加和删除
 * <p>
 * 查询任务的时机：
 * 1.检测文件被修改
 * 2.到了定时执行的时间（定时执行的时间由用户设置）
 */
public class GetTasks implements Runnable {
    private static final Logger logger = LogManager.getLogger(GetTasks.class);
    private boolean isStop;

    private TaskQueue taskQueue;

    public GetTasks(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void run() {
        while (!isStop) {
            try {
                ComboPooledDataSource dataSource = DBUtil.getDataSource();
                QueryRunner queryRunner = new QueryRunner(dataSource);

                logger.debug("开始查询数据库里的任务");
                //查询新建和更新的任务
                List<MailTaskBean> tasks = queryRunner.query("select * from mail where status = ? or status = ? or status = ?", new BeanListHandler<MailTaskBean>(MailTaskBean.class),
                        Constant.EmailStatus.NEW, Constant.EmailStatus.UPDATED, Constant.EmailStatus.SQL_EXCUTE_TIMER);

                int size = tasks == null ? 0 : tasks.size();
                logger.debug("查询到的任务数：" + size);

                timerTasks(tasks);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            logger.debug("本次查询完毕，休眠");
            taskQueue.waitTask();


        }

    }

    /**
     * 定时任务
     *
     * @param tasks
     */
    private void timerTasks(List<MailTaskBean> tasks) {
        if (tasks == null) {
            return;
        }
        for (MailTaskBean task : tasks) {
            logger.debug("开始对任务定时：" + task.getTask_name());
            //定时之前把以前的删除
            QuartzManager.removeJob(task.getTask_name() + "sql执行", "excute_sql" + String.valueOf(task.getId()));
            QuartzManager.addJob(task.getTask_name() + "sql执行", String.valueOf(task.getId()), "excute_sql" + String.valueOf(task.getId()), ExcuteSqlJob.class, task.getSend_time(), null);
            DBUtil.update("update mail set  status = ?  where id = ?", Constant.EmailStatus.SQL_EXCUTE_TIMER, task.getId());
        }

    }
}
