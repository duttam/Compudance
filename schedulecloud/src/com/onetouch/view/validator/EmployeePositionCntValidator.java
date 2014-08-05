package com.onetouch.view.validator;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.onetouch.view.util.ValidationMessages;

@FacesValidator(value="EmployeePositionCntValidator")
public class EmployeePositionCntValidator implements Validator{
	
	
	private static final String ZIP_REGEX = "[0-9]{4,17}";
	
	
	
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
	      Pattern mask =  Pattern.compile(ZIP_REGEX);
	      String empCount = (String)value; 
			 	
	    
	    Matcher matcher = mask.matcher(empCount);
		     
	    if (!matcher.matches()){
		     	
	    	FacesMessage message = ValidationMessages.getMessage(ValidationMessages.MESSAGE_RESOURCES, "invalidNumber", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
	    }else{
	    	
	    }
		
	}
	
	
}
