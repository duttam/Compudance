package com.onetouch.view.bean.admin.location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.menu.MainMenuBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.util.FacesUtils;

 
@ManagedBean(name="regionBean")
@SessionScoped
public class RegionBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Region> regionList;
	private Region selectedRegion;
	private Region loggedInEmployeeRegion;
	private Tenant tenant;
	@ManagedProperty(value="#{supportBean}")
    private SupportBean supportBean;
	@ManagedProperty(value="#{mainMenuBean}")
	private MainMenuBean mainMenuBean;
	private Map<Integer,String> adminUrlMap;
	@PostConstruct
	public void initRegionHome(){
		tenant = tenantContext.getTenant();
		CustomUser customUser=null;
		if(tenant == null){
			UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
			if(authenticationToken!=null && authenticationToken.isAuthenticated()){
				customUser = (CustomUser)authenticationToken.getPrincipal();
				tenant = customUser.getTenant();
				SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
				supportBean.setTenant(tenant);
				tenantContext.setTenant(tenant);
				
			}
			
		}
		if(tenant!=null){
			regionList = getLocationService().getAllRegions(tenant);
			if(regionList.size()>0){
			Integer employeeId = customUser.getEmp_id();
    		loggedInEmployeeRegion = getEmployeeService().findRegionByEmployee(employeeId,tenant.getId());
    		selectedRegion = loggedInEmployeeRegion;
			}
			else{
				
			}
		}
		
		adminUrlMap = new HashMap<Integer, String>();
		adminUrlMap.put(0,"ui/admin/adminHome");
		adminUrlMap.put(1,"ui/admin/event/roster");
		adminUrlMap.put(2,"ui/admin/employee/employeeHome");
		adminUrlMap.put(3,"ui/admin/event/eventHome");
		adminUrlMap.put(4,"ui/admin/location/locationHome");
		adminUrlMap.put(5,"ui/admin/EmployeeSignInOut");
		adminUrlMap.put(6,"ui/admin/reports/reportHome");
		adminUrlMap.put(7,"ui/admin/tools/toolHome");
		adminUrlMap.put(8,"ui/admin/employee/uploadEmployeeHome");
		adminUrlMap.put(9,"ui/admin/email/emailStatus");
	}


	public List<Region> getRegionList() {
		return regionList;
	}


	public void setRegionList(List<Region> regionList) {
		this.regionList = regionList;
	}

	


	public Region getSelectedRegion() {
		return selectedRegion;
	}


	public void setSelectedRegion(Region selectedRegion) {
		this.selectedRegion = selectedRegion;
	}


	public SupportBean getSupportBean() {
		return supportBean;
	}


	public void setSupportBean(SupportBean supportBean) {
		this.supportBean = supportBean;
	}
	// redirect to the page depending on active index
	public void redirectOnRegionChange(){
		String renderKitId = FacesContext.getCurrentInstance().getViewRoot().getRenderKitId();       
        if(renderKitId.equalsIgnoreCase("PRIMEFACES_MOBILE")){
        	FacesUtils.redirectToPage("ui/mobile/admin/adminHome");
        }else{
        	Integer activeMenuIndex = mainMenuBean.getActiveMenuIndex(); 
        		//supportBean.getActiveTabIndex();
        	String url = adminUrlMap.get(activeMenuIndex);
        	FacesUtils.redirectToPage(url);
        }
	}


	public Region getLoggedInEmployeeRegion() {
		return loggedInEmployeeRegion;
	}


	public void setLoggedInEmployeeRegion(Region loggedInEmployeeRegion) {
		this.loggedInEmployeeRegion = loggedInEmployeeRegion;
	}


	public MainMenuBean getMainMenuBean() {
		return mainMenuBean;
	}


	public void setMainMenuBean(MainMenuBean mainMenuBean) {
		this.mainMenuBean = mainMenuBean;
	}
	
}
