package com.onetouch.view.context;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.onetouch.model.domainobject.Tenant;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {
	
	@Override
	protected Object determineCurrentLookupKey() {
		Tenant tenant = (Tenant)TenantContextUtil.getTenant();
		String tenantDataSourceKey =  null;
		if(tenant!=null)
			tenantDataSourceKey = tenant.getCode();
		if(tenantDataSourceKey!=null){
			String firstLetter = tenantDataSourceKey.substring(0,1);
			if(firstLetter.compareTo("n")<0)
				tenantDataSourceKey = "dfyoung"; // either first half
			else
				tenantDataSourceKey = "superior"; // last half
		}
			
		return tenantDataSourceKey;
	}
}