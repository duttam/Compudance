package com.onetouch.model.emailbatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.email.EmailStatusDAO;
import com.onetouch.model.dao.schedule.ScheduleDAO;
import com.onetouch.model.domainobject.EmailStatus;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.exception.InviteEMailException;

@Service("batchEmailSender")
public class BatchEmailSender implements IBatchEmailSender{

	private @Value("${mail.smtp.host}") String smtpHost;
	static final Logger logger = Logger.getLogger("BatchEmailSender.class");
	@Autowired
	private EmailStatusDAO emailStatusDAO;
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	@Override
	@Async @Transactional 
	public void sendFailedEmails() {
		List<EmailStatus> pendingEmails = new ArrayList<EmailStatus>();
		try{
		pendingEmails = emailStatusDAO.findAllPendingEmails();
		}catch (DataAccessException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		for(final EmailStatus emailStatus : pendingEmails){
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
					public void prepare(MimeMessage mimeMessage) throws Exception {
						String recipient = emailStatus.getToEmail();
						if(recipient!=null) {

							if(isValid(recipient))
								mimeMessage.addRecipients(Message.RecipientType.TO, recipient);

						}
						MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
						message.setFrom(emailStatus.getFromEmail());
						message.setSubject(emailStatus.getSubject());
						message.setPriority(1);
						message.setText(emailStatus.getContent(), true);

					}

					private boolean isValid(String recipient) {
						return true;
					}
				};

				mailSender.setHost(smtpHost);
				try{
					mailSender.send(preparator);
					emailStatusDAO.updateStatus(emailStatus.getId(),"success");
				}catch (MailException e) {
					emailStatusDAO.updateStatus(emailStatus.getId(),"fail");
					System.out.println(e.getMessage());
					sendAlertEmail(e.getMessage());
					
				}catch(Exception exception ){
					logger.warn(exception.getMessage());
				}
		}
	}
	
	public void sendAlertEmail(String errorMsg) {
		 
		  
		
		final String username = "manasjyoti.dutta";//"AKIAI6PNXJBYCAJFVBEQ";
		final String password = "Onetouch0806";//"AuhLSPGlnQLi+fbXq3Dg/dVfk20qfct/aDBM2ogkAwZW";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");//smtp.gmail.com
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("CXRA-NoReply@schedule-cloud.com"));
			
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("manasjyoti.dutta@gmail.com, manas.dutta@dfyoung.com,maryann.spitaletta@superiortechnology.com,bikram.barua@dfyoung.com"));
			message.setSubject("Server Error in Sending Email");
			message.setText(errorMsg);
 
			Transport.send(message);
 
			System.out.println("Done");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
		
	}
}
