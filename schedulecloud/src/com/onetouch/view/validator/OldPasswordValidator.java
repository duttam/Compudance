package com.onetouch.view.validator;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.ValidationMessages;


/**
 * validator for old password, updateRegistration wizard
 * @author duttam
 *
 */
@FacesValidator(value="oldPasswordValidator")
public class OldPasswordValidator implements Validator {

    
    
	public void validate(FacesContext context, UIComponent component, Object value)	throws ValidatorException {
		String oldPassword = value.toString();
		CustomUserDetail user=null;
		if(value!=null){
			UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
			if(authenticationToken.isAuthenticated()){
				user = (CustomUser)authenticationToken.getPrincipal();
				
			}
			
			LoginBean loginBean =(LoginBean)FacesUtils.getManagedBean("loginBean");
			if(!loginBean.oldPasswdExist(user.getUsername(), oldPassword,user.getTenantId())){
				FacesMessage message = ValidationMessages.getMessage(
						ValidationMessages.MESSAGE_RESOURCES, "oldPasswordVerfiy", null);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(message);
			}

		}
}

}