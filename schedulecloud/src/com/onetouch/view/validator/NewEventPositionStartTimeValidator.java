package com.onetouch.view.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.view.bean.admin.event.AddEventBean;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;
@FacesValidator(value="newEventPositionStartTimeValidator")
public class NewEventPositionStartTimeValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		AddEventBean addEventBean = (AddEventBean)FacesUtils.getManagedBean("addEventBean");
		EventPosition eventPosition = addEventBean.getEventPosition();
		Date stime = (Date)value;
		Date etime = eventPosition.getEndTime();
		if(stime!=null && etime!=null){
			if(DateUtil.compareTimeOnly(stime,etime)>=0)
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Verify Start and End time","Verify Start and End time"));
			else{
    			
    		}
		}
		

	}

}
