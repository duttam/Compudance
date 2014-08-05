package com.onetouch.model.rest;

import java.util.List;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;

public interface AvailabilityResource {

	public List<Availability> findAll();
	
}
