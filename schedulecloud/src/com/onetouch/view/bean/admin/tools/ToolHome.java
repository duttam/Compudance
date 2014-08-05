package com.onetouch.view.bean.admin.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DualListModel;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.context.TenantContext;
import com.onetouch.view.util.FacesUtils;
@ManagedBean(name="toolHome")
@SessionScoped
public class ToolHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Tenant tenant;
	@PostConstruct
	public void initToolHome(){
		tenant = tenantContext.getTenant();
	}
	
	public void onTabChange(TabChangeEvent event){
	    
	    
	        if ( event.getTab().getId().equalsIgnoreCase( "positionHome" ) ){
	        
	        }
	        if ( event.getTab().getId().equalsIgnoreCase( "shiftHome" ) ){
		        
	        }
	 }
	
	
}
