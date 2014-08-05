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
@FacesValidator(value="salesEventStartDateValidator")
public class SalesEventStartDateValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		//AddEventBean addEventBean = (AddEventBean)FacesUtils.getManagedBean("addEventBean");
		//Event event = addEventBean.getEvent();
		CustomUser customUser = null;
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			customUser = (CustomUser)authenticationToken.getPrincipal();
		
		Date stime = (Date)value;
		
		if( customUser!=null && customUser.isSalesPerson()){
			if(stime==null)
				FacesUtils.addErrorMessage("addEventForm:startdate","Information required");
			else{
				if(DateUtil.dateRange(new Date(),stime)<2)
					throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select a future date","Please select a future date"));
			}
    	}
	}

}
