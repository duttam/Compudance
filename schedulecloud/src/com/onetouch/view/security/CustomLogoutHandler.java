package com.onetouch.view.security;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

public class CustomLogoutHandler extends SimpleUrlAuthenticationSuccessHandler implements LogoutSuccessHandler {
    	

	private String targetUrl = "";
	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		if(authentication!=null){
			
		/*List<String> roles = getAuthorityList(authentication.getAuthorities());
        
		if(roles.contains("ROLE_ACCESS_SECURED")){
			targetUrl = "/superior/ui/login/login.jsf";
		}
		if(roles.contains("ROLE_ACCESS_ADMIN")){
			targetUrl = "/dfyoung/ui/login/login.jsf";
			
		}*/
			
			CustomUser customUser = (CustomUser)authentication.getPrincipal();
			
			targetUrl = customUser.getTenant().getLogoutUrl();
		}
		super.handle(request, response, authentication);
	}
    
	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		return targetUrl;
	}
	
	private List<String> getAuthorityList(
			Collection<? extends GrantedAuthority> authorities) {
		List<String> grantedAuthorities = new ArrayList<String>();
		for(GrantedAuthority authority : authorities){
			grantedAuthorities.add(authority.getAuthority());
		}

		return grantedAuthorities;
	}
}