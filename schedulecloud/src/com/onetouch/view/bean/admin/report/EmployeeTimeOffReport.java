package com.onetouch.view.bean.admin.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.onetouch.model.domainobject.Employee;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TimeOffRequest;
import com.onetouch.view.bean.BaseBean;

//@ManagedBean(name="empTimeOff")
//@ViewScoped
public class EmployeeTimeOffReport extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, List<TimeOffRequest>> vacationTimeOffMap;
	private Map<String, List<TimeOffRequest>> sickTimeOffMap;
	private Employee timeoffEmployee;
	private Map<Integer,Employee> employeeTimeOff;
	private Tenant tenant;
	@PostConstruct
	public void initBean(){
		tenant = tenantContext.getTenant();
		List<TimeOffRequest> vacationTimeOffList = getTimeoffService().getAllVacationTimeOffRequestsByEmployee(new Employee(1),tenant,"");
		List<TimeOffRequest> sickTimeOffList = getTimeoffService().getAllSickTimeOffRequestByEmployee(new Employee(1),tenant,"");
		vacationTimeOffMap = new HashMap<String, List<TimeOffRequest>>();
		sickTimeOffMap = new HashMap<String, List<TimeOffRequest>>();
		sickTimeOffMap.put("Sick", sickTimeOffList);
		vacationTimeOffMap.put("Vacation", vacationTimeOffList);
		employeeTimeOff = new HashMap<Integer, Employee>();
		/*for(TimeOffRequest timeOffRequest : timeOffList){
			Integer empId = timeOffRequest.getEmployeeId();
			Employee employee = employeeTimeOff.get(empId);
			if(employee == null){
				employee = new Employee(empId);
				employee.setFirstname(timeOffRequest.getEmpName());
				employeeTimeOff.put(empId, employee);
			}
			
			List<TimeOffRequest> timeoffs = employee.getTimeOffList();
			if(timeoffs==null){
				timeoffs = new ArrayList<TimeOffRequest>();
				employee.setTimeOffList(timeoffs);
			}
			timeoffs.add(timeOffRequest);
		}*/
	}
	
	

	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public List<Employee> getEmployeeTimeOff() {
		return new ArrayList<Employee>();
	}

	public Employee getTimeoffEmployee() {
		return timeoffEmployee;
	}


	public void setTimeoffEmployee(Employee timeoffEmployee) {
		this.timeoffEmployee = timeoffEmployee;
	}
	public List getSickTimeOffMap(){
		List entrySet = new ArrayList(sickTimeOffMap.entrySet());
		
		return entrySet;
	}
	public List getVacationTimeOffMap(){
		List entrySet = new ArrayList(vacationTimeOffMap.entrySet());
		
		return entrySet;
	}
}
