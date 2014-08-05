package com.onetouch.view.security;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.UAgentInfo;

public class CustomLoginFailureHandler implements AuthenticationFailureHandler {
    public CustomLoginFailureHandler() {
        
    }
    private String defaultFailureUrl;
    

	public String getDefaultFailureUrl() {
		return defaultFailureUrl;
	}


	public void setDefaultFailureUrl(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
	}


	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		String userAgent = request.getHeader("User-Agent");
		String accept = request.getHeader("Accept");
		if (userAgent != null && accept != null){
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ServletContext context = FacesUtils.getServletContext();
			UAgentInfo agent = new UAgentInfo(userAgent, accept);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
                    AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY, exception);
			FacesUtils.addErrorMessage("Username or password not valid.");
			if (agent.isMobileDevice()){
				String targetUrl = context.getContextPath()+"/ui/mobile/login.jsf";
				ec.redirect(targetUrl);
				
			}
			else{
				String targetUrl = context.getContextPath()+"/ui/login/login.jsf";
	            ec.redirect(targetUrl);
			}
		}
	}

}