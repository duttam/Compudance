package com.onetouch.model.rest;

import java.util.ArrayList;
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

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.service.employee.IEmployeeService;
import com.onetouch.model.service.tenant.ITenantService;
import com.onetouch.view.context.TenantContext;
@Component
@Path("/employeeservice")
@Scope("request")
public class RestEmployeeResource implements EmployeeResource{

	@Autowired
	private IEmployeeService employeeService;
	
	@Autowired
	private ITenantService tenantService;
	
	@ManagedProperty(value="#{tenantContext}")
	protected TenantContext tenantContext;
	@Override
	@GET 
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<Employee> findAll() {
		/*Tenant tenant = tenantService.getTenant(tenantCode, true);
		if(tenant !=null)
			return employeeService.getAllEmployee(tenant);
		else*/
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		CustomUser customUser = null;
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
			
		}
		Tenant tenant = customUser.getTenant();
		 
		List<Employee> employees = employeeService.getAllEmployee(tenant, new String[]{"active"},new Region(new Tenant(8)));
		/*List<Employee> employees = new ArrayList<Employee>();
		employees.add(new Employee("john","luludis"));*/
		return employees;
		
	}

	
	

}
