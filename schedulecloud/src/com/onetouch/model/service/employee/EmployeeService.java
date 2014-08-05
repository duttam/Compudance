package com.onetouch.model.service.employee;



import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.CustomJdbcUserDetailsManager;
import com.onetouch.model.dao.audit.AuditDAO;
import com.onetouch.model.dao.employee.EmployeeDAO;
import com.onetouch.model.dao.position.PositionDAO;
import com.onetouch.model.dao.schedule.ScheduleDAO;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.EmailGroup;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.SalesPersonLocation;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.util.ApplicationContextProvider;
import com.onetouch.model.util.ApplicationData;
import com.onetouch.model.util.SpringUtil;

@Service
public class EmployeeService implements IEmployeeService{

	@Autowired
	private ScheduleDAO scheduleDAO;
	@Autowired
	EmployeeDAO employeeDAO;
	
	@Autowired
	PositionDAO positionDAO;
	
	@Autowired
	private CustomJdbcUserDetailsManager userDetailManager;
	
	@Autowired
	private ApplicationData applicationData;
	
	@Autowired
	AuditDAO auditDAO;
	
	@Override
	public List<Employee> getAllEmployee(Tenant tenant,String[] employeeStatus,Region region) {
		
		List<Employee> employeeList = employeeDAO.findAll(tenant.getId(),employeeStatus,region.getId());
		
		return employeeList;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Employee inviteEmployee(Employee employee) {
		Integer empId = employeeDAO.save(employee);
		employeeDAO.saveRate(empId,employee.getEmpRate());
		employeeDAO.saveEmployeePositionBatch(empId,employee.getAssignedPositions(),employee.getTenant().getId());
		if(employee.isScSalesPerson()){
			if(employee.getAssignedSalesPersonLocations()!=null){
				List<SalesPersonLocation> salesPersonLocations = new ArrayList<SalesPersonLocation>();
				for(Location location : employee.getAssignedSalesPersonLocations()){
					SalesPersonLocation salesPersonLoc = new SalesPersonLocation(empId, location.getId(),employee.getTenant().getId());
					salesPersonLocations.add(salesPersonLoc);
				}
				
				employeeDAO.saveSalesPersonLocation(empId,employee.getAssignedSalesPersonLocations(),employee.getTenant().getId());
			}
		}
		/*GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SECURED")};
		UserDetails user = new CustomUser(employee.getUserName(), employee.getTemporaryPassword(),true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant());
		userDetailManager.createUser(user);
		userDetailManager.addUserToGroup(user,"ROLE_USER");*/
		String uploadedStatus = employee.getEmployeeUploadStatus();
		if(uploadedStatus!=null && uploadedStatus.equalsIgnoreCase("upload"))
			employeeDAO.updateUploadedEmployeeStatus(employee,"invite");
		employee.setId(empId);
		return employee;
	}

	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Employee editEmployee(boolean isadminView, Employee employee, List<Position> newPositions, List<Position> deletedPositions,List<Location> deleteSalesPersonLocation, List<Location> newSalespersonLocation, List<Location> newRestrictEmpLoc, List<Location> deleteRestrictEmpLoc) {
		String statename = employee.getState().getStateName();
		String statecode = applicationData.getStateCode(statename);
		employee.getState().setStateCode(statecode);
		if(employee.getHomephone()!=null){
			String homephone = ApplicationData.getPhoneNumber(employee.getHomephone());
			employee.setHomephone(homephone);
		}
		if(employee.getCellphone()!=null){
			String cellphone = ApplicationData.getPhoneNumber(employee.getCellphone());
			employee.setCellphone(cellphone);
		}
		if(employee.getFax()!=null){
			String fax = ApplicationData.getPhoneNumber(employee.getFax());
			employee.setFax(fax);
		}
		if(employee.getEmergencyPhone()!=null){
			String emergencyPhone = ApplicationData.getPhoneNumber(employee.getEmergencyPhone());
			employee.setEmergencyPhone(emergencyPhone);
		}
		try {
			employeeDAO.update(isadminView,employee);
			
			if(isadminView){ // only admin can chage rate and permissions
				if(employee.getEmpRate()!=null){
					BigDecimal newrate = employee.getEmpRate().getHourlyRate();
					BigDecimal oldrate = employee.getEmpRate().getOldHourlyRate();
					if(oldrate!=null){
						oldrate = oldrate.setScale(2);
						if(newrate.compareTo(oldrate)!=0){
							employeeDAO.updateRate(employee.getId(),employee.getEmpRate());
							employeeDAO.saveRate(employee.getId(),employee.getEmpRate());
						}
					}else{
						employeeDAO.saveRate(employee.getId(),employee.getEmpRate()); //if for some reasons its null save 
					}
				}
				if(newPositions!=null && newPositions.size()>0)
					employeeDAO.saveEmployeePositionBatch(employee.getId(),newPositions,employee.getTenant().getId());
				if(deletedPositions!=null && deletedPositions.size()>0)
					employeeDAO.deleteEmployeePositionBatch(employee.getId(),deletedPositions,employee.getTenant().getId());
				
				if(deleteSalesPersonLocation!=null && deleteSalesPersonLocation.size()>0)
					employeeDAO.deleteSalesPersonLocation(employee.getId(),deleteSalesPersonLocation,employee.getTenant().getId());
				if(newSalespersonLocation!=null && newSalespersonLocation.size()>0)
					employeeDAO.saveSalesPersonLocation(employee.getId(),newSalespersonLocation,employee.getTenant().getId());
				
				if(newRestrictEmpLoc!=null && newRestrictEmpLoc.size()>0)
					employeeDAO.saveRestrictEmpLocation(employee.getId(),newRestrictEmpLoc,employee.getTenant().getId());
				if(deleteRestrictEmpLoc!=null && deleteRestrictEmpLoc.size()>0)
					employeeDAO.deleteRestrictEmpLocation(employee.getId(),deleteRestrictEmpLoc,employee.getTenant().getId());
				
				String username = userDetailManager.findUsernameByEmployeeId(employee);
				
				/*if(!employee.isOldSCAdmin() && employee.isScAdmin()){
					authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN")};
					UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
					
					userDetailManager.updateUserGroup(user);
					
				}
				if(employee.isOldSCAdmin() && !employee.isScAdmin()){
					authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN")};
					UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
					
					userDetailManager.deleteUserFromGroup(user);
					if(employee.isScManager()){
						GrantedAuthority[] newauthorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_MANAGER")};
						UserDetails newuser = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(newauthorities)),employee.getTenant().getId(),employee.getId());
						
						userDetailManager.addUserToGroup(newuser);
					}else{
						GrantedAuthority[] newauthorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_USER")};
						UserDetails newuser = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(newauthorities)),employee.getTenant().getId(),employee.getId());
						
						userDetailManager.addUserToGroup(newuser);
					}
				}
				if(!employee.isOldSCManager() && employee.isScManager()){
					if(employee.isScAdmin()){
						
					}else{
						authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_MANAGER")};
						UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
						
						userDetailManager.updateUserGroup(user);
					}
					
				}
				if(employee.isOldSCManager() && !employee.isScManager()){
					if(employee.isScAdmin()){
						
					}else{
					authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_MANAGER")};
					UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
					
					userDetailManager.deleteUserFromGroup(user);
					}
				}*/
				// check if employee admin 
				
				if(employee.isScManager()){
					if(!employee.isOldSCManager()){
						GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_MANAGER")};
						UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
						
						userDetailManager.updateUserGroup(user);
						employee.setOldSCManager(true);
					}else{
						
					}
				}else{
					if(employee.isScAdmin()){
						if(!employee.isOldSCAdmin()){
							GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN")};
							UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
							
							userDetailManager.updateUserGroup(user);
							employee.setOldSCAdmin(true);
						}else{
							
						}
					}else{
						GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SECURED")};
						UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
						
						userDetailManager.updateUserGroup(user);
					}
				}
				// check if employee is manager
				if(employee.isScAdmin()){
					if(!employee.isOldSCAdmin()){
						GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN")};
						UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
						
						userDetailManager.updateUserGroup(user);
					}else{
						
					}
				}else{
					if(employee.isScManager()){
						if(!employee.isOldSCManager()){
							GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_MANAGER")};
							UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
							
							userDetailManager.updateUserGroup(user);
						}else{
							
						}
					}else{
						GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SECURED")};
						UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
						
						userDetailManager.updateUserGroup(user);
					}
				}
				if(employee.isScSalesPerson()){
					if(!employee.isOldSCSalesPerson()){
						GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SALES")};
						UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
						userDetailManager.updateUserGroup(user);
						//employee.setOldSCSalesPerson(true);
					}
				}else{
					if(employee.isOldSCSalesPerson()){
						GrantedAuthority[] authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SECURED")};
						UserDetails user = new CustomUser(username, "",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employee.getId());
						userDetailManager.updateUserGroup(user);
						//employee.setOldSCSalesPerson(true);
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updatePassword(String username, String oldPassword, String newPassword ) {
		if(oldPassword!=null && newPassword!=null){
			ShaPasswordEncoder encoder = (ShaPasswordEncoder)ApplicationContextProvider.getSpringManagedBean("passwordEncoder");
	        String encrypted_passwd = encoder.encodePassword(newPassword,username);
	        
			userDetailManager.changePassword(oldPassword, encrypted_passwd);
		}
	}
	/*@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Employee getEmployeeByUsername(String username,Tenant tenant) {
		Employee employee = employeeDAO.selectAllEmployeeWithPositions(username,tenant.getId());//findEmployeeByUsername(username,tenant.getId());
		if(employee.getEmployeeStatus().equalsIgnoreCase("invitesent")){
			
			employeeDAO.updateEmployeeStatus(employee.getId(),"active");
		}
		return employee;
	}*/

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getAllEmployeeAvailability(Tenant tenant, Date eventDate,Region region) {
		
		return employeeDAO.findAvailabilityByEmployee(tenant,eventDate,region.getId());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getAllEmployeeByPosition(Integer positionId,Region region,Integer tenantId) {
		
		return employeeDAO.getAllEmployeeByPosition(positionId, region.getId(),tenantId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Employee getEmplyeeById(Integer id, Tenant tenant) {
		
		Employee employee = employeeDAO.findById(id,tenant.getId());
		employee.setId(id);
		employee.setTenant(tenant);
		String username = userDetailManager.findUsernameByEmployeeId(employee);
		employee.setUserName(username);
		if(employee!=null){
			List<Position> positions = positionDAO.findByEmployee(id, tenant.getId());
			employee.setAssignedPositions(positions);
		}
		if(employee.isScSalesPerson()){
			List<Location> salesPersonLocations = employeeDAO.findSalesPersonLocation(id,tenant.getId());
			employee.setAssignedSalesPersonLocations(salesPersonLocations);
			
		}
		List<Location> restrictEmpLoc = employeeDAO.findRestrictEmployeeLocation(id, tenant.getId());
		employee.setRestrictedLocations(restrictEmpLoc);
		return employee;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void createUser(Employee employee, String username, String password, Tenant tenant) {
		GrantedAuthority[] authorities = null;
		Integer employeeId = employee.getId();
		/*if(employee.isScAdmin()){
			authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SECURED"),new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN")};
			UserDetails user = new CustomUser(username, password,true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),tenant.getId(),employeeId);
			userDetailManager.createUser(user);
			userDetailManager.addUserToGroup(user);
			
		}
		else if(employee.isScManager()){
			authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SECURED"),new SimpleGrantedAuthority("ROLE_ACCESS_MANAGER")};
			UserDetails user = new CustomUser(username, password,true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),tenant.getId(),employeeId);
			userDetailManager.createUser(user);
			userDetailManager.addUserToGroup(user);
			
		}*/
				
		if(employee.isScManager()){
			if(employee.isScAdmin()){
				authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN")};
				UserDetails user = new CustomUser(username, password,true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),tenant.getId(),employeeId);
				userDetailManager.createUser(user);
				userDetailManager.addUserToGroup(user);
				
			}else{
			authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_MANAGER")};
			UserDetails user = new CustomUser(username, password,true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),tenant.getId(),employeeId);
			userDetailManager.createUser(user);
			userDetailManager.addUserToGroup(user);
			}
			
		}else if(employee.isScSalesPerson()){
			//add as sales person
			authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SALES")};
			UserDetails user = new CustomUser(username, password,true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),tenant.getId(),employeeId);
			userDetailManager.createUser(user);
			userDetailManager.addUserToGroup(user);
		}else{
			authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SECURED")};
			UserDetails user = new CustomUser(username, password,true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),tenant.getId(),employeeId);
			userDetailManager.createUser(user);
			userDetailManager.addUserToGroup(user);
		}
		
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Employee getEmplyeePositionById(Integer id, Tenant tenant) {
		Employee employee = employeeDAO.selectAllEmployeeWithPositions(id,tenant.getId());//findEmployeeByUsername(username,tenant.getId());
		if(employee!=null && employee.getEmployeeStatus().equalsIgnoreCase("invitesent")){
			
			employeeDAO.updateEmployeeStatus(employee.getId(),"active");
		}
		return employee;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getOwners(Tenant tenant) {
		return employeeDAO.findAllOwners(tenant.getId());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getCoordinators(Tenant tenant) {
		return employeeDAO.findAllCoordinators(tenant.getId());
		
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public boolean verifyOldPassword(String username, String oldPassword, Integer companyId) {
		ShaPasswordEncoder encoder = (ShaPasswordEncoder)ApplicationContextProvider.getSpringManagedBean("passwordEncoder");
        String encrypted_passwd = encoder.encodePassword(oldPassword,new String(username));
        String actualPassword = userDetailManager.getEncodedPassword(username, companyId);
        if(actualPassword.equals(encrypted_passwd))
        	return true;
        else
        	return false;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public EmployeeRate getEmployeeRateById(Event event,Integer empId, Tenant tenant) {
		return employeeDAO.findEmployeeRateById(event.getStartDate(), empId, tenant.getId());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void saveUploadedEmployees(List<Employee> uploadEmployeeList,Set<String> uploadedPositions) {
		Tenant tenant=null;
		for(Employee employee : uploadEmployeeList){
			State state = employee.getState();
			if(state!=null){
				String statecode = state.getStateCode();
				String statename = state.getStateName();
				if(statecode!=null){
					statename = applicationData.getStateName(statecode);
					state.setStateName(statename);
				}else if(statename!=null){
					statecode = applicationData.getStateCode(statename);
					state.setStateCode(statecode);
				}
				
			}
			tenant = employee.getTenant();
			if(employee.getHomephone()!=null){
				String homephone = ApplicationData.getPhoneNumber(employee.getHomephone());
				employee.setHomephone(homephone);
			}
			if(employee.getCellphone()!=null){
				String cellphone = ApplicationData.getPhoneNumber(employee.getCellphone());
				employee.setCellphone(cellphone);
			}
			if(employee.getFax()!=null){
				String fax = ApplicationData.getPhoneNumber(employee.getFax());
				employee.setFax(fax);
			}
			if(employee.getEmergencyPhone()!=null){
				String emergencyPhone = ApplicationData.getPhoneNumber(employee.getEmergencyPhone());
				employee.setEmergencyPhone(emergencyPhone);
			}
		}
		employeeDAO.saveUploadedEmployees(uploadEmployeeList);//Batch(tenant,uploadEmployeeList);
		if(uploadedPositions!=null && uploadedPositions.size()>0){
			for(String positionName : uploadedPositions){
				Integer id = positionDAO.find(positionName,tenant.getId());
				if(id==null){
					Position position = new Position(tenant);
					position.setName(positionName);
					positionDAO.save(position);
				}
			}
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public List<Employee> getEmployeeByEventPosition(EventPosition eventPosition, Event event, Tenant tenant,Region region) {
		return employeeDAO.findEmployeesByPosition(eventPosition, event,tenant.getId(),region.getId());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void deleteEmployee(Employee employee, Tenant tenant) {
		employeeDAO.deleteEmployee(employee.getId(),tenant.getId());
		userDetailManager.deleteUserByEmpId(tenant.getId(),employee.getId());
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void resetPassword(String username, String tempPassword) {
		userDetailManager.resetTempPassword(username,tempPassword);
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getAllUploadedEmployee(Tenant tenant,Region region) {
		
		return employeeDAO.findAllUploadedEmployee(tenant.getId(),region.getId());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void deleteUploadedEmployee(Employee employee, Tenant tenant) {
		
		employeeDAO.deleteUploadedEmployee(employee.getId(),tenant.getId());
	}

	@Override
	public String getEmployeeUsernameByEmail(String resetPasswdEmail) {
		
		return employeeDAO.findUsernameByEmail(resetPasswdEmail);
	}

	@Override
	public String userExist(Employee employee) {
		return userDetailManager.findUsernameByEmployeeId(employee);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateInviteEmployee(Employee employee,List<Position> newPositions, List<Position> deletedPositions) {
		
		employeeDAO.updateInvite(employee);
		employeeDAO.updateRate(employee.getId(),employee.getEmpRate());
		if(newPositions!=null && newPositions.size()>0)
			employeeDAO.saveEmployeePositionBatch(employee.getId(),newPositions,employee.getTenant().getId());
		if(deletedPositions!=null && deletedPositions.size()>0)
			employeeDAO.deleteEmployeePositionBatch(employee.getId(),deletedPositions,employee.getTenant().getId());
		
		
	
	}

	@Override
	public Region findRegionByEmployee(Integer employeeId, Integer id) {
		return employeeDAO.findRegionById(employeeId,id);
	}

	@Override
	public List<Employee> getAllEmployeeByGroup(EmailGroup emailGroup) {
		return employeeDAO.findAllByEmailGroup(emailGroup);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public EmailGroup addEmailGroup(EmailGroup emailGroup) {
		Integer emailGroupId = employeeDAO.saveEmailGroup(emailGroup);
		employeeDAO.SaveEmployeeEmailGroup(emailGroup.getEmployees(),emailGroupId,emailGroup.getCompanyId());
		emailGroup.setId(emailGroupId);
		return emailGroup;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getAllEmployeesByRegionAndPosition(
			List<Position> positions, List<Region> regions, Tenant tenant) {
		 List<Integer> positionIds = new ArrayList<Integer>();
		 for(Position position : positions){
			 positionIds.add(position.getId());
		 }
		 List<Integer> regionIds = new ArrayList<Integer>();
		 for(Region region : regions){
			 regionIds.add(region.getId());
		 }
		return employeeDAO.findAllEmployeesByRegionAndPosition(positionIds,regionIds,tenant);
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public boolean isUsernameAlreadyExist(String username){
		UserDetails user = userDetailManager.loadUserByUsername(username);
		if(user.isEnabled())
			return true;
		else
			return false;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Employee> getAllOverrideEmployeeByPosition(Event event,EventPosition eventPosition ,Region selectedRegion, Tenant tenant) {
		Integer locationId = event.getLocation().getId();
		Integer positionId = eventPosition.getPosition().getId();
		Integer eventPositionId = eventPosition.getId();
		Integer tenantId = tenant.getId();
		
		List<Employee> allEmployeeByPosition = employeeDAO.getOverrideEmployeeByPosition(locationId,positionId, selectedRegion.getId(),tenantId);
		List<Employee> allScheduledEmployeesByPosition = scheduleDAO.findScheduledEmployeeByEventPosition(eventPositionId,selectedRegion.getId(),tenantId);
		allEmployeeByPosition.removeAll(allScheduledEmployeesByPosition);
		
		return allEmployeeByPosition;
	}

	@Override
	public List<Employee> getSalesPersons(Location location, Tenant tenant) {
		return employeeDAO.findAllSalesPersons(location, tenant);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void saveEmployee(Employee employee) {
		String statename = employee.getState().getStateName();
		String statecode = applicationData.getStateCode(statename);
		employee.getState().setStateCode(statecode);
		if(employee.getHomephone()!=null){
			String homephone = ApplicationData.getPhoneNumber(employee.getHomephone());
			employee.setHomephone(homephone);
		}
		if(employee.getCellphone()!=null){
			String cellphone = ApplicationData.getPhoneNumber(employee.getCellphone());
			employee.setCellphone(cellphone);
		}
		if(employee.getFax()!=null){
			String fax = ApplicationData.getPhoneNumber(employee.getFax());
			employee.setFax(fax);
		}
		if(employee.getEmergencyPhone()!=null){
			String emergencyPhone = ApplicationData.getPhoneNumber(employee.getEmergencyPhone());
			employee.setEmergencyPhone(emergencyPhone);
		}
		
		Integer employeeId = employeeDAO.save(employee);
		employeeDAO.saveRate(employeeId,employee.getEmpRate());
		employeeDAO.saveEmployeePositionBatch(employeeId,employee.getAssignedPositions(),employee.getTenant().getId());
		if(employee.isScSalesPerson()){
			if(employee.getAssignedSalesPersonLocations()!=null){
				List<SalesPersonLocation> salesPersonLocations = new ArrayList<SalesPersonLocation>();
				for(Location location : employee.getAssignedSalesPersonLocations()){
					SalesPersonLocation salesPersonLoc = new SalesPersonLocation(employeeId, location.getId(),employee.getTenant().getId());
					salesPersonLocations.add(salesPersonLoc);
				}
				
				employeeDAO.saveSalesPersonLocation(employeeId,employee.getAssignedSalesPersonLocations(),employee.getTenant().getId());
			}
		}
		if(employee.isCreateUserAccount()){
			GrantedAuthority[] authorities = null;
			
					
			if(employee.isScManager() || employee.isScAdmin()){
				if(employee.isScAdmin()){
					authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN")};
					UserDetails user = new CustomUser(employee.getUserName(), "schedule",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employeeId);
					userDetailManager.createUser(user);
					userDetailManager.addUserToGroup(user);
					
				}else{
				authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_MANAGER")};
				UserDetails user = new CustomUser(employee.getUserName(), "schedule",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employeeId);
				userDetailManager.createUser(user);
				userDetailManager.addUserToGroup(user);
				}
				
			}else if(employee.isScSalesPerson()){
				//add as sales person
				authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SALES")};
				UserDetails user = new CustomUser(employee.getUserName(), "schedule",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employeeId);
				userDetailManager.createUser(user);
				userDetailManager.addUserToGroup(user);
			}else{
				authorities = new SimpleGrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ACCESS_SECURED")};
				UserDetails user = new CustomUser(employee.getUserName(), "schedule",true,true,true,true, new ArrayList<GrantedAuthority>(Arrays.asList(authorities)),employee.getTenant().getId(),employeeId);
				userDetailManager.createUser(user);
				userDetailManager.addUserToGroup(user);
			}
			
		}
			
	}
	
}
