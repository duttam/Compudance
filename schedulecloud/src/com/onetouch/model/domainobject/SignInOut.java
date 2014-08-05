package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.util.Date;

public class SignInOut implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Tenant tenant;
	private Date startTime;
	private Date endTime;
	private Date breakStartTime;
	private Date breakEndTime;
	private String remarks;
	private Employee employee;
	private Event event;
	
	
	private Integer employee_id;
	private Integer position_id;
	private Integer schedule_id;
	
	public SignInOut() {
		
	}
	
	

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
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
	public Date getBreakStartTime() {
		return breakStartTime;
	}
	public void setBreakStartTime(Date breakStartTime) {
		this.breakStartTime = breakStartTime;
	}
	public Date getBreakEndTime() {
		return breakEndTime;
	}
	public void setBreakEndTime(Date breakEndTime) {
		this.breakEndTime = breakEndTime;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Integer getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(Integer schedule_id) {
		this.schedule_id = schedule_id;
	}
	
	
	public Integer getEmployee_id() {
		return employee_id;
	}
	public void setEmployee_id(Integer employee_id) {
		this.employee_id = employee_id;
	}
	public Integer getPosition_id() {
		return position_id;
	}
	public void setPosition_id(Integer position_id) {
		this.position_id = position_id;
	}
	public boolean isDisableBreakStartTimeBtn() {
		boolean emptySftStartTime = startTime==null ;
		boolean emptySftEndTime = endTime==null;
		//boolean emptybreakStartTime = breakStartTime==null;
		if(emptySftStartTime)
			return true;
		else{
			if(emptySftEndTime)
				return false;
			else
				return true;
		}
		
		
	}
	public boolean isDisableBreakEndTimeBtn() {
		boolean emptySftStartTime = startTime==null ;
		
		boolean emptybreakStartTime = breakStartTime==null;
		if(emptySftStartTime)
			return true;
		else{
			if(emptybreakStartTime)
				return true;
			else
				return false;
		}
			
		
	}
}
