package com.onetouch.view.security;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Tenant;

import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.context.TenantContextUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.UAgentInfo;

public class SwitchUserSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		String userAgent = request.getHeader("User-Agent");
		String accept = request.getHeader("Accept");
		if (userAgent != null && accept != null){
			
		    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ServletContext context = FacesUtils.getServletContext();
			CustomUser user = (CustomUser)authentication.getPrincipal();
			
			UAgentInfo agent = new UAgentInfo(userAgent, accept);
			if (agent.isMobileDevice()){
				ec.redirect(context.getContextPath()+"/"+ "ui/mobile/employee/empHome.jsf");
				
			}else{
				SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
		    	supportBean.setHomeUrlTemplate("employeecontenttemplate.xhtml");
		    	
		    	ec.redirect(context.getContextPath()+"/"+ "/ui/useremployee/employee/employeeHome.jsf");
		    	
			}
		}
	}
    	

	
}