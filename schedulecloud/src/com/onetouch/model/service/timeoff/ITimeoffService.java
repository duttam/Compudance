package com.onetouch.model.service.timeoff;

import java.util.List;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TimeOffRequest;

public interface ITimeoffService {

	public List<TimeOffRequest> getAllTimeOffRequests(Integer empId);
        public TimeOffRequest getTimeOffRequest(Integer id);
	public void addTimeOffRequest(TimeOffRequest request);
        public void updateTimeOffRequest(TimeOffRequest request);
	public void sendRequest();
	public List<TimeOffRequest> getAllVacationTimeOffRequestsByEmployee(Employee employee,Tenant tenant,final String timeoffMonth);
	public List<TimeOffRequest> getAllSickTimeOffRequestByEmployee(Employee employee, Tenant tenant,String timeoffmonth);
	public List<Schedule> addSickTimeOffRequests(List<TimeOffRequest> sickRequests);
	public List<TimeOffRequest> getAllCallOutRequests(Integer empId);
	public void deleteSickTimeOff(TimeOffRequest timeOff);
	public void updateSickTimeOffRequest(TimeOffRequest timeOff);
	
}
