package com.onetouch.model.service.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.employee.EmployeeDAO;
import com.onetouch.model.dao.event.EventDAO;
import com.onetouch.model.dao.eventposition.EventPositionDAO;
import com.onetouch.model.dao.position.PositionDAO;
import com.onetouch.model.dao.report.ReportDAO;
import com.onetouch.model.dao.schedule.ScheduleDAO;

import com.onetouch.model.dao.signinout.SignInOutDAO;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.DetailedAvailReport;

import com.onetouch.model.domainobject.DetailedAvailReportByEmployee;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.OneTouchReport;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Schedule;

import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.util.DateUtil;

@Service
public class ReportService implements IReportService{

	@Autowired
	private ScheduleDAO scheduleDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private EventPositionDAO eventPositionDAO;
	@Autowired
	private PositionDAO positionDAO;
	@Autowired
	private SignInOutDAO signInOutDAO;

	@Autowired
	private EventDAO eventDAO;
	
	@Autowired
	private ReportDAO reportDAO;
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Position> getScheduleByEventPosition(Event event, Tenant tenant) {
		/*List<Schedule> scheduleList = scheduleDAO.findByEvent(event.getId(),tenant.getId()); // get all active schedules from schedule tables

		Map<Integer,Position> positions = new HashMap<Integer,Position>();
		if(scheduleList!=null && scheduleList.size()>0){
			//SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
			for(Schedule schedule : scheduleList){
				Integer empId = schedule.getEmployee().getId(); // get employee id for schedule
				Integer sftId = schedule.getShift().getId(); // get shift id for schedule
				Integer pstnId = schedule.getPosition().getId(); // get position id for schedule


				Position position = positions.get(pstnId);
				if(position == null){
					position = positionDAO.findById(pstnId); // get position
					positions.put(pstnId,position);
				}
				List<Employee> employees = position.getEmployees();
				if(employees==null){
					employees = new ArrayList<Employee>();
					position.setEmployees(employees);
				}
				Shift shift = shiftDAO.findById(sftId, tenant.getId());
				int shiftHrs = DateUtil.getHours(shift.getStartTime(),shift.getEndTime());
				int shiftMins = DateUtil.getMinutes(shift.getStartTime(),shift.getEndTime());

				Employee employee = employeeDAO.findById(empId);//get employee

				employee.setScheduleId(schedule.getId());
				employee.setHours(shiftHrs);
				employee.setMinutes(shiftMins);
				employee.setPosition(position);
				employees.add(employee);

			}

		}

		return new ArrayList<Position>(positions.values());*/

		return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Position> getSignInOutByEventPosition(Event event, Tenant tenant) {
		List<SignInOut> signInOutList = signInOutDAO.findByEvent(event.getId(),tenant.getId()); // get all active schedules from schedule tables

		Map<Integer,Position> positions = new HashMap<Integer,Position>();
		if(signInOutList!=null && signInOutList.size()>0){
			for(SignInOut signInOut : signInOutList){
				Integer empId = signInOut.getEmployee_id(); // get employee id for schedule
				Integer pstnId = signInOut.getPosition_id(); // get position id for schedule


				Position position = positions.get(pstnId);
				if(position == null){
					position = positionDAO.findById(pstnId); // get position
					positions.put(pstnId,position);
				}
				List<Employee> employees = position.getEmployees();
				if(employees==null){
					employees = new ArrayList<Employee>();
					position.setEmployees(employees);
				}
				/*if(signInOut.getStartTime()!=null && signInOut.getEndTime()!=null){
					if(DateUtil.compareTimeOnly(signInOut.getStartTime(),signInOut.getEndTime())<=0){
						shiftHrs = DateUtil.getHours(signInOut.getStartTime(),signInOut.getEndTime());
						shiftMins = DateUtil.getMinutes(signInOut.getStartTime(),signInOut.getEndTime());
					}
					else{// calculation needs to be done properly
						shiftHrs = 23 - (DateUtil.getHours(signInOut.getEndTime(),signInOut.getStartTime()));
						shiftMins = 60-(DateUtil.getHours(signInOut.getEndTime(),signInOut.getStartTime()));
					}
				}
				if(signInOut.getBreakStartTime()!=null && signInOut.getBreakEndTime()!=null){
					if(DateUtil.compareTimeOnly(signInOut.getBreakStartTime(),signInOut.getBreakEndTime())<=0){
						breakHrs = DateUtil.getHours(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
						breakMins = DateUtil.getMinutes(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
					}
					else{
						breakHrs = 24 - DateUtil.getHours(signInOut.getBreakEndTime(),signInOut.getBreakStartTime());
						breakMins = 60 - DateUtil.getMinutes(signInOut.getBreakEndTime(),signInOut.getBreakStartTime());
					}
				}*/
				int shiftHrs = DateUtil.getHours(signInOut.getStartTime(),signInOut.getEndTime());
				int shiftMins = DateUtil.getMinutes(signInOut.getStartTime(),signInOut.getEndTime());

				//int breakHrs = DateUtil.getHours(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
				//int breakMins = DateUtil.getHours(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
				Employee employee = employeeDAO.findById(empId,tenant.getId());//get employee

				employee.setHours(shiftHrs);
				employee.setMinutes(shiftMins);
				//employee.setBreakHours(breakHrs);
				//employee.setBreakMinutes(breakMins);
				employee.setPosition(position);
				employees.add(employee);

			}

		}

		return new ArrayList<Position>(positions.values());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Event> getAllEventDetailReport(Tenant tenant) {
		Integer companyId = tenant.getId();
		List<Event> eventList = new ArrayList<Event>();// write a new dao method for published events, eventDAO.findAll(tenant.getId(),new String[]{"PUBLISHED"},null);
		List<Event> eventReportList = new ArrayList<Event>();
		for(Event event : eventList){
			Integer eventId = event.getId();
			Event eventDetail = eventDAO.findDetailById(event.getId(), tenant.getId());
			List<EventPosition> allEventPositions = eventPositionDAO.findSchedulesByEvent(event.getId(),tenant.getId());
			for(EventPosition eventPosition : allEventPositions){

			}
		}
		return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<OneTouchReport> getAllScheduleByEmployee(Employee employee,Tenant tenant,String reportMonth) {

		List<OneTouchReport> reportEventList = new ArrayList<OneTouchReport>();
		List<Schedule> allSchedule = new ArrayList<Schedule>();
		String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
		if(reportMonth.equalsIgnoreCase("YTD")){
			for(int month=1;month<13;month++){
				List<Schedule> scheduleList = scheduleDAO.findAllScheduleByEmployee(month,employee.getId(),tenant.getId());
				String monthName = months[month-1];
				for(Schedule schedule : scheduleList){
					Integer empId = employee.getId();
					Date eventDate = schedule.getEvent().getStartDate();
					EmployeeRate employeeRate = employeeDAO.findEmployeeRateById(eventDate,empId,tenant.getId());
					employee.setEmpRate(employeeRate);

					schedule.setTenant(tenant);
					schedule.setEmployee(employee);
					schedule.setEventMonth(monthName);
				}

				if(scheduleList!=null && scheduleList.size()>0){
					OneTouchReport oneTouchReport = new OneTouchReport(monthName);
					oneTouchReport.setScheduleList(scheduleList);
					reportEventList.add(oneTouchReport);
				}

			}
		}else{
			int month =  Arrays.asList(months).indexOf(reportMonth)+1;
			List<Schedule> scheduleList = scheduleDAO.findAllScheduleByEmployee(month,employee.getId(),tenant.getId());
			for(Schedule schedule : scheduleList){
				Integer empId = employee.getId();
				Date eventDate = schedule.getEvent().getStartDate();
				EmployeeRate employeeRate = employeeDAO.findEmployeeRateById(eventDate,empId,tenant.getId());
				employee.setEmpRate(employeeRate);

				schedule.setTenant(tenant);
				schedule.setEmployee(employee);
				schedule.setEventMonth(reportMonth);
			}

			if(scheduleList!=null && scheduleList.size()>0){
				OneTouchReport oneTouchReport = new OneTouchReport(reportMonth);
				oneTouchReport.setScheduleList(scheduleList);
				reportEventList.add(oneTouchReport);
			}
		}


		/*for(Schedule schedule : allSchedule){
			Integer empId = employee.getId();
			Date eventDate = schedule.getEvent().getStartDate();
			EmployeeRate employeeRate = employeeDAO.findEmployeeRateById(eventDate,empId,tenant.getId());
			employee.setEmpRate(employeeRate);
			BigDecimal working_hours = new BigDecimal(schedule.getWorkingHours());
			BigDecimal working_minutes = new BigDecimal(schedule.getWorkingMinutes());
			if(employeeRate!=null){
				BigDecimal reg_pay = employeeRate.getHourlyRate().multiply(working_hours).add(employeeRate.getHourlyRate().divide(new BigDecimal(60)).multiply(working_minutes));
				BigDecimal ot_pay = employeeRate.getHourlyRate().multiply(working_hours).add(employeeRate.getHourlyRate().divide(new BigDecimal(60)).multiply(working_minutes));
				BigDecimal dt_pay = employeeRate.getHourlyRate().multiply(working_hours).add(employeeRate.getHourlyRate().divide(new BigDecimal(60)).multiply(working_minutes));
				employeeRate.setRegPay(reg_pay);
				employeeRate.setOtPay(ot_pay);
				employeeRate.setDtPay(dt_pay);
			}

			schedule.setEmployee(employee);

		}*/
		return reportEventList;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<OneTouchReport> getAllPublishedEventsByMonth(Tenant tenant,CustomUser customUser,boolean isEventDetail, String reportMonth) {
		Integer companyId = (tenant!=null) ? tenant.getId() : null;
		List<OneTouchReport> reportEventList = new ArrayList<OneTouchReport>();
		String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
		if(isEventDetail){
			
			if(reportMonth.equalsIgnoreCase("YTD")){
				for(int month=1;month<13;month++){
					List<Event> eventList = eventDAO.findAllByMonth(customUser,companyId,month);
					String monthName = months[month-1];
					for(Event event : eventList){
						BigDecimal totalForecastLaborCost = new BigDecimal(0),totalActualLaborCost = new BigDecimal(0),totalBreakLaborCost = new BigDecimal(0);
						event.setEventMonth(monthName);
						List<Employee> sch_emps = scheduleDAO.findAllScheduledEmpByEvent(event.getId(), companyId);
						for(Employee employee : sch_emps){
							EmployeeRate employeeRate = employeeDAO.findEmployeeRateById(event.getStartDate(), employee.getId(), tenant.getId());
							BigDecimal hourlyRate = employeeRate.getHourlyRate();
							BigDecimal shiftHrs = new BigDecimal(employee.getHours());
							BigDecimal shiftMins = new BigDecimal(employee.getMinutes());
							totalForecastLaborCost = totalForecastLaborCost.add(shiftHrs.multiply(hourlyRate).add((hourlyRate.divide(new BigDecimal(60),2).multiply(shiftMins))));
						}
						event.setTotalForecastLaborCost(totalForecastLaborCost);
						List<Employee> signInOut_emps = scheduleDAO.findAllSignInOutEmpByEvent(event.getId(), companyId);
						for(Employee employee : signInOut_emps){
							EmployeeRate employeeRate = employeeDAO.findEmployeeRateById(event.getStartDate(), employee.getId(), tenant.getId());
							BigDecimal hourlyRate = employeeRate.getHourlyRate();
							BigDecimal shiftHrs = new BigDecimal(employee.getHours());
							BigDecimal shiftMins = new BigDecimal(employee.getMinutes());
							BigDecimal breakHrs = new BigDecimal(employee.getBreakHours());
							BigDecimal breakMins = new BigDecimal(employee.getBreakMinutes());
							totalActualLaborCost = totalActualLaborCost.add(shiftHrs.multiply(hourlyRate).add((hourlyRate.divide(new BigDecimal(60),2).multiply(shiftMins))));
							totalBreakLaborCost = totalBreakLaborCost.add(hourlyRate.multiply(breakHrs).add((hourlyRate.divide(new BigDecimal(60),2).multiply(breakMins))));
						}
						event.setTotalActualLaborCost(totalActualLaborCost.subtract(totalBreakLaborCost));
					}

					if(eventList!=null && eventList.size()>0){
						OneTouchReport oneTouchReport = new OneTouchReport(monthName, eventList);
						reportEventList.add(oneTouchReport);
					}

				}
			}else{
					int month =  Arrays.asList(months).indexOf(reportMonth)+1;
					List<Event> eventList = eventDAO.findAllByMonth(customUser,companyId,month);
					//String monthName = months[month-1];
					for(Event event : eventList){
						BigDecimal totalForecastLaborCost = new BigDecimal(0),totalActualLaborCost = new BigDecimal(0),totalBreakLaborCost = new BigDecimal(0);
						event.setEventMonth(reportMonth);
						List<Employee> sch_emps = scheduleDAO.findAllScheduledEmpByEvent(event.getId(), companyId);
						for(Employee employee : sch_emps){
							EmployeeRate employeeRate = employeeDAO.findEmployeeRateById(event.getStartDate(), employee.getId(), tenant.getId());
							BigDecimal hourlyRate = employeeRate.getHourlyRate();
							if(hourlyRate==null)
								hourlyRate = new BigDecimal(0.0);
							BigDecimal shiftHrs = new BigDecimal(employee.getHours());
							Integer minInt = employee.getMinutes();
							BigDecimal shiftMins = new BigDecimal(minInt);
							totalForecastLaborCost = totalForecastLaborCost.add(shiftHrs.multiply(hourlyRate).add((hourlyRate.divide(new BigDecimal(60),2).multiply(shiftMins))));
						}
						event.setTotalForecastLaborCost(totalForecastLaborCost);
						List<Employee> signInOut_emps = scheduleDAO.findAllSignInOutEmpByEvent(event.getId(), companyId);
						for(Employee employee : signInOut_emps){
							EmployeeRate employeeRate = employeeDAO.findEmployeeRateById(event.getStartDate(), employee.getId(), tenant.getId());
							BigDecimal hourlyRate = employeeRate.getHourlyRate();
							BigDecimal shiftHrs = new BigDecimal(employee.getHours());
							BigDecimal shiftMins = new BigDecimal(employee.getMinutes());
							BigDecimal breakHrs = new BigDecimal(employee.getBreakHours());
							BigDecimal breakMins = new BigDecimal(employee.getBreakMinutes());
							totalActualLaborCost = totalActualLaborCost.add(shiftHrs.multiply(hourlyRate).add((hourlyRate.divide(new BigDecimal(60),2).multiply(shiftMins))));
							totalBreakLaborCost = totalBreakLaborCost.add(hourlyRate.multiply(breakHrs).add((hourlyRate.divide(new BigDecimal(60),2).multiply(breakMins))));
						}
						event.setTotalActualLaborCost(totalActualLaborCost.subtract(totalBreakLaborCost));
					}
	
					if(eventList!=null && eventList.size()>0){
						OneTouchReport oneTouchReport = new OneTouchReport(reportMonth, eventList);
						reportEventList.add(oneTouchReport);
					}

			}
		}else{
			for(int month=1;month<13;month++){
				List<Event> eventList = eventDAO.findAllByMonth(customUser,companyId,month);
				String monthName = months[month-1];
				if(eventList!=null && eventList.size()>0){
					OneTouchReport oneTouchReport = new OneTouchReport(monthName, eventList);
					reportEventList.add(oneTouchReport);
				}
			}

			
		}


		return reportEventList;
	}

	@Override
	public List<OneTouchReport> getAllPublishedEventsByDateRange(Tenant tenant,
			CustomUser customUser, boolean isMonthView, Date reportStartDate,
			Date reportEndDate) {
		Integer companyId = (tenant!=null) ? tenant.getId() : null;
		Map<String,OneTouchReport> reportEventMap = new LinkedHashMap<String, OneTouchReport>();
		
		String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
		
		List<Event> eventList = eventDAO.findAllByDateRange(companyId, reportStartDate, reportEndDate, customUser);
		for(Event event : eventList){
			BigDecimal totalForecastLaborCost = new BigDecimal(0),totalActualLaborCost = new BigDecimal(0),totalBreakLaborCost = new BigDecimal(0);
			
			List<Employee> sch_emps = scheduleDAO.findAllScheduledEmpByEvent(event.getId(), companyId);
			for(Employee employee : sch_emps){
				EmployeeRate employeeRate = employeeDAO.findEmployeeRateById(event.getStartDate(), employee.getId(), tenant.getId());
				BigDecimal hourlyRate = employeeRate.getHourlyRate();
				BigDecimal shiftHrs = new BigDecimal(employee.getHours());
				BigDecimal shiftMins = new BigDecimal(employee.getMinutes());
				totalForecastLaborCost = totalForecastLaborCost.add(shiftHrs.multiply(hourlyRate).add((hourlyRate.divide(new BigDecimal(60),2).multiply(shiftMins))));
			}
			event.setTotalForecastLaborCost(totalForecastLaborCost);
			List<Employee> signInOut_emps = scheduleDAO.findAllSignInOutEmpByEvent(event.getId(), companyId);
			for(Employee employee : signInOut_emps){
				EmployeeRate employeeRate = employeeDAO.findEmployeeRateById(event.getStartDate(), employee.getId(), tenant.getId());
				BigDecimal hourlyRate = employeeRate.getHourlyRate();
				BigDecimal shiftHrs = new BigDecimal(employee.getHours());
				BigDecimal shiftMins = new BigDecimal(employee.getMinutes());
				BigDecimal breakHrs = new BigDecimal(employee.getBreakHours());
				BigDecimal breakMins = new BigDecimal(employee.getBreakMinutes());
				totalActualLaborCost = totalActualLaborCost.add(shiftHrs.multiply(hourlyRate).add((hourlyRate.divide(new BigDecimal(60),2).multiply(shiftMins))));
				totalBreakLaborCost = totalBreakLaborCost.add(hourlyRate.multiply(breakHrs).add((hourlyRate.divide(new BigDecimal(60),2).multiply(breakMins))));
			}
			event.setTotalActualLaborCost(totalActualLaborCost.subtract(totalBreakLaborCost));
			DateTime startDateTime = new DateTime(event.getStartDate());
			String monthName = startDateTime.monthOfYear().getAsText();
			event.setEventMonth(monthName);
			OneTouchReport oneTouchReport = reportEventMap.get(monthName);
			if(oneTouchReport==null){
				oneTouchReport = new OneTouchReport(monthName, new ArrayList<Event>());
				reportEventMap.put(monthName, oneTouchReport);
			}
			oneTouchReport.getEventList().add(event);
		}

		
		return new ArrayList<OneTouchReport>(reportEventMap.values());
	}

	@Override
	public List<OneTouchReport> getAllPublishedEventsByDateRange(Tenant tenant,CustomUser customUser, Region region, Date reportStartDate,Date reportEndDate) {
		Map<String,OneTouchReport> reportEventMap = new LinkedHashMap<String, OneTouchReport>();
		Integer companyId = (tenant!=null) ? tenant.getId() : null;
		List<Event> eventList = eventDAO.findAllByDateRangeAndLocation(companyId,reportStartDate,reportEndDate,customUser,null,region);
		if(eventList!=null && eventList.size()>0){
			for(Event event : eventList){
				List<Map<String,Object>> schCnt = eventPositionDAO.findScheduleCountForEvent(event.getId(), companyId,region.getId());
				for(Map<String, Object> m : schCnt){
				   Integer requiredCnt = (Integer)m.get("reqdNum");
				   Integer staffed = (Integer)m.get("schNum");
				   event.setPositionRequiredCount(requiredCnt);
				   event.setPositionStaffedCount(staffed);
				   event.setPositionOpenCount(requiredCnt-staffed);
				} 
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				String eventDate = dateformat.format(event.getStartDate());
				OneTouchReport oneTouchReport = reportEventMap.get(eventDate);
				if(oneTouchReport==null){
					oneTouchReport = new OneTouchReport(new ArrayList<Event>());
					oneTouchReport.setEventDate(eventDate);
					reportEventMap.put(eventDate, oneTouchReport);
				}
				oneTouchReport.getEventList().add(event);
			}
		}
		
		return new ArrayList<OneTouchReport>(reportEventMap.values());
	}

	@Override
	public List<OneTouchReport> getDetailedAvailReportByEmployeeDateRange(
			List<Position> selectedPositions, Date reportStartDate,
			Date reportEndDate, Region selectedRegion, Tenant tenant) {
		List<OneTouchReport> reports = new ArrayList<OneTouchReport>();
		int days = DateUtil.dateRange(reportStartDate,reportEndDate);
		for(int i=0;i<=days;i++){
			Date reportDate =  DateUtil.incrementByDay(reportStartDate,i);
			for(Position selectedPosition : selectedPositions){
				List<DetailedAvailReportByEmployee> result =  reportDAO.findAvailabilityByPositionAndDate(selectedPosition, reportDate,selectedRegion,tenant);
				OneTouchReport oneTouchReport = new OneTouchReport();
				oneTouchReport.setDetailedAvailReports(result);
				oneTouchReport.setReportDate(reportDate);
				oneTouchReport.setPosition(selectedPosition);
				reports.add(oneTouchReport);
			}
		}
		
        return reports;
	}
	
	@Override
	public List<OneTouchReport> getAllEmployeeStatusByDateRange(
			List<Position> selectedPositions, Date reportStartDate,
			Date reportEndDate, Region selectedRegion, Tenant tenant) {
		List<OneTouchReport> reports = new ArrayList<OneTouchReport>();
		int days = DateUtil.dateRange(reportStartDate,reportEndDate);
		for(int i=0;i<=days;i++){
			Date reportDate =  DateUtil.incrementByDay(reportStartDate,i);
			for(Position selectedPosition : selectedPositions){
				List<DetailedAvailReportByEmployee> result =  reportDAO.findEmployeeStatusByPositionAndDate(selectedPosition, reportDate,selectedRegion,tenant);
				OneTouchReport oneTouchReport = new OneTouchReport();
				oneTouchReport.setDetailedAvailReports(result);
				oneTouchReport.setReportDate(reportDate);
				oneTouchReport.setPosition(selectedPosition);
				reports.add(oneTouchReport);
			}
		}
		
        return reports;
		
	}
}
