package com.suxiaoshuai.util.sms;


import com.suxiaoshuai.exception.SxsToolsException;
import com.suxiaoshuai.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * 邮箱工具类
 */
public class EmailUtil {


    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);
    /**
     * 163邮箱服务器地址,端口号
     */
    private static final String EMAIL_163_HOST = "smtp.163.com";
    private static final int EMAIL_163_PORT = 25;
    private static final String EMAIL_PROTOCOL_SMTP = "smtp";

    /**
     * 是否进行smtp鉴权认证key
     */
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    /**
     * 发送的协议key
     */
    public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";

    /**
     * 发送邮件(支持发送给多人)
     *
     * @param context           邮件内容
     * @param subject           邮件主题
     * @param recipients        收件人
     * @param sender            发件人
     * @param authorizationCode 邮箱授权码
     */
    public static void sendFrom163(String context, String subject, String[] recipients, String sender, String authorizationCode) {

        try {
            if (null == recipients || recipients.length <= 0) {
                return;
            }
            Properties props = new Properties();
            // 必须 普通客户端
            props.setProperty(MAIL_SMTP_AUTH, "true");
            // 必须选择协议
            props.setProperty(MAIL_TRANSPORT_PROTOCOL, EMAIL_PROTOCOL_SMTP);
            Session session = Session.getDefaultInstance(props);
            // 设置debug模式   在控制台看到交互信息
            session.setDebug(true);
            // 建立一个要发送的信息
            Message msg = new MimeMessage(session);
            // 设置发送的时间
            msg.setSentDate(new Date());
            if (StringUtil.isNotBlank(subject)) {
                // 设置发送的主题
                msg.setSubject(subject);
            }
            // 设置简单的发送内容
            msg.setContent(context, "text/html;charset=UTF-8");
            // 发件人邮箱号
            msg.setFrom(new InternetAddress(sender));
            // 发送信息的工具
            Transport transport = session.getTransport();
            // 发件人邮箱号 和密码
            transport.connect(EMAIL_163_HOST, EMAIL_163_PORT, sender, authorizationCode);
            int length = recipients.length;
            Address[] addresses = new Address[length];
            // 对方的地址
            for (int i = 0; i < length; i++) {
                addresses[i] = new InternetAddress(recipients[i]);
            }
            transport.sendMessage(msg, addresses);
            transport.close();
        } catch (Exception e) {
            logger.error("fail to send email", e);
            throw new SxsToolsException(e);
        }
    }
}
