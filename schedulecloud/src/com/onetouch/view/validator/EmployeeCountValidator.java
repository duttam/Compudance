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
@FacesValidator(value="employeeCountValidator")
public class EmployeeCountValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		Integer empCnt = (Integer)value;
		if(empCnt<=0)
		{
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Verify Employee Count","Must be greater than 0"));
		}
		
	}

}
