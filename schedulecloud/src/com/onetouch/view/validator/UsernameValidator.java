package com.onetouch.view.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.onetouch.view.bean.useremployee.employee.RegisterEmployeeBean;
import com.onetouch.view.util.FacesUtils;
@FacesValidator(value="usernameValidator")
public class UsernameValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		// TODO Auto-generated method stub
		try{
		RegisterEmployeeBean registerEmployeeBean = (RegisterEmployeeBean)FacesUtils.getManagedBean("registerEmployeeBean");
		if(registerEmployeeBean.userAlreadyExist(value.toString()))
			FacesUtils.addErrorMessage("Username already exist");
		}catch(UsernameNotFoundException usernameNotFoundException){
			
		}
		
		
	}

}
