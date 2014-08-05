package com.onetouch.model.service.email;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.onetouch.model.dao.email.EmailStatusDAO;
import com.onetouch.model.domainobject.EmailStatus;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.exception.InviteEMailException;

@Service("inviteEmployeeMailSender")
public class InviteEmployeeEmail implements IInviteEmployeeEmail{



	private @Value("${emailTemplate.inviteEmployee}") String inviteEmployee;
	private @Value("${invite.registration.url}") String inviteRegistrationUrl;
	private @Value("${mail.smtp.host}") String smtpHost;
	@Autowired
	private EmailStatusDAO emailStatusDAO;
	@Autowired
	private VelocityEngine velocityEngine;
	@Autowired
	private SimpleMailMessage inviteMessage;

	@Autowired
	private JavaMailSenderImpl mailSender;

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}
	public void setInviteMessage(SimpleMailMessage inviteMessage) {
		this.inviteMessage = inviteMessage;
	}



	public void setJavaMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void inviteEmployee(Tenant tenant, Region region, final Map<Object, Object> hTemplateVariables, final String recipient, final String sender, final String operation)throws MailException {
		if(sender!=null)
			inviteMessage.setFrom(sender);//over ride default
		hTemplateVariables.put("inviteRegistrationUrl", inviteRegistrationUrl);
		final Integer employeeId = (Integer)hTemplateVariables.get("empid");
		final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, inviteEmployee, hTemplateVariables);
		
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
            	
            	if(recipient!=null) {

            		if(isValid(recipient))
            			mimeMessage.addRecipients(Message.RecipientType.TO, recipient);

            	}else{
            		if(inviteMessage.getTo()!=null){
            			String to =inviteMessage.getTo()[0]; 
            			mimeMessage.addRecipients(Message.RecipientType.TO, to);
            		}
            	}
               MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
               if(inviteMessage!=null && inviteMessage.getFrom()!=null)
            	   message.setFrom(inviteMessage.getFrom());
               if(inviteMessage!=null && inviteMessage.getSubject()!=null)
            	   message.setSubject(inviteMessage.getSubject());
               else
            	   message.setSubject(hTemplateVariables.get("emailsubject").toString());
               
               if(hTemplateVariables.get("priority")!=null && hTemplateVariables.get("priority").toString().equalsIgnoreCase("HIGH"))
            	   message.setPriority(1);
               
               
               
               message.setText(body, true);
               
            }

			private boolean isValid(String recipient) {
				return true;
			}
         };
         EmailStatus emailStatus = new EmailStatus(inviteMessage.getFrom(),recipient,hTemplateVariables.get("emailsubject").toString(),
        		 operation,body,new Date(),region.getId(),tenant.getId(),employeeId,"pending");
         Integer recordId = emailStatusDAO.saveEmployeeEmail(emailStatus);
         mailSender.setHost(smtpHost);
         try{
         //mailSender.send(preparator);
         }catch (MailException e) {
        	 emailStatusDAO.updateStatus(recordId,"fail");
        	 e.printStackTrace();
        	 throw new InviteEMailException("Invitation Email couldn't send to Employee");
		}
        
    }
}