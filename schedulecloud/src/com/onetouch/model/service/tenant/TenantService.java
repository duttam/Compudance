package com.onetouch.model.service.tenant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.CustomJdbcUserDetailsManager;
import com.onetouch.model.dao.employee.EmployeeDAO;
import com.onetouch.model.dao.location.LocationDAO;
import com.onetouch.model.dao.position.PositionDAO;
import com.onetouch.model.dao.tenant.TenantRepository;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TenantDocument;
import com.onetouch.model.util.ApplicationData;

@Service
public class TenantService implements ITenantService{

	@Autowired
	private TenantRepository tenantRepository;
	@Autowired
	private ApplicationData applicationData;
	@Autowired
	private EmployeeDAO employeeDAO;
	
	@Autowired
	private PositionDAO positionDAO;
	
	@Autowired
	private LocationDAO locationDAO;
	
	private Map<String, Tenant> tenantMap=null;
	
	@Autowired
	@Qualifier("userDetailsService")
	private CustomJdbcUserDetailsManager userDetailManager;
	@Override
	public synchronized Tenant getTenant(String tenantCode, boolean refresh){
		List<Tenant> tenants = tenantRepository.getTenants(refresh);
		
		if(tenants!=null && tenants.size()>0){
			if(tenantMap!=null)
				tenantMap.clear();
			tenantMap = new HashMap<String, Tenant>();
			
			for(Tenant tenant : tenants){
				tenantMap.put(tenant.getCode(),tenant);
				tenantMap.put(tenant.getCode(),tenant);
			}
		}
		if(tenantMap!=null){
			return tenantMap.get(tenantCode);
		}
		
		return null;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void addTenant(Tenant tenant,Region region) {
		String statename = tenant.getState().getStateName();
		String statecode = applicationData.getStateCode(statename);
		tenant.getState().setStateCode(statecode);
		if(tenant.getBusinessPhone()!=null){
			String businessPhone = ApplicationData.getPhoneNumber(tenant.getBusinessPhone());
			tenant.setBusinessPhone(businessPhone);
		}
		
		if(tenant.getFax()!=null){
			String fax = ApplicationData.getPhoneNumber(tenant.getFax());
			tenant.setFax(fax);
		}
		tenant.setActive(true);
		if(tenant.getTheme()==null)
			tenant.setTheme("cupertino");
		if(tenant.getLogoUrl()==null)
			tenant.setLogoUrl("logo.jpg");
		Integer tenantId = tenantRepository.save(tenant);
		tenant.setId(tenantId);
		region.setTenant(tenant);
		Integer regionId = locationDAO.saveRegion(region);
		region.setId(regionId);
		Employee adminEmployee = tenant.getAdminEmployee();
		adminEmployee.setTenant(tenant);
		adminEmployee.setRegion(region);
		Integer employeeId= employeeDAO.save(adminEmployee);
		employeeDAO.updateEmployeeStatus(employeeId,"active");
		/*List<Position> positions = new ArrayList<Position>();
		Position adminPosition = new Position(tenant);
		adminPosition.setName("SCADMIN");
		Position managerPosition = new Position(tenant);
		managerPosition.setName("SCMANAGER");
		positions.add(adminPosition);
		positions.add(managerPosition);
		positionDAO.saveBatchPositions(positions);*/
		String username = tenant.getUsername();
		String password = tenant.getPassword();
		//GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SECURED"),new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN")};
		GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN")};
		UserDetails user = new CustomUser(username,password ,true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),tenantId,employeeId);
		userDetailManager.createUser(user);
		userDetailManager.addUserToGroup(user);
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Tenant findTenant(Integer tenantId) {
		return tenantRepository.getTenant(tenantId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Tenant> getAllTenants() {
		
		return tenantRepository.findAll();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateTenant(Tenant company) {
		tenantRepository.update(company);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void disableTenant(Tenant company) {
		tenantRepository.disableCompany(company);
		userDetailManager.deleteUser(company.getUsername());
		List<UserDetails> allUsers = userDetailManager.findAllUsers(company.getId());
		for(UserDetails user : allUsers){
			CustomUser customUser = (CustomUser)user;
			//employeeDAO.deleteEmployee(customUser.getEmp_id(), company.getId()); 
			userDetailManager.deleteUser(user.getUsername());
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void savePolicyNotes(Tenant tenant) {
		tenantRepository.savePolicyNotes(tenant);
	}

	
	
	
}
