package com.onetouch.model.service.availability;

import java.util.Date;
import java.util.List;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;

public interface IEmployeeAvailability {

	//public List<Availability> getAllAvailability(Tenant tenant, Employee employee, Date start, Date end);
	public List<Availability> getAllAvailability(Tenant tenant, Employee employee);
	public List<Availability> addAvailability(List<Availability> availability);
	
	public void updateAvailability(Availability availability);

	public Availability getAvailability(Integer availId, Employee employee,
			Tenant tenant);
	
	public void deleteAvailability(Integer availId, Employee employee,Tenant tenant);

	public boolean overlapAvailability(Availability availability,
			Employee employee, Tenant tenant);
	public List<Availability> getAllAvailabilityByDate(Tenant tenant, Employee employee, Date date);
}
