package com.onetouch.view.bean.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;

import com.onetouch.view.bean.BaseBean;

@ManagedBean(name="supportBean")
@SessionScoped
public class SupportBean extends BaseBean{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String theme = "cupertino";
	private String logo = "logo.jpg";
	private Tenant tenant;
	private String homeUrl="";
	private String homeUrlTemplate = "";
	private Employee currentLoggedEmployee;

	@PostConstruct
	public void initSupportBean(){
		tenant = new Tenant();
		tenant.setTheme(this.theme);
		tenant.setLogoUrl(this.logo);

	}
	
	public String getTenantTheme(){
		
		return tenant.getTheme();
	}
	
	public String getTenantLogo(){
		return "/images/"+tenant.getLogoUrl();
		//return "http://localhost:8181/OneTouch/dfyoung/images/"+logo;getApplicationData().getImage_url()+
	}
	public List<String> getStates() {
        List<String> stateNames = new ArrayList<String>(getApplicationData().getStates().keySet());
        Collections.sort(stateNames, new Comparator<String>() {

                @Override
                public int compare(String state1, String state2) {
                        return state1.compareToIgnoreCase(state2);
                }
        });

        return stateNames; 
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public Employee getCurrentLoggedEmployee() {
		return currentLoggedEmployee;
	}

	public void setCurrentLoggedEmployee(Employee currentLoggedEmployee) {
		this.currentLoggedEmployee = currentLoggedEmployee;
	}

	public String getHomeUrlTemplate() {
		return homeUrlTemplate;
	}

	public void setHomeUrlTemplate(String homeUrlTemplate) {
		this.homeUrlTemplate = homeUrlTemplate;
	}

	

	
		
}
