package com.onetouch.model.service.email;

import java.util.List;
import java.util.Map;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;

public interface IScheduleEmployeeEmail {
	public void scheduleEmployee(Tenant tenant, Region region, Map<Object, Object> hTemplateVariables,String recipient, String sender, String operation)throws MailException;

	public void sendScheduleNotes(Tenant tenant, Region selectedRegion,Map<Object, Object> attributes, String email, String emailSender,String string);

	

	
}
