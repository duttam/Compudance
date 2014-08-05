package com.onetouch.model.rest;

import java.util.List;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;

public interface EmployeeResource {

	public List<Employee> findAll();
	
}
