package com.onetouch.view.security;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.firewall.FirewalledRequest;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.filter.TenantAwareHttpServletRequest;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.UAgentInfo;



public class CustomTargetUrlResolver implements AuthenticationSuccessHandler {

   
	private String employeeSuccessUrl;
    private String adminSuccessUrl;
    private String salesSuccessUrl;
    private String superAdminSuccessUrl;
    private String defaultTargetUrl;
    private String currentHomeUrl;
	
    // mobile success urls
    
    private String mobileAdminSuccessUrl;
    private String mobileSalesSuccessUrl;
    private String mobileEmployeeSuccessUrl;
    
    
	public String getMobileAdminSuccessUrl() {
		return mobileAdminSuccessUrl;
	}

	public void setMobileAdminSuccessUrl(String mobileAdminSuccessUrl) {
		this.mobileAdminSuccessUrl = mobileAdminSuccessUrl;
	}
	
	public String getMobileSalesSuccessUrl() {
		return mobileSalesSuccessUrl;
	}

	public void setMobileSalesSuccessUrl(String mobileSalesSuccessUrl) {
		this.mobileSalesSuccessUrl = mobileSalesSuccessUrl;
	}

	public String getMobileEmployeeSuccessUrl() {
		return mobileEmployeeSuccessUrl;
	}

	public void setMobileEmployeeSuccessUrl(String mobileEmployeeSuccessUrl) {
		this.mobileEmployeeSuccessUrl = mobileEmployeeSuccessUrl;
	}

	public String getEmployeeSuccessUrl() {
		return employeeSuccessUrl;
	}

	public void setEmployeeSuccessUrl(String employeeSuccessUrl) {
		this.employeeSuccessUrl = employeeSuccessUrl;
	}

	public String getAdminSuccessUrl() {
		return adminSuccessUrl;
	}

	public void setAdminSuccessUrl(String adminSuccessUrl) {
		this.adminSuccessUrl = adminSuccessUrl;
	}

	public String getDefaultTargetUrl() {
		return defaultTargetUrl;
	}

	public void setDefaultTargetUrl(String defaultTargetUrl) {
		this.defaultTargetUrl = defaultTargetUrl;
	}

	public String getSuperAdminSuccessUrl() {
		return superAdminSuccessUrl;
	}

	public void setSuperAdminSuccessUrl(String superAdminSuccessUrl) {
		this.superAdminSuccessUrl = superAdminSuccessUrl;
	}
	
	public String getSalesSuccessUrl() {
		return salesSuccessUrl;
	}

	public void setSalesSuccessUrl(String salesSuccessUrl) {
		this.salesSuccessUrl = salesSuccessUrl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		// user agent detection for mobile browsers
		String userAgent = request.getHeader("User-Agent");
		String accept = request.getHeader("Accept");
		if (userAgent != null && accept != null){
			
		    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ServletContext context = FacesUtils.getServletContext();
			CustomUser user = (CustomUser)authentication.getPrincipal();
			List<String> roles = getAuthorityList(authentication.getAuthorities());
			SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
			UAgentInfo agent = new UAgentInfo(userAgent, accept);
			if (agent.isMobileDevice()){
				for(String role : roles){
					if (role.equalsIgnoreCase("ROLE_ACCESS_ADMIN") || role.equalsIgnoreCase("ROLE_ACCESS_MANAGER") || role.equalsIgnoreCase("ROLE_ACCESS_SUPERADMIN")) {
						this.currentHomeUrl = "/"+ getMobileAdminSuccessUrl();
						String targetUrl = context.getContextPath()+"/"+ getMobileAdminSuccessUrl();
			           
			            ec.redirect(targetUrl);
			            break;
		            
		            }else if (role.equalsIgnoreCase("ROLE_ACCESS_SALES")) {
		            	this.currentHomeUrl = "/"+ getMobileSalesSuccessUrl();
						String targetUrl = context.getContextPath()+"/"+ getMobileSalesSuccessUrl();
			            
			            ec.redirect(targetUrl);
			            break;
		            }
					else if (role.equalsIgnoreCase("ROLE_ACCESS_SECURED")) {
		            	this.currentHomeUrl = "/"+ getMobileEmployeeSuccessUrl();
		            	ec.redirect(context.getContextPath()+"/"+ getMobileEmployeeSuccessUrl());
		            	
		            	break;
		            
		            }else{
		            	supportBean.setHomeUrlTemplate("employeecontenttemplate.xhtml");
		            	ec.redirect(context.getContextPath()+"/"+ getMobileEmployeeSuccessUrl());
		            	
		            	
		            }
		            
				}
			}else{
				
				for(String role : roles){
					if (role.equalsIgnoreCase("ROLE_ACCESS_ADMIN")) {
						this.currentHomeUrl = "/"+ getAdminSuccessUrl();
			            supportBean.setHomeUrlTemplate("admincontenttemplate.xhtml");
			            ec.redirect(context.getContextPath()+"/"+ getAdminSuccessUrl());
						//response.sendRedirect(context.getContextPath()+"/"+ getAdminSuccessUrl());
			            
			            break;
		            }else if (role.equalsIgnoreCase("ROLE_ACCESS_MANAGER")) {
		            	this.currentHomeUrl = "/"+ getEmployeeSuccessUrl();
		            	supportBean.setHomeUrlTemplate("admincontenttemplate.xhtml");
		            	response.sendRedirect(context.getContextPath()+"/"+ getAdminSuccessUrl());
		            	break;
		            }else if (role.equalsIgnoreCase("ROLE_ACCESS_SALES")) {
		            	this.currentHomeUrl = "/"+ getSalesSuccessUrl();
		            	supportBean.setHomeUrlTemplate("admincontenttemplate.xhtml");
		            	response.sendRedirect(context.getContextPath()+"/"+ getSalesSuccessUrl());
		            	break;
		            }else if (role.equalsIgnoreCase("ROLE_ACCESS_SECURED")) {
		            	this.currentHomeUrl = "/"+ getEmployeeSuccessUrl();
		            	supportBean.setHomeUrlTemplate("employeecontenttemplate.xhtml");
		            	response.sendRedirect(context.getContextPath()+"/"+ getEmployeeSuccessUrl());
		            	break;
		            } else if(role.equalsIgnoreCase("ROLE_ACCESS_SUPERADMIN")){
		            	this.currentHomeUrl = "/"+ getSuperAdminSuccessUrl();
		            	supportBean.setHomeUrlTemplate("admincontenttemplate.xhtml");
		            	response.sendRedirect(context.getContextPath()+"/"+getSuperAdminSuccessUrl());
		            	break;
		            }else{
		            	supportBean.setHomeUrlTemplate("employeecontenttemplate.xhtml");
		            	response.sendRedirect(context.getContextPath()+"/"+getDefaultTargetUrl());
		            	
		            }
		            
				}
				
				
			}
			supportBean.setHomeUrl(currentHomeUrl);
			//boolean test = agent.detectIpod();
			//System.out.print(test);
			
		}
		
		
		//LoginBean lb =  (LoginBean)FacesUtils.getManagedBean("loginBean");
		
		
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