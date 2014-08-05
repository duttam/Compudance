package com.onetouch.model.domainobject;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class DetailedAvailReport{

	private String reportDate;
	private Employee employee;
	private Set<Availability> availabilities;
	private Set<Schedule> schedules;
	private Location location;
	private Event event;
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public Set<Availability> getAvailabilities() {
		return availabilities;
	}
	public void setAvailabilities(Set<Availability> availabilities) {
		this.availabilities = availabilities;
	}
		
	public Set<Schedule> getSchedules() {
		return schedules;
	}
	public void setSchedules(Set<Schedule> schedules) {
		this.schedules = schedules;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	
	
}
