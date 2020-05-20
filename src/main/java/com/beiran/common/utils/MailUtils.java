package com.beiran.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件工具
 */

@Slf4j
public class MailUtils {

    // 发件人邮箱地址
    private static final String FROM = "abcd@gmail.com";

    // 发件人的邮箱账户
    private static final String USERNAME = "abcd@gmail.com";

    // 发件人的邮箱密码（授权码）
    private static final String PASSWORD = "123456";

    // SMTP 服务器地址，可以是其他的
    private static final String HOST = "smtp.qq.com";

    /**
     * 根据指定的邮箱地址发送邮件<br>
     * emailMsg 中可以使用 HTML 标签<br>
     * @param email 接收人的邮箱地址
     * @param emailMsg 邮件内容
     * @return 是否发送成功
     */
    public static Boolean sendEmail(String email, String emailMsg) {

        // 定义 Properties，设置环境信息
        Properties properties = System.getProperties();

        // 指定 SMTP 服务器，简单的邮件协议
        properties.setProperty("mail.smtp.host", HOST);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.transport.protocol", "smtp");

        // 创建邮件的 Session 对象
        Session session = Session.getInstance(properties);
        // 设置输出调试信息
        session.setDebug(true);
        try {
            // Message的实例对象表示一封电子邮件
            MimeMessage message = new MimeMessage(session);
            // 设置发件人的地址
            message.setFrom(new InternetAddress(FROM));
            // 设置主题
            message.setSubject("ERP");
            // 设置邮件的文本内容
            // message.setText("普通文本内容!");
            message.setContent((emailMsg), "text/html;charset=utf-8");

            // 设置附件
            // message.setDataHandler(dh);

            // 从session的环境中获取发送邮件的对象
            Transport transport = session.getTransport();
            // 连接邮件服务器 25是端口号
            transport.connect(HOST, 25, USERNAME, PASSWORD);
            // 设置收件人地址,并发送消息
            transport.sendMessage(message, new Address[] { new InternetAddress(email) });
            //关闭连接
            transport.close();
            log.info(" { 邮件发送成功 } -> " + email);
            return true;
        } catch (MessagingException e) {
            log.error(" { 邮件发送异常 } " + e.getLocalizedMessage());
            return false;
        }
    }
}
