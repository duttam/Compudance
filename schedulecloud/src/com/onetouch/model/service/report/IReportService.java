package com.onetouch.model.service.report;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.DetailedAvailReport;

import com.onetouch.model.domainobject.DetailedAvailReportByEmployee;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.OneTouchReport;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.Tenant;

public interface IReportService {

	public List<Position> getScheduleByEventPosition(Event event, Tenant tenant);
	public List<Position> getSignInOutByEventPosition(Event event, Tenant tenant);
	public List<Event> getAllEventDetailReport(Tenant tenant);
	public List<OneTouchReport> getAllScheduleByEmployee(Employee employee,
			Tenant tenant,String reportMonth);
	public List<OneTouchReport> getAllPublishedEventsByMonth(Tenant tenant,CustomUser customUser,boolean isEventDetailReport, String reportMonth);
	public List<OneTouchReport> getAllPublishedEventsByDateRange(Tenant tenant,CustomUser customUser, boolean isMonthView, Date reportStartDate,Date reportEndDate);
	public List<OneTouchReport> getAllPublishedEventsByDateRange(Tenant tenant,CustomUser customUser, Region region,Date reportStartDate,Date reportEndDate);
	
	public List<OneTouchReport> getDetailedAvailReportByEmployeeDateRange(List<Position> selectedPositions, Date reportStartDate,Date reportEndDate, Region selectedRegion, Tenant tenant);
	public List<OneTouchReport> getAllEmployeeStatusByDateRange(List<Position> selectedPositions, Date reportStartDate,Date reportEndDate, Region selectedRegion, Tenant tenant);
}
