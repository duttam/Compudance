package com.onetouch.view.bean.menu;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.exception.OneTouchDataAccessException;
import com.onetouch.view.bean.BaseBean;

import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.util.FacesUtils;



@ManagedBean(name="mainMenuBean")
@SessionScoped
public class MainMenuBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private Integer activeMenuIndex;
	private Integer eventActiveTabIndex;
	private String url;
	private Tenant tenant;
    private Integer menuId;
	private CustomUser customUser;

	
	public MainMenuBean(){
		activeMenuIndex = 0;
		
		if(tenantContext!=null)
			tenant = tenantContext.getTenant();
		if(tenant == null){
			UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
			if(authenticationToken.isAuthenticated()){
				customUser = (CustomUser)authenticationToken.getPrincipal();
				
			}
			tenant = customUser.getTenant();
			
			if(tenantContext!=null){
				tenantContext.setTenant(tenant);
				
			}
		}
	}
	@PostConstruct
	public void init(){
		activeMenuIndex = 0;
	}
	@PreDestroy
	public void destroy()
	{
		System.out.println("MainMenuBean Destroy");
	}
	public String adminMenuItemClicked(String menuid){
		
		if(menuid.equalsIgnoreCase("adminhome")){
			this.activeMenuIndex = 0;
			
			return "/ui/admin/adminHome?faces-redirect=true";
		}
		
		else if(menuid.equalsIgnoreCase("rooster")){
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        	String startDate = dateformat.format(new Date());
			String endDate = dateformat.format(new Date());
			String contextRoot = FacesUtils.getServletContext().getContextPath();
			this.activeMenuIndex = 1;
			
			return "/ui/admin/event/roster.jsf?faces-redirect=true&startDate="+startDate+"&endDate="+endDate;
			
			
			
		}
		else if(menuid.equalsIgnoreCase("employee")){
			this.activeMenuIndex = 2;
			
			return "/ui/admin/employee/employeeHome?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("event")){
			if(customUser.isSalesPerson()){
				this.activeMenuIndex = 2;
				
			}
			else{
				this.activeMenuIndex = 3;
				
			}
			this.eventActiveTabIndex = 0;
			
			return "/ui/admin/event/eventHome?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("location")){
			
			if(customUser.isSalesPerson()){
				this.activeMenuIndex = 3;
				
			}
			else{
				this.activeMenuIndex = 4;
				
			}
			return "/ui/admin/location/locationHome?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("signinout")){
			
			this.activeMenuIndex = 5;
			return "/ui/admin/EmployeeSignInOut?faces-redirect=true";
			
		}
		
		else if(menuid.equalsIgnoreCase("reports")){
			
			this.activeMenuIndex = 6;
			return "/ui/admin/reports/reportHome?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("tools")){
			if(customUser.isSalesPerson()){
				this.activeMenuIndex = 4;
				
			}
			else{
				this.activeMenuIndex = 7;
				
			}
			return "/ui/admin/tools/toolHome?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("uploademployee")){
			this.activeMenuIndex = 8;
			
			return "/ui/admin/employee/uploadEmployeeHome?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("emailstatus")){
			this.activeMenuIndex = 9;
			
			return "/ui/admin/email/emailStatus?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("newcompany")){
			this.activeMenuIndex = 10;
			
			return "/ui/admin/registration/companyHome?faces-redirect=true";
			
		}
		else
			return "";
	}
	
	
	public String employeeMenuItemClicked(String menuid){
		
		if(menuid.equalsIgnoreCase("employeeHome")){
			this.activeMenuIndex = 0;
			
			return "/ui/useremployee/employee/employeeHome?faces-redirect=true";
		}
		else if(menuid.equalsIgnoreCase("schedule")){
			this.activeMenuIndex = 1;
			
			return "/ui/useremployee/schedule/scheduleHome?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("availability")){
			this.activeMenuIndex = 2;
			
			return "/ui/useremployee/availability/availabilityHome?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("notification")){
			this.activeMenuIndex = 3;
			
			return "/ui/useremployee/notification/notificationHome?faces-redirect=true";
		}
		else if(menuid.equalsIgnoreCase("document")){
			this.activeMenuIndex = 4;
			
			return "/ui/useremployee/document/downloadDocument?faces-redirect=true";
			
		}
		else if(menuid.equalsIgnoreCase("request")){
			this.activeMenuIndex = 5;
			
			return "/ui/useremployee/request/requestHome?faces-redirect=true";
			
		}
		else
			return "";
		
	}
	
	
	public Integer getActiveMenuIndex() {
		return activeMenuIndex;
	}

	public void setActiveMenuIndex(Integer activeMenuIndex) {
		this.activeMenuIndex = activeMenuIndex;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public Integer getMenuId() {
		return menuId;
	}


	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	public Integer getEventActiveTabIndex() {
		return eventActiveTabIndex;
	}
	public void setEventActiveTabIndex(Integer eventActiveTabIndex) {
		this.eventActiveTabIndex = eventActiveTabIndex;
	}


	

	
	

		
}
