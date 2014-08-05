package com.onetouch.model.service.timeoff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TimeOffRequest;
import com.onetouch.model.dao.availability.AvailabilityDAO;
import com.onetouch.model.dao.schedule.ScheduleDAO;
import com.onetouch.model.dao.timeoff.TimeoffDAO;
import com.onetouch.view.util.DateUtil;


@Service(value="timeoffService")
public class TimeoffService implements ITimeoffService {

	@Autowired
	private TimeoffDAO timeoffDAO;
	@Autowired
	private AvailabilityDAO availabilityDAO;
	@Autowired
	private ScheduleDAO scheduleDAO;
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<TimeOffRequest> getAllTimeOffRequests(Integer emplId) {
		return timeoffDAO.findAll(emplId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void addTimeOffRequest(TimeOffRequest request) {
		timeoffDAO.save(request);
		/*if(request.getRequestType().equalsIgnoreCase("Not Available")){
			Date vacation_startdate = request.getBeginDate();
			Date vacation_enddate = request.getEndDate();
			int days = DateUtil.dateRange(vacation_startdate,vacation_enddate)+1;
			List<Availability> vacations = new ArrayList<Availability>();
			for(int day = 0; day< days;day++){
				Availability availability = new Availability(new Tenant(request.getCompanyId()), new Employee(request.getEmployeeId()));
				Date vacationDate = DateUtil.incrementByDay(vacation_startdate,day);
				availability.setAvailDate(vacationDate);
				availability.setTitle("Not Available");
				availability.setAllday(true);
				availability.setStartTime(DateUtil.getDate("00:00 AM","hh:mm a"));
				availability.setEndTime(DateUtil.getDate("11:59 PM","hh:mm a"));
				vacations.add(availability);
			}
			
			availabilityDAO.saveAvailabilityBatch(vacations);
		}*/
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateTimeOffRequest(TimeOffRequest request) {
		timeoffDAO.update(request);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public TimeOffRequest getTimeOffRequest(Integer id) {
		return timeoffDAO.findById(id);
	}

	@Override
	public void sendRequest() {}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<TimeOffRequest> getAllVacationTimeOffRequestsByEmployee(Employee employee,Tenant tenant, final String timeoffMonth) {
		// TODO Auto-generated method stub
		List<TimeOffRequest> vacationTimeoffRequest = new ArrayList<TimeOffRequest>();
		String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
		if(timeoffMonth.equalsIgnoreCase("YTD")){
			for(int month=1;month<13;month++){
				List<TimeOffRequest> timeoffRequest = timeoffDAO.findAllVacationByEmployee(month, employee.getId(),tenant.getId());
				vacationTimeoffRequest.addAll(timeoffRequest);
			}
		}else{
			int month =  Arrays.asList(months).indexOf(timeoffMonth)+1;
			List<TimeOffRequest> timeoffRequest = timeoffDAO.findAllVacationByEmployee(month, employee.getId(),tenant.getId());
			vacationTimeoffRequest.addAll(timeoffRequest);
		}
			
		return vacationTimeoffRequest;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<TimeOffRequest> getAllSickTimeOffRequestByEmployee(
			Employee employee, Tenant tenant,String timeoffMonth) {
		
		List<TimeOffRequest> sickTimeoffRequest = new ArrayList<TimeOffRequest>();
		String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
		if(timeoffMonth.equalsIgnoreCase("YTD")){
			for(int month=1;month<13;month++){
				List<TimeOffRequest> timeoffRequest = timeoffDAO.findAllSickByEmployee(employee.getId(),tenant.getId(),month);
				sickTimeoffRequest.addAll(timeoffRequest);
			}
		}else{
			int month =  Arrays.asList(months).indexOf(timeoffMonth)+1;
			List<TimeOffRequest> timeoffRequest = timeoffDAO.findAllSickByEmployee(employee.getId(),tenant.getId(),month);
			sickTimeoffRequest.addAll(timeoffRequest);
		}
			
		return sickTimeoffRequest;
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Schedule> addSickTimeOffRequests(List<TimeOffRequest> sickRequests) {
		List<Schedule> sickSchedules = null;
		for(TimeOffRequest sickRequest : sickRequests){
			timeoffDAO.save(sickRequest);
			Integer empid = sickRequest.getEmployeeId();
			Integer eventid = sickRequest.getSickEvent().getId();
			Integer companyid = sickRequest.getCompanyId();
			sickSchedules = scheduleDAO.findAllCallOutSchedules(empid,eventid,companyid);
			scheduleDAO.deleteSchedule(sickSchedules);
			
		}
		return sickSchedules;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<TimeOffRequest> getAllCallOutRequests(Integer empId) {
		
		return timeoffDAO.findAllCallOut(empId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteSickTimeOff(TimeOffRequest timeOff) {
		
		timeoffDAO.deleteSickTimeOff(timeOff);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateSickTimeOffRequest(TimeOffRequest timeOff) {
		timeoffDAO.updateCallOutRequest(timeOff);
	}


}
