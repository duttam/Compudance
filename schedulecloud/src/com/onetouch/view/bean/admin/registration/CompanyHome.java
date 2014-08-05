package com.onetouch.view.bean.admin.registration;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="companyHome")
@ViewScoped
public class CompanyHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Tenant tenant;
	private List<Tenant> companyList;
	@PostConstruct
	public void initTenantHome(){
		tenant = tenantContext.getTenant();
		CustomUser customUser=null;
		if(tenant == null){
			UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
			if(authenticationToken.isAuthenticated())
				customUser = (CustomUser)authenticationToken.getPrincipal();
			Integer tenantId = customUser.getTenantId();
			tenant = getTenantService().findTenant(tenantId);
			SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
			supportBean.setTenant(tenant);
			tenantContext.setTenant(tenant);
		}
		
		companyList = getTenantService().getAllTenants();
	}



	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public List<Tenant> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<Tenant> companyList) {
		this.companyList = companyList;
	}
	
	
}
