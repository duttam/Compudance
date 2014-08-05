
package com.onetouch.view.email;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;

/**
 * Sends an e-mail message.
 * 
 * 
 */
@Component(value="sender")
public class VelocityEmailSender implements Sender { 

    private static final Logger logger = LoggerFactory.getLogger(VelocityEmailSender.class);

    private final VelocityEngine velocityEngine;
    private final JavaMailSenderImpl mailSender;
        
    private @Value("${emailTemplate.inviteEmployee}") String inviteEmployee;
    private @Value("${emailTemplate.scheduleNote}") String sendScheduleNotes;
    private @Value("${emailTemplate.scheduleEmployee}") String scheduleEmployee;
    //private @Value("${emailTemplate.changeScheduleEmployee}") String changeScheduleEmployee;
    private @Value("${emailTemplate.resendScheduleEmployee}") String resendScheduleEmployee;
    private @Value("${emailTemplate.sickRequestTimeOff}") String sickRequestTimeOff;
    private @Value("${emailTemplate.vacationRequestTimeOff}") String vacationRequestTimeOff;
    private @Value("${emailTemplate.resetPassword}") String resetPassword;
    private @Value("${invite.registration.url}") String inviteRegistrationUrl;
    private @Value("${mail.smtp.host}") String smtpHost;
    private @Value("${emailTemplate.deleteScheduleEmployee}") String deleteScheduleEmployee;
    /**
     * Constructor
     */
    @Autowired
    public VelocityEmailSender(VelocityEngine velocityEngine, 
    		JavaMailSenderImpl mailSender) {
        this.velocityEngine = velocityEngine;
        this.mailSender = mailSender;
        //this.inviteEmployee = inviteEmployee;
        //this.scheduleEmployee = scheduleEmployee;
    }
    // this method will run asynchronously
    @Async    
    protected void send(final SimpleMailMessage msg, 
                     final Map<Object, Object> hTemplateVariables, final List<String> recipients, final String emailTemplate) throws MailException

   {
    	
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
            	
            	if(recipients!=null && recipients.size()>0){
	            	for (String recipient :  recipients) {
	    				if(isValid(recipient))
	    					mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
	    			}
            	}else{
            		if(msg.getTo()!=null){
            			String to =msg.getTo()[0]; 
            			mimeMessage.addRecipients(Message.RecipientType.TO, to);
            		}
            	}
               MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
               if(msg!=null && msg.getFrom()!=null)
            	   message.setFrom(msg.getFrom());
               //message.setFrom("STS-NoReply@schedule-cloud.com");
               if(msg!=null && msg.getSubject()!=null)
            	   message.setSubject(msg.getSubject());
               else
            	   message.setSubject(hTemplateVariables.get("emailsubject").toString());
               
               if(hTemplateVariables.get("priority")!=null && hTemplateVariables.get("priority").toString().equalsIgnoreCase("HIGH"))
            	   message.setPriority(1);
               
               String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, emailTemplate, hTemplateVariables);
               
               logger.info("body={}", body);

