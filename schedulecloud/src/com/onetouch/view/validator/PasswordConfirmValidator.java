package com.onetouch.view.validator;



import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.onetouch.view.util.ValidationMessages;


/**
 * Validator for password
 * verify if the passwords in family tab are same
 * (remember there is a inbuilt component from primefaces for this validation) 
 * @author duttam
 *
 */
@FacesValidator(value="passwordConfirmValidator")
public class PasswordConfirmValidator implements Validator {

    
    
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
    	
		String password = (String) component.getAttributes().get("password");
    	if(password==null){
        	FacesMessage message = ValidationMessages.getMessage(
                    ValidationMessages.MESSAGE_RESOURCES, "passwordConfirmError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    		
        // Cast the value of the entered password of the second field back to String.
        String confirmpassword = (String) value;

        // Compare the first password with the second password.
        if (!password.equals(confirmpassword)) {
        	FacesMessage message = ValidationMessages.getMessage(
                    ValidationMessages.MESSAGE_RESOURCES, "passwordConfirmError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        
	}

}