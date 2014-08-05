package com.onetouch.model.emailbatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
public class EmailBatchProcessor implements EmailProcessor{
	
	@Autowired
	private IBatchEmailSender batchEmailSender;

	//@Scheduled(cron="0 0/5 * * * *")
	@Scheduled(fixedDelay=300000)
	public void sendEmail() {
		
		batchEmailSender.sendFailedEmails();
		
	}

}
