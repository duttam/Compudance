package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.onetouch.view.comparator.EmployeeNameComparator;
import com.onetouch.view.util.EmployeeUtil;

public class Position implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String  description; // hexadecimal color code
	private String notes;
	private Tenant tenant;
	private Integer reqdNumber; // how many required 
	private boolean selected;
	private Integer shiftPositionId;
	private List<Employee> employees;
	private List<Employee> scheduledEmployees;
	private List<Employee> selectedEmployees;
	private List<Employee> originalEmployees;
	private List<Employee> acceptedEmps;
	private Employee employee;
	private Integer assignedEmpCount; 
	private Integer displayOrder;//
	// for signinout purpose
	private Date startTime;
	private Date endTime;
	private List<SignInOut> scheduledSignInOut; 
	private boolean noEmpAssign = false;
	private boolean viewReports = false;
	private int fromIndex = 0;
	private int toIndex = 50;
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public List<SignInOut> getScheduledSignInOut() {
		return scheduledSignInOut;
	}
	public void setScheduledSignInOut(List<SignInOut> scheduledSignInOut) {
		this.scheduledSignInOut = scheduledSignInOut;
	}
	public Position() {
		
	}
	public Position(Integer id) {
		
		this.id = id;
	}
	
	
	public Position(Integer id, String name) {
	
		this.id = id;
		this.name = name;
	}
	private Position(Integer id, String name, String description,
			String notes, Integer reqdNumber, boolean selected) {
		
		this.id = id;
		this.name = name;
		this.description = description;
		this.notes = notes;
		this.reqdNumber = reqdNumber;
		this.selected = selected;
	}
	private Position(Integer id, String name, String description, Integer reqdNumber) {
		
		this.id = id;
		this.name = name;
		this.description = description;
		this.reqdNumber = reqdNumber;
	}
	public static Position getPosition(Integer id, String name, String positionColor, Integer reqdNumber){
		return new Position(id, name, positionColor,reqdNumber);
	}
	public static Position getPosition(Integer id, String name, String positionColor,
			String notes, Integer reqdNumber, boolean selected){
		return new Position(id, name, positionColor, notes, reqdNumber, selected);
	}
	public Position(Tenant tenant) {
		this.tenant = tenant;
		
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public Integer getReqdNumber() {
		return reqdNumber;
	}
	public void setReqdNumber(Integer reqdNumber) {
		this.reqdNumber = reqdNumber;
	}
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public List<Employee> getEmployees() {
		if(employees!=null && employees.size()>0)
			Collections.sort(employees,new EmployeeNameComparator());
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	
	public List<Employee> getSelectedEmployees() {
		
		return selectedEmployees;
	}
	public void setSelectedEmployees(List<Employee> selectedEmployees) {
		this.selectedEmployees = selectedEmployees;
	}
	
	public Integer getShiftPositionId() {
		return shiftPositionId;
	}
	public void setShiftPositionId(Integer shiftPositionId) {
		this.shiftPositionId = shiftPositionId;
	}
	
	public Integer getAssignedEmpCount() {
		return assignedEmpCount;
	}
	public void setAssignedEmpCount(Integer assignedEmpCount) {
		this.assignedEmpCount = assignedEmpCount;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
		
	public List<Employee> getOriginalEmployees() {
		return originalEmployees;
	}
	public void setOriginalEmployees(List<Employee> originalEmployees) {
		this.originalEmployees = originalEmployees;
	}
	
	public List<Employee> getScheduledEmployees() {
		if(scheduledEmployees!=null && scheduledEmployees.size()>0)
			Collections.sort(scheduledEmployees,new EmployeeNameComparator());
		return scheduledEmployees;
	}
	public Integer getScheduledEmployeesCount() {
		Integer empCount = 0;
		if(scheduledEmployees!=null){
			for(Employee employee : scheduledEmployees){
				if(employee.getSchedule()!=null && employee.getSchedule().getSchedulestatus()<=2)
					empCount = empCount+1;
			}
		}
		return empCount;
	}
	public Integer getScheduledAcceptedCount() {
		Integer empCount = 0;
		if(scheduledEmployees!=null){
			for(Employee employee : scheduledEmployees){
				if(employee.getSchedule()!=null && employee.getSchedule().getSchedulestatus()==2)
					empCount = empCount+1;
			}
		}
		return empCount;
	}
	public List<Employee> getAllAcceptedEmployees(){
		
		List<Employee> pos_scheduledEmps = this.getScheduledEmployees();
		
		
		if(pos_scheduledEmps!=null && (pos_scheduledEmps.size()>0)){
			for(Employee employee : pos_scheduledEmps){
				Schedule schedule = employee.getSchedule();
				if(schedule!=null && schedule.getSchedulestatus().intValue()== 2){
					acceptedEmps.add(employee);
				}
			}
		}
		if(acceptedEmps!=null && acceptedEmps.size()>0)
			Collections.sort(acceptedEmps,new EmployeeNameComparator());
		return acceptedEmps;
	}
	public List<Employee> getAllNotifiedAcceptedEmployees(){
		
		List<Employee> pos_scheduledEmps = this.getScheduledEmployees();
		List<Employee> notifiedAcceptedEmps = new ArrayList<Employee>();
		
		if(pos_scheduledEmps!=null && (pos_scheduledEmps.size()>0)){
			for(Employee employee : pos_scheduledEmps){
				Schedule schedule = employee.getSchedule();
				if(schedule!=null && schedule.getSchedulestatus()>= 1){
					notifiedAcceptedEmps.add(employee);
				}
			}
		}
		if(notifiedAcceptedEmps!=null && notifiedAcceptedEmps.size()>0)
			Collections.sort(notifiedAcceptedEmps,new EmployeeNameComparator());
		return notifiedAcceptedEmps;
	}
	
	public List<Employee> getOnlyNotifiedEmployees(){
		
		List<Employee> pos_scheduledEmps = this.getScheduledEmployees();
		List<Employee> notifiedEmps = new ArrayList<Employee>();
		
		if(pos_scheduledEmps!=null && (pos_scheduledEmps.size()>0)){
			for(Employee employee : pos_scheduledEmps){
				Schedule schedule = employee.getSchedule();
				if(schedule!=null && schedule.getSchedulestatus()>= 1){
					notifiedEmps.add(employee);
				}
			}
		}
		if(notifiedEmps!=null && notifiedEmps.size()>0)
			Collections.sort(notifiedEmps,new EmployeeNameComparator());
		return notifiedEmps;
	}
	public void setScheduledEmployees(List<Employee> scheduledEmployees) {
		this.scheduledEmployees = scheduledEmployees;
	}
	
	public boolean isNoEmpAssign() {
		return noEmpAssign;
	}
	public void setNoEmpAssign(boolean noEmpAssign) {
		this.noEmpAssign = noEmpAssign;
	}
	
	public boolean isViewReports() {
		return viewReports;
	}
	public void setViewReports(boolean viewReports) {
		this.viewReports = viewReports;
	}
	
	public List<Employee> getAcceptedEmps() {
		if(acceptedEmps!=null && acceptedEmps.size()>0)
			Collections.sort(acceptedEmps,new EmployeeNameComparator());
		return acceptedEmps;
		
	}
	public void setAcceptedEmps(List<Employee> acceptedEmps) {
		this.acceptedEmps = acceptedEmps;
	}
	public List<Employee> getLimitedEmployees(){
		if(employees.size()>=(fromIndex+toIndex))
			return employees.subList(fromIndex, toIndex);
		
		return employees;
	}
	
	public int getFromIndex() {
		return fromIndex;
	}
	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}
	public int getToIndex() {
		return toIndex;
	}
	public void setToIndex(int toIndex) {
		this.toIndex = toIndex;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Position [id=" + id + ", name=" + name + ", description="
				+ description + ", notes=" + notes + ", tenant=" + tenant
				+ ", reqdNumber=" + reqdNumber + ", selected=" + selected
				+ ", displayOrder=" + displayOrder + "]";
	}
	
	
}
 