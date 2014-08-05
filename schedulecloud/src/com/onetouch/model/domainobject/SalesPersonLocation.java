package com.onetouch.model.domainobject;

import java.io.Serializable;

public class SalesPersonLocation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer employeeId;
	private Integer locationId;
	private Integer companyId;
	public SalesPersonLocation(Integer employeeId, Integer locationId,
			Integer companyId) {
		super();
		this.employeeId = employeeId;
		this.locationId = locationId;
		this.companyId = companyId;
	}
	public Integer getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	
	

}
