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
import com.onetouch.view.bean.admin.event.AddEventBean;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;
@FacesValidator(value="eventEndTimeValidator")
public class EventEndTimeValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		AddEventBean addEventBean = (AddEventBean)FacesUtils.getManagedBean("addEventBean");
		Event event = addEventBean.getEvent();
		Date stime = event.getStartTime();
		Date etime = (Date)value;
		if(stime!=null && etime!=null){
			if(DateUtil.compareTimeOnly(stime,etime)==0)
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Start Time and End Time can't be same","Start Time and End Time can't be same"));
			else{
    			String time = DateUtil.getTimeOnly(etime,"hh:mm a");
    			if(DateUtil.compareTimeOnly(stime,etime)>0){
    				Date endDate = DateUtil.incrementByDay(event.getStartDate(),1);
    				event.setEndDate(endDate);
    				FacesUtils.addInfoMessage("addEventForm:growl","Event ends on next day at "+time);
    			}else{
    				Date endDate = event.getStartDate();
    				event.setEndDate(endDate);
    				FacesUtils.addInfoMessage("addEventForm:growl","Event ends on same day at "+time);
    			}
    		}
		}
		

	}

}
