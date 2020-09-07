/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.service;

public interface IEmailService {

    public void sendMail(String to, String subject, String content);

}
