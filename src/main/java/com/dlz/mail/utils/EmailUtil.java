package com.dlz.mail.utils;

import com.dlz.mail.bean.MailConfBean;
import org.apache.commons.mail.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 邮件发送的工具类
 *
 * @author wangyuelin
 */
public class EmailUtil {
    private static final Logger logger = LogManager.getLogger(EmailUtil.class);

    public static MailConfBean mailConf;


    /**
     * 发送带附件的邮件
     *
     * @param attachmentPaths 附件的路径集合
     * @param subject
     * @param content
     * @param recipients      接收邮件的人
     * @param copyTos         邮件的抄送人
     */
    public static boolean sendAttachmentEmail(List<String> attachmentPaths,
                                              String subject, String content, String[] recipients, String[] copyTos) {
        logger.debug("开始发送带附件的邮件");

        if (mailConf == null) {
            logger.debug("发件人邮件的配置为空，返回");
            return false;
        }

        // Create the email message
        logger.debug("开始配置邮件");
        MultiPartEmail email = new MultiPartEmail();
        email.setCharset("utf-8");
        email.setHostName(mailConf.getHost());
        email.setAuthentication(mailConf.getUser(), mailConf.getPassword());
        try {
            email.addTo(recipients);
            if (TextUtil.isEmpty(mailConf.getUserName())) {
                email.setFrom(mailConf.getUser());//不带名称的
            } else {
                email.setFrom(mailConf.getUser(), mailConf.getUserName());//带名称
            }

            if (TextUtil.isEmpty(subject)) {
                subject = "";
            }
            email.setSubject(subject);
            if (TextUtil.isEmpty(content)) {
                content = " ";
            }
            email.setMsg(content);

            //添加附件
            logger.debug("开始添加附件");
            if (attachmentPaths != null) {
                int size = attachmentPaths.size();
                for (int i = 0; i < size; i++) {
                    String path = attachmentPaths.get(i);
                    File file = new File(path);
                    //发送带附件的邮件
                    EmailAttachment attachment = new EmailAttachment();
                    //附件的路劲
                    attachment.setPath(file.getAbsolutePath());
                    attachment.setDisposition(EmailAttachment.ATTACHMENT);

                    String fileName = FileUtil.getFileNameWithType(path);
                    if (TextUtil.isEmpty(fileName)) {
                        fileName = "查询结果";
                    }

                    //将附件的名称加上日期
                    fileName = FileUtil.addTime(fileName);
                    attachment.setDescription(fileName);//setName中文会出问题，使用这个代替
                    logger.debug("添加的附件：name:" + fileName + " 路径：" + file.getAbsolutePath());
                    email.attach(attachment);

                }
            }

            //添加抄送人
            if (copyTos != null && copyTos.length > 0) {
                email.addCc(copyTos);
            }
            logger.debug("发送");
            email.send();
            logger.debug("发送成功");
            return true;
        } catch (EmailException e) {
            logger.debug("邮件发送异常");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将错误发送到错误的订阅者
     *
     * @param toAddress
     * @param subject
     * @param content
     */
    public static boolean sendMail(String toAddress, String subject, String content) {
        logger.debug("开始发送简单邮件");
        if (mailConf == null) {
            logger.debug("发件人邮件的配置为空");
            return false;
        }
        if (subject == null) {
            subject = "";
        }


        Email email = new SimpleEmail();
        email.setDebug(true);
        email.setCharset("utf-8");
        email.setHostName(mailConf.getHost());
        email.setAuthenticator(new DefaultAuthenticator(mailConf.getUser(), mailConf.getPassword()));
        //email.setSSLOnConnect(true);
        //        email.setSSL(true);//commons-mail-1.1支持的方法，1.4中使用setSSLOnConnect(true)代替
        try {
            if (TextUtil.isEmpty(mailConf.getUserName())) {
                email.setFrom(mailConf.getUser());
            } else {
                email.setFrom(mailConf.getUser(), mailConf.getUserName());
            }

            email.setSubject(subject);
            email.setMsg(content);
            email.addTo(toAddress);
            logger.debug("开始发送");
            email.send();
            logger.debug("邮件发送成功，接收人：" + toAddress);
            return true;
        } catch (EmailException e) {
            logger.debug("邮件发送异常：" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return false;

    }


    /**
     * 发送带html邮件，可以带附件
     *
     * @param attachmentPaths 附件的路径
     * @param subject
     * @param content
     * @param recipients      接收邮件的人
     * @param copyTos         邮件的抄送人
     */
    public static boolean sendHtmlEmail(List<String> attachmentPaths,
                                        String subject, String content, String[] recipients, String[] copyTos) {
        if (mailConf == null) {
            logger.debug("发件人邮件的配置为空，返回");
            return false;
        }

        logger.debug("开始配置html邮件");
        //发送带附件的邮件
        HtmlEmail htmlEmail = new HtmlEmail();
        //邮件的配置
        htmlEmail.setHostName(mailConf.getHost());
        htmlEmail.setAuthentication(mailConf.getUser(), mailConf.getPassword());
        htmlEmail.setCharset("UTF-8");
        if (TextUtil.isEmpty(subject)) {
            subject = "";
        }
        htmlEmail.setSubject(subject);
        try {
            //设置邮件的抄送者
            htmlEmail.setHtmlMsg(content);
            if (copyTos != null && copyTos.length > 0) {
                htmlEmail.addCc(copyTos);
            }
            //摄者邮件的发送者
            if (TextUtil.isEmpty(mailConf.getUserName())) {
                htmlEmail.setFrom(mailConf.getUser());
            } else {
                htmlEmail.setFrom(mailConf.getUser(), mailConf.getUserName());
            }

            //设置邮件的接收
            if (recipients != null) {
                htmlEmail.addTo(recipients);
            }
        } catch (EmailException e) {
            logger.debug("html邮件配置异常");
            e.printStackTrace();
        }

        //添加附件
        int size = attachmentPaths.size();
        logger.debug("开始添加附件， 总的附件数：" + size);
        if (attachmentPaths != null) {
            for (int i = 0; i < size; i++) {
                String path = attachmentPaths.get(i);
                if (TextUtil.isEmpty(path)) {
                    continue;
                }
                EmailAttachment emailAttachment = new EmailAttachment();
                emailAttachment.setPath(new File(path).getAbsolutePath());
                emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
                emailAttachment.setDisposition("excel");
                String fileName = FileUtil.getFileNameWithType(path);
                if (TextUtil.isEmpty(fileName)) {
                    fileName = "查询结果";
                }
                emailAttachment.setName(fileName);
                logger.debug("构造附件：" + fileName);

                try {
                    htmlEmail.attach(emailAttachment);
                    logger.debug("将附件添加到邮件");
                } catch (EmailException e) {
                    logger.debug("附件添加异常");
                    e.printStackTrace();
                }
            }
        }


        try {
            logger.debug("开始发送邮件");
            htmlEmail.send();
            logger.debug("邮件发送成功");
            return true;
        } catch (EmailException e) {
            logger.debug("邮件发送异常");
            e.printStackTrace();
        }

        logger.debug("邮件发送失败");
        return false;
    }

    /**
     * 发送邮件
     *
     * @param attachmentPaths
     * @param subject
     * @param content
     * @param recipients
     * @param copyTos
     * @param type            sql查询的结果在邮件中的形式： 0：放在内容   1：放在附件
     * @return
     */
    public static boolean sendSQLEmail(List<String> attachmentPaths, String subject,
                                       String content, String[] recipients, String[] copyTos, int type) {
        boolean result = false;
//        if (type == Constant.SQL_RESULT_TYPE.CONTENT) {
            result = sendHtmlEmail(attachmentPaths, subject, content, recipients, copyTos);
//        } else if (type == Constant.SQL_RESULT_TYPE.ATTACMENT) {
//            result = sendAttachmentEmail(attachmentPaths, subject, content, recipients, copyTos);
//        }
        return result;
    }
}
