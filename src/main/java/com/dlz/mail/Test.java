package com.dlz.mail;

import com.dlz.mail.bean.MailConfBean;
import com.dlz.mail.bean.MailTaskBean;
import com.dlz.mail.db.DBUtil;
import com.dlz.mail.queue.TaskQueue;
import com.dlz.mail.task.ExecuteSQL;
import com.dlz.mail.task.GetTasks;
import com.dlz.mail.task.MonitorTask;
import com.dlz.mail.utils.Constant;
import com.dlz.mail.utils.DesUtil;
import com.dlz.mail.utils.EmailUtil;
import com.dlz.mail.utils.Log;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;

public class Test {
    private static final Logger logger;

    static {
        PropertyConfigurator.configure( System.getProperty("user.dir") + Constant.FileConfig.CONF_DIR +"/log4j.properties");
        logger = LoggerFactory.getLogger(Test.class);

        System.setProperty("mail.mime.splitlongparameters","false");//解决邮件名称过长会被截断的问题
    }


    public  static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {

        MailConfBean mailConfBean = getSendMail();
        if (mailConfBean == null){
            logger.debug("邮件发送者为空，结束程序");
            return;
        }
        EmailUtil.mailConf = mailConfBean;


        TaskQueue taskQueue = new TaskQueue();
        startGetTasks(taskQueue);//开始sql任务的查询

        startMonitorFile(taskQueue);//开始文件的检测

    }


    /**
     * 查询获得发件这的邮件配置
     *
     * @return
     */
    private static MailConfBean getSendMail()  {
        logger.debug("开始从数据库查询邮件发送者的邮件配置");
        ComboPooledDataSource dataSource = DBUtil.getDataSource();
        QueryRunner queryRunner = new QueryRunner(dataSource);
        List<MailConfBean> mailConfBeans = null;
        try {
            mailConfBeans = queryRunner.query(Constant.SQL.GET_SEND_MAIL, new BeanListHandler<MailConfBean>(MailConfBean.class));
        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("查询邮件发送者的邮件配置出现异常");
        }
        if (mailConfBeans != null && mailConfBeans.size() > 0) {
            logger.debug("查询邮件发送者的邮件配置成功");

            //处理密码加解密
            MailConfBean mailConfBean = mailConfBeans.get(0);
            if (mailConfBean.getPwdEncrypt() == 1){//加密过的密码，需要解密
                try {
                    mailConfBean.setPassword(DesUtil.getInstance().decrypt(mailConfBean.getPassword()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {//密码没有加密，则需要加密
                try {
                    String encryptPwd = DesUtil.getInstance().encrypt(mailConfBean.getPassword());
                    String sql = "update send_mail set password = ?, pwdEncrypt = ? where user = ?";
                    queryRunner.update(sql, encryptPwd, "1", mailConfBean.getUser());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            return mailConfBean;
        }
        logger.debug("查询邮件发送者的邮件配置失败");
        return null;
    }

    /**
     * 测试代码
     */
    private static void addEmailUser(){
        String sql = "insert into send_mail(auth, protocol, host, port, user, password) values(?, ?, ?, ?, ?, ?) ";
        ComboPooledDataSource dataSource = DBUtil.getDataSource();
        QueryRunner queryRunner = new QueryRunner(dataSource);
        try {
            queryRunner.update(sql, "1", "test", "test", "test", "测试是否插入中文乱码", "test");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 开始监听文件的变化
     *
     */
    private static void startMonitorFile(final TaskQueue taskQueue){
        String sqlMonitorPath = System.getProperty("user.dir") + Constant.FileConfig.CONF_DIR ;
        MonitorTask monitorTask = new MonitorTask(sqlMonitorPath, new MonitorTask.FileChangeListener() {
            @Override
            public void onCreated(String path) {

            }

            @Override
            public void onDelete(String path) {

            }

            @Override
            public void onModify(String path) {
                logger.debug("唤醒查询sql任务的线程");
                taskQueue.startGetSQlTasks();//唤醒查询sql任务的线程

            }
        });
        executorService.submit(monitorTask);

    }


    /**
     * 开始查询sql的
     * @param taskQueue
     */
    private static void startGetTasks(TaskQueue taskQueue){
        GetTasks getTasks = new GetTasks(taskQueue);
        new Thread(getTasks).start();
    }


}
