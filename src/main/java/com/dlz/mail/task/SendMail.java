package com.dlz.mail.task;

import com.dlz.mail.bean.MailTaskBean;

import java.util.concurrent.BlockingQueue;

/**
 * 从待发送队sql执行结构列中取出待发送的sql任务
 */
public class SendMail implements Runnable {

    private BlockingQueue<MailTaskBean> sendMailQueue;//待发送的邮件队列

    public void run() {

    }
}
