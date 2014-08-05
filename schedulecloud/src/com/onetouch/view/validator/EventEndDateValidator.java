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
@FacesValidator(value="eventEndDateValidator")
public class EventEndDateValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		AddEventBean addEventBean = (AddEventBean)FacesUtils.getManagedBean("addEventBean");
		Event event = addEventBean.getEvent();
		Date sdate = event.getStartDate();
		Date edate = (Date)value;
		if(sdate!=null && edate!=null){
			if(sdate.getTime()>edate.getTime())
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Enter a valid end date","Enter a valid end date"));
		}
		/*if(edate==null)
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"InformationRequired","InformationRequired"));
		else if(sdate!=null && edate!=null){
			if(sdate.getTime()>edate.getTime())
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Enter a valid end date","Enter a valid end date"));
		}else{
		}*/

	}

}
