package com.onetouch.model.service.employee;

import java.util.Date;
import java.util.List;
import java.util.Set;


import com.onetouch.model.domainobject.EmailGroup;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;

public interface IEmployeeService {

	/**
	 * 
	 * @param company
	 * @return all employees for the company
	 */
	public List<Employee> getAllEmployee(Tenant tenant,String[] employeeStatus, Region region );
	
	public Employee inviteEmployee(Employee employee);
	
	public Employee editEmployee(boolean isadminView, Employee employee, List<Position> newSelectedPositions, List<Position> deletedPositions, List<Location> deleteSalesPersonLocation, List<Location> newSalespersonLocation, List<Location> newRestrictEmpLoc, List<Location> deleteRestrictEmpLoc);

	//public Employee getEmployeeByUsername(String username,Tenant tenant);

	public List<Employee> getAllEmployeeByPosition(Integer positionId, Region region,Integer tenantId);
	public List<Employee> getAllEmployeeAvailability(Tenant tenant, Date eventDate,Region region);

	public Employee getEmplyeeById(Integer id, Tenant tenant);
	public Employee getEmplyeePositionById(Integer id, Tenant tenant);
	public void createUser(Employee employee, String username, String password, Tenant tenant);

	public List<Employee> getOwners(Tenant tenant);

	public List<Employee> getCoordinators(Tenant tenant);
	public boolean verifyOldPassword(String username, String oldPassword, Integer companyId);
	public void updatePassword(String username, String oldPassword, String newPassword);

	public EmployeeRate getEmployeeRateById(Event event,Integer empId, Tenant tenant);

	public void saveUploadedEmployees(List<Employee> uploadEmployeeList, Set<String> uploadedPositions);
	public List<Employee> getEmployeeByEventPosition(EventPosition eventPosition, Event event, Tenant tenant, Region region);

	public void deleteEmployee(Employee employee, Tenant tenant);

	public void resetPassword(String username, String tempPassword);

	public List<Employee> getAllUploadedEmployee(Tenant tenant, Region region);

	public void deleteUploadedEmployee(Employee employee, Tenant tenant);

	public String getEmployeeUsernameByEmail(String resetPasswdEmail);
	
	public String userExist(Employee employee);

	public void updateInviteEmployee(Employee employee, List<Position> newSelectedPositions, List<Position> deletedPositions);

	public Region findRegionByEmployee(Integer employeeId, Integer id);
	
	public List<Employee> getAllEmployeeByGroup(EmailGroup emailGroup);
	
	public List<Employee> getAllEmployeesByRegionAndPosition(List<Position> positions,List<Region> regions,Tenant tenant);
	public EmailGroup addEmailGroup(EmailGroup emailGroup);

	public boolean isUsernameAlreadyExist(String username);

	public List<Employee> getAllOverrideEmployeeByPosition(Event event, EventPosition eventPosition,Region selectedRegion, Tenant tenant);

	public List<Employee> getSalesPersons(Location location, Tenant tenant);

	public void saveEmployee(Employee employee);
}
