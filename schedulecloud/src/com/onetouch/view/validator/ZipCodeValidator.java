package com.onetouch.view.validator;


/**
 * validator class for zip code
 * Accepts zip codes like 01234 , only digits not letter
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.onetouch.view.util.ValidationMessages;

@FacesValidator(value="zipCodeValidator")
public class ZipCodeValidator implements Validator{
	
	
	private static final String ZIP_REGEX = "[0-9]{5}";
	
	
	
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
	      Pattern mask =  Pattern.compile(ZIP_REGEX);
	      String zipField = (String)value; 
			 	
	    
	    Matcher matcher = mask.matcher(zipField);
		     
	    if (!matcher.matches()){
		     	
	    	FacesMessage message = ValidationMessages.getMessage(ValidationMessages.MESSAGE_RESOURCES, "invalidZipCode", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
	    }
		
	}
	
	
}
