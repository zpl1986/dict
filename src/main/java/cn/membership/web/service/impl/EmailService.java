/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.service.impl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.membership.common.util.PropertyUtil;
import cn.membership.web.service.IEmailService;

@Service
@Qualifier("emailService")
public class EmailService implements IEmailService {
    
    private Logger log = Logger.getLogger(getClass());


    @Override
    public void sendMail(String to, String subject, String content) {
        try {
            Properties mail = PropertyUtil.getProperties("/mail.properties");
            String username = mail.getProperty("username");
            String password = mail.getProperty("password");
            String host = mail.getProperty("host");
            String protocal = mail.getProperty("protocal");

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(props);
            MimeMessage message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, to);
            message.setFrom(new InternetAddress(username));
            message.setSubject(subject);
            message.setText(content, "UTF-8");
            Transport transport = session.getTransport(protocal);
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            String message = "";
            try {
                message = new String(e.getMessage().getBytes("iso8859-1"), "gbk");
            } catch (Exception e1) {
            }
            log.error("发邮件时异常:"+message,e);
        }
        
    }

    public static void main(String[] args) throws Exception {
        new EmailService().sendMail("380007905@qq.com,343702704@qq.com", "xxx", "xxx");
    }
}