               message.setText(body, true);
               
            }

			private boolean isValid(String recipient) {
				return true;
			}
         };
         mailSender.setHost(smtpHost);
         /*if(hTemplateVariables.get("smtphost")!=null){
        	 String smtphost = hTemplateVariables.get("smtphost").toString();
        	 if(smtphost!=null && smtphost.length()>0)
        		 mailSender.setHost(smtphost);
         }*/
         
        /*Properties props = new Properties();
 		props.put("mail.smtp.auth", "true");
 		props.put("mail.smtp.starttls.enable", "true");
 		props.put("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
 		props.put("mail.smtp.port", "587");
  
 		Session session = Session.getInstance(props,
 		  new javax.mail.Authenticator() {
 			protected PasswordAuthentication getPasswordAuthentication() {
 				return new PasswordAuthentication("AKIAI6PNXJBYCAJFVBEQ", "AuhLSPGlnQLi+fbXq3Dg/dVfk20qfct/aDBM2ogkAwZW");
 			}
 		  });
 		 mailSender.setSession(session);*/
         mailSender.send(preparator);
        
        //logger.info("Sent e-mail to '{}'.", msg.getTo());
    }
   
    @Async    
    protected void send(final SimpleMailMessage msg, 
                     final Map<Object, Object> hTemplateVariables, final String recipient, final String emailTemplate) throws MailException
    {
    	MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
            	if(recipient!=null){
	            		if(isValid(recipient))
	    					mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
	    			
            	}else{
            		if(msg.getTo()!=null){
            			String to =msg.getTo()[0]; 
            			mimeMessage.addRecipients(Message.RecipientType.TO, to);
            		}
            	}
               MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
               if(msg!=null && msg.getFrom()!=null)
            	   message.setFrom(msg.getFrom());
               if(msg!=null && msg.getSubject()!=null)
            	   message.setSubject(msg.getSubject());
               else
            	   message.setSubject(hTemplateVariables.get("emailsubject").toString());
               
               if(hTemplateVariables.get("priority")!=null && hTemplateVariables.get("priority").toString().equalsIgnoreCase("HIGH"))
            	   message.setPriority(1);
               
               String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, emailTemplate, hTemplateVariables);
               
               logger.info("body={}", body);

               message.setText(body, true);
            }

			private boolean isValid(String recipient) {
				return true;
			}
         };
         mailSender.setHost(smtpHost);
         
         mailSender.send(preparator);
    	
    }
	@Override
	public synchronized void inviteEmployee(SimpleMailMessage msg,
			Map<Object, Object> hTemplateVariables, List<String> recipients,String sender)throws MailException {
		if(sender!=null)
			 msg.setFrom(sender);//over ride default
		hTemplateVariables.put("inviteRegistrationUrl", inviteRegistrationUrl);
		send(msg, hTemplateVariables, recipients, inviteEmployee);
		
	}

	@Override
	public synchronized void sendScheduleNotes(SimpleMailMessage msg,
			Map<Object, Object> hTemplateVariables, List<String> recipients,String sender)throws MailException {
		if(sender!=null)
			 msg.setFrom(sender);
		for(String recipient : recipients)
			send(msg, hTemplateVariables, recipient, sendScheduleNotes);
		
	}
	
	@Override
	public synchronized void scheduleEmployee(SimpleMailMessage msg,
			Map<Object, Object> hTemplateVariables, List<String> recipients,String sender) throws MailException {
		if(sender!=null)
			 msg.setFrom(sender); 
		send(msg, hTemplateVariables, recipients, scheduleEmployee);
		
	}
	@Override
	public synchronized void resendScheduleEmployee(SimpleMailMessage msg,
			Map<Object, Object> hTemplateVariables, List<String> recipients,String sender) throws MailException {
		if(sender!=null)
			 msg.setFrom(sender);
		send(msg, hTemplateVariables, recipients, resendScheduleEmployee);
		
	}
	@Override
	public synchronized void deleteScheduleEmployee(SimpleMailMessage msg,
			Map<Object, Object> hTemplateVariables, List<String> recipients,String sender) throws MailException {
		if(sender!=null)
			 msg.setFrom(sender); 
		send(msg, hTemplateVariables, recipients, deleteScheduleEmployee);
		
	}
	@Override
	public synchronized void changeScheduleEmployee(SimpleMailMessage msg,
			Map<Object, Object> hTemplateVariables, List<String> recipients) throws MailException {
		//send(msg, hTemplateVariables, recipients, changeScheduleEmployee);
		
	}
	 @Override
     public synchronized void timeOffNotification(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables,List<String> recipients, String type,String sender)
         throws MailException {
		 if(msg.getFrom()==null)
			 msg.setFrom(sender);
		 
		 if(type.equalsIgnoreCase("Sick"))
			 send(msg, hTemplateVariables, recipients, sickRequestTimeOff);
		 else
			 send(msg, hTemplateVariables, recipients, vacationRequestTimeOff); 
     }


	@Override
	public synchronized void sendTempPasswordEmployee(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables, List<String> recipients) {
		
		send(msg, hTemplateVariables, recipients, resetPassword); 
	}
	@Override
	public synchronized void sendScheduleNotes(final SimpleMailMessage msg, final Map<Object, Object> hTemplateVariables,
			final List<String> recipients, final String sender, final String ccEmail, final Tenant tenant, final List<String> uploadAttachment) throws MailException {
		if(tenant.getEmailSender()!=null)
			 msg.setFrom(tenant.getEmailSender());
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
            	
            	if(recipients!=null && recipients.size()>0){
	            	for (String recipient :  recipients) {
	    				if(isValid(recipient))
	    					mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
	    			}
            	}else{
            		if(msg.getTo()!=null){
            			String to =msg.getTo()[0]; 
            			mimeMessage.addRecipients(Message.RecipientType.TO, to);
            		}
            	}
               MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true, "UTF-8");
               message.addCc(ccEmail);
               message.setFrom(sender);
               /*if(msg!=null && msg.getFrom()!=null)
            	   message.setFrom(msg.getFrom());*/
               //message.setFrom("STS-NoReply@schedule-cloud.com");
               if(msg!=null && msg.getSubject()!=null)
            	   message.setSubject(msg.getSubject());
               else
            	   message.setSubject(hTemplateVariables.get("emailsubject").toString());
               
               if(hTemplateVariables.get("priority")!=null && hTemplateVariables.get("priority").toString().equalsIgnoreCase("HIGH"))
            	   message.setPriority(1);
               
               String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, sendScheduleNotes, hTemplateVariables);
               
               logger.info("body={}", body);

               message.setText(body, true);
               String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/upload/"+tenant.getCode()+"/");
               for(String filename : uploadAttachment){
            	   FileSystemResource file = new FileSystemResource(path+"/"+filename);
            	   message.addAttachment(file.getFilename(), file);
               }
       		   
            }

			private boolean isValid(String recipient) {
				return true;
			}
         };
         mailSender.setHost(smtpHost);
         mailSender.send(preparator);
        
	}
}
