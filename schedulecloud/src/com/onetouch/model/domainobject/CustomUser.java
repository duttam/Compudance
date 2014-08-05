package com.onetouch.model.domainobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUser extends User implements CustomUserDetail{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Tenant tenant;
	private Integer emp_id;
	private Integer tenantId;
	private String logoutUrl;
	
	
	public CustomUser(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities,Integer tenantId, Integer emp_id,Tenant tenant) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.tenantId = tenantId;
		this.emp_id = emp_id;
		this.tenant = tenant;
	}
	public CustomUser(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities,Integer tenantId, Integer emp_id) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.tenantId = tenantId;
		this.emp_id = emp_id;
		
	}
	public CustomUser(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities,Tenant tenant, Integer emp_id) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.tenant = tenant;
		this.emp_id = emp_id;
	}
	public CustomUser(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		
	}
	public CustomUser(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities,Integer emp_id) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.emp_id = emp_id;
	}
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	@Override
	public Integer getTenantId() {
		
		return tenantId;
	}

	public Integer getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(Integer emp_id) {
		this.emp_id = emp_id;
	}
	public String getLogoutUrl() {
		return logoutUrl;
	}
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	
	
    
	
	
	private List<String> getAuthorityList(
			Collection<? extends GrantedAuthority> authorities) {
		List<String> grantedAuthorities = new ArrayList<String>();
		for(GrantedAuthority authority : authorities){
			grantedAuthorities.add(authority.getAuthority());
		}

		return grantedAuthorities;
	}
	public boolean isAdmin(){
		List<String> roles = getAuthorityList(this.getAuthorities());
		if(roles.contains("ROLE_ACCESS_ADMIN"))
			return true;
		else
			return false;
	}
	public boolean isManager(){
		List<String> roles = getAuthorityList(this.getAuthorities());
		if(roles.contains("ROLE_ACCESS_MANAGER"))
			return true;
		else
			return false;
	}
	public boolean isSalesPerson(){
		List<String> roles = getAuthorityList(this.getAuthorities());
		if(roles.contains("ROLE_ACCESS_SALES"))
			return true;
		else
			return false;
	}
}
