package com.onetouch.view.email;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
/*
 * This class is not being used now. In future if email is slow then use this 
 */
public class AsyncEmailSender implements Runnable {
	 
    private MimeMessagePreparator preparator;
    private JavaMailSenderImpl mailSender;

    private AsyncEmailSender(MimeMessagePreparator preparator,JavaMailSenderImpl mailSender) {
        this.preparator = preparator;
        this.mailSender = mailSender;
    }

    public void run() {
    	mailSender.send(preparator);
    }
}
