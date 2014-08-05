package com.onetouch.model.domainobject;

import java.util.List;

public class EmployeePosition {

	
	private Position position;
	private List<Employee> allAvailEmplyee;
	private List<Employee> assignedEmployee;// new assigned employee list
	private List<Employee> prevAssignedEmployees;// initially this is equal to assignedEmployee . originally assigned employee list
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public List<Employee> getAllAvailEmplyee() {
		return allAvailEmplyee;
	}
	public void setAllAvailEmplyee(List<Employee> allAvailEmplyee) {
		this.allAvailEmplyee = allAvailEmplyee;
	}
	public List<Employee> getAssignedEmployee() {
		return assignedEmployee;
	}
	public void setAssignedEmployee(List<Employee> assignedEmployee) {
		this.assignedEmployee = assignedEmployee;
	}
	public List<Employee> getPrevAssignedEmployees() {
		return prevAssignedEmployees;
	}
	public void setPrevAssignedEmployees(List<Employee> prevAssignedEmployees) {
		this.prevAssignedEmployees = prevAssignedEmployees;
	}
	
	
	
	
}
