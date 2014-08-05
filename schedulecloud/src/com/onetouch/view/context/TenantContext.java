package com.onetouch.view.context;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Tenant;



public class TenantContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Tenant tenant;
	

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	
	
	
	
	
}
