
package com.onetouch.view.email;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

import com.onetouch.model.domainobject.Tenant;

/**
 * Sends an e-mail message.
 * 
 * 
 */
public interface Sender { 

    /**
     * Sends e-mail using Velocity template for the body and 
     * the properties passed in as Velocity variables.
     * 
     * @param   msg                 The e-mail message to be sent, except for the body.
     * @param   hTemplateVariables  Variables to use when processing the template.
     * @param   recipients 			email ids to which msg to be sent 
     * @param sender 
     */
    //public void send(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables,List<String> recipients);
    
    public void inviteEmployee(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables,List<String> recipients, String sender)throws MailException;
    public void sendScheduleNotes(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables,List<String> recipients, String sender)throws MailException;
    
    public void sendScheduleNotes(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables,List<String> recipients, String sender, String ccEmail,Tenant tenant,List<String> uploadAttachment)throws MailException;
    
    public void changeScheduleEmployee(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables,List<String> recipients)throws MailException;
    public void scheduleEmployee(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables,List<String> recipients, String sender)throws MailException;
    public void deleteScheduleEmployee(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables, List<String> recipients,String sender) throws MailException;
    public void timeOffNotification(SimpleMailMessage msg, Map<Object, Object> hTemplateVariables,List<String> recipients,String type, String sender)throws MailException;
	public void sendTempPasswordEmployee(SimpleMailMessage resetPasswordMessage,Map<Object, Object> attributes, List<String> receipients);
	public void resendScheduleEmployee(SimpleMailMessage msg,Map<Object, Object> hTemplateVariables, List<String> recipients,String sender) throws MailException;
}
