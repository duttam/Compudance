package com.onetouch.view.security;

import java.io.IOException;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.UAgentInfo;

@SuppressWarnings("deprecation")

public class MobileAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint{

	protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
			
		String userAgent = request.getHeader("User-Agent");
		String accept = request.getHeader("Accept");
		if (userAgent != null && accept != null){
			
			UAgentInfo agent = new UAgentInfo(userAgent, accept);
			if (agent.isMobileDevice())
				return "/ui/mobile/login.jsf";
			else
				return getLoginFormUrl();
		}
        
		return getLoginFormUrl();
    }
	
}

