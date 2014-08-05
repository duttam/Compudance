package com.onetouch.model.service.email;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
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

@Service("scheduleEmployeeMailSender")
public class ScheduleEmployeeEmail implements IScheduleEmployeeEmail{

	static final Logger logger = Logger.getLogger("BatchEmailSender.class");

	private @Value("${emailTemplate.scheduleEmployee}") String scheduleEmployee;
	private @Value("${emailTemplate.resendScheduleEmployee}") String resendScheduleEmployee;
	private @Value("${emailTemplate.deleteScheduleEmployee}") String deleteScheduleEmployee;
	private @Value("${emailTemplate.scheduleNote}") String sendScheduleNotes;
	private @Value("${emailTemplate.offerScheduleEmployee}") String offerScheduleEmployee;
	private @Value("${emailTemplate.moveScheduleEmployee}") String moveScheduleEmployee;
	private @Value("${mail.smtp.host}") String smtpHost;
	@Autowired
	private EmailStatusDAO emailStatusDAO;
	@Autowired
	private VelocityEngine velocityEngine;
	@Autowired
	private SimpleMailMessage scheduleMessage;

	@Autowired
	private JavaMailSenderImpl mailSender;

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}
	


	public void setScheduleMessage(SimpleMailMessage scheduleMessage) {
		this.scheduleMessage = scheduleMessage;
	}



	public void setJavaMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public synchronized void scheduleEmployee(Tenant tenant, Region region, final Map<Object, Object> hTemplateVariables, final String recipient, final String sender, final String operation)throws MailException {
		if(sender!=null)
			scheduleMessage.setFrom(sender);//over ride default
		String content = "";
		if(operation.equalsIgnoreCase("notify employee"))
			content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, scheduleEmployee, hTemplateVariables);
		else if(operation.equalsIgnoreCase("resend schedule"))
			content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, resendScheduleEmployee, hTemplateVariables);
		else if(operation.equalsIgnoreCase("delete schedule"))
			content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, deleteScheduleEmployee, hTemplateVariables);
		else if(operation.equalsIgnoreCase("offer schedule"))
			content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, offerScheduleEmployee, hTemplateVariables);
		else if(operation.equalsIgnoreCase("move schedule"))
			content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, moveScheduleEmployee, hTemplateVariables);
		else{}
		
		final String body = content;
		final Integer employeeId = (Integer)hTemplateVariables.get("employeeId");
		final Integer scheduleId = (Integer)hTemplateVariables.get("scheduleId");
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
            	
            	if(recipient!=null) {

            		if(isValid(recipient))
            			mimeMessage.addRecipients(Message.RecipientType.TO, recipient);

            	}else{
            		if(scheduleMessage.getTo()!=null){
            			String to =scheduleMessage.getTo()[0]; 
            			mimeMessage.addRecipients(Message.RecipientType.TO, to);
            		}
            	}
               MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
               if(scheduleMessage!=null && scheduleMessage.getFrom()!=null)
            	   message.setFrom(scheduleMessage.getFrom());
               if(scheduleMessage!=null && scheduleMessage.getSubject()!=null)
            	   message.setSubject(scheduleMessage.getSubject());
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
         EmailStatus emailStatus = new EmailStatus(scheduleMessage.getFrom(),recipient,hTemplateVariables.get("emailsubject").toString(),operation,body,new Date(),
        		 scheduleId,employeeId,region.getId(),tenant.getId(),"pending");
         Integer recordId = emailStatusDAO.saveScheduleEmail(emailStatus);
         mailSender.setHost(smtpHost);
         try{
             //mailSender.send(preparator);
             }catch (MailException e) {
            	 //emailStatusDAO.updateStatus(recordId,"fail");
            	 logger.warn(e.getMessage());
            	 throw new InviteEMailException(" Email couldn't send to Employee");
            	 
					
				}catch(Exception exception ){
					logger.warn(exception.getMessage());
				}
        
    }



	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void sendScheduleNotes(Tenant tenant, Region region,
			final Map<Object, Object> hTemplateVariables, final String recipient, String emailSender,
			String operation) {
		
		final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, sendScheduleNotes, hTemplateVariables);
		
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
            	
            	if(recipient!=null) {

            		if(isValid(recipient))
            			mimeMessage.addRecipients(Message.RecipientType.TO, recipient);

            	}else{
            		if(scheduleMessage.getTo()!=null){
            			String to =scheduleMessage.getTo()[0]; 
            			mimeMessage.addRecipients(Message.RecipientType.TO, to);
            		}
            	}
               MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
               if(scheduleMessage!=null && scheduleMessage.getFrom()!=null)
            	   message.setFrom(scheduleMessage.getFrom());
               if(scheduleMessage!=null && scheduleMessage.getSubject()!=null)
            	   message.setSubject(scheduleMessage.getSubject());
               else
            	   message.setSubject(hTemplateVariables.get("emailsubject").toString());
               
               
               
               message.setText(body, true);
               
            }

			private boolean isValid(String recipient) {
				return true;
			}
         };
         EmailStatus emailStatus = new EmailStatus(scheduleMessage.getFrom(),recipient,hTemplateVariables.get("emailsubject").toString(),operation,body,new Date(),
        		 region.getId(),tenant.getId(),"pending");
         Integer recordId = emailStatusDAO.saveScheduleNoteEmail(emailStatus);
         
         mailSender.setHost(smtpHost);
         try{
             //mailSender.send(preparator);
             }catch (MailException e) {
            	 //emailStatusDAO.updateStatus(recordId,"fail");
            	 logger.warn(e.getMessage());
            	 throw new InviteEMailException("Email couldn't send to Employee");
    		}catch (Exception exception) {
    			logger.warn(exception.getMessage());
			}
		
	}
	
}