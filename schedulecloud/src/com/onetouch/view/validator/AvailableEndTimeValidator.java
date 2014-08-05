package com.onetouch.view.validator;



import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.joda.time.LocalTime;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.bean.useremployee.availability.AvailabilityHome;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.ValidationMessages;


/**
 * validator for old password, updateRegistration wizard
 * @author duttam
 *
 */
@FacesValidator(value="endTimeValidator")
public class AvailableEndTimeValidator implements Validator {

    
    
	public void validate(FacesContext context, UIComponent component, Object value)	throws ValidatorException {
		AvailabilityHome availabilityHome = (AvailabilityHome)FacesUtils.getManagedBean("availHome");
		Date startTime = availabilityHome.getAvailability().getStartTime();
		LocalTime StartTime = new LocalTime(startTime.getTime());
		Date endTime = (Date)value;
		LocalTime EndTime = new LocalTime(endTime.getTime());
		if((StartTime.compareTo(EndTime))>=0){
		FacesMessage message = ValidationMessages.getMessage(
						ValidationMessages.MESSAGE_RESOURCES, "endtimeVerfiy", null);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(message);
			

		}
		else{
			
		}

	}
}