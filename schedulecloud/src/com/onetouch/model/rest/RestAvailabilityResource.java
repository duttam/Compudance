package com.onetouch.model.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedProperty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.service.availability.IEmployeeAvailability;
import com.onetouch.model.service.employee.IEmployeeService;
import com.onetouch.model.service.tenant.ITenantService;
import com.onetouch.view.context.TenantContext;
@Component
@Path("/availabilityservice")
@Scope("request")
public class RestAvailabilityResource implements AvailabilityResource{

	@Autowired
	private IEmployeeAvailability employeeAvailability;
	
	
	@Override
	@GET 
	@Produces({ MediaType.APPLICATION_JSON})
	public List<Availability> findAll() {
		long startTime = System.currentTimeMillis();
		List<Availability> list =  null;//employeeAvailability.getAllAvailability(new Tenant(3),new Employee(52), new Date(),new Date());
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.print(totalTime);
		
		return list;
	}

	
	

}
