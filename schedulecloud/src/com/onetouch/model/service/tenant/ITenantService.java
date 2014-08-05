package com.onetouch.model.service.tenant;

import java.util.List;
import java.util.Map;

import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TenantDocument;

public interface ITenantService {

	public Tenant getTenant(String tenantName, boolean refresh);
	public void addTenant(Tenant tenant, Region region);
	public Tenant findTenant(Integer tenantId);
	public List<Tenant> getAllTenants();
	public void updateTenant(Tenant company);
	public void disableTenant(Tenant company);
	public void savePolicyNotes(Tenant tenant);
	
}
