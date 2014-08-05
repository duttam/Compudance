package com.onetouch.view.bean.admin.location;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;

@ManagedBean(name="locationSupport")
@ViewScoped
public class LocationSupport extends BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Location> locations;
	@PostConstruct
	public void initEventSupport(){
		Tenant tenant = tenantContext.getTenant();// get company detail from security context
		
		//locations = getLocationService().getAllLocation(tenant);
		
	}
	public List<Location> getLocations() {
		return locations;
	}
}