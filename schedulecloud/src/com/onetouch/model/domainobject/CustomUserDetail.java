package com.onetouch.model.domainobject;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetail extends UserDetails{

	/**
     * Returns the tenant id used to authenticate the user. Cannot return <code>null</code>.
     *
     * @return the tenant id (never <code>null</code>)
     */
	Integer getTenantId();
	Integer getEmp_id();
	Tenant getTenant();
	void setTenant(Tenant tenant);
	boolean isAdmin();
	boolean isManager();
}
