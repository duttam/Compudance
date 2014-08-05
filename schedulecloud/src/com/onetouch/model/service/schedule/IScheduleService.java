package com.onetouch.model.service.schedule;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Schedule;

import com.onetouch.model.domainobject.Tenant;

public interface IScheduleService {

	public List<Schedule> saveSchedules(Event event,List<Schedule> saveSchedules);
	//public Integer updateSchedule(Event event,List<Schedule> saveSchedules,List<Schedule> deleteSchedules);
	/**
	 * retrieve all schedules for employees group by shift, positions,employees for a particular event and company
	 * @param event
	 * @param tenant
	 * @return
	 */
	public List<Position> getScheduleByEvent(Event event, Tenant tenant);
	
	public Schedule getSchedule(Integer scheduleId,Integer eventPositionId, Integer employeeId, Integer companyId);
	
	
	public List<Position> getPositionsByShift(Integer id, Integer shiftId,Integer tenantId);
	
	public Map<Integer,EventPosition> getTempScheduleByEventPosition(Event event, Tenant tenant);
	public List<EventPosition> getScheduleByEventPosition(Event event, Tenant tenant);
	public List<Employee> getAvailableEmployeesByEventPosition(
			EventPosition eventPosition, Event event,Tenant tenant,Region region);
	public List<Employee> getTempAvailableEmployeesByEventPosition(
			EventPosition eventPosition, Event event, Tenant tenant, Region region);
	//public void updateTempSchedule(Event event, EventPosition scheduledEventPosition, List<Schedule> saveSchedules, List<Schedule> deleteSchedules);
	public void deleteScheduleByEventPosition(EventPosition scheduledEventPosition, List<Schedule> deleteSchedules,Tenant tenant);
	//public EventPosition getAvailableEmployeesByPosition(Event event,EventPosition customEventPosition);
	public List<Schedule> scheduleNewEventPosition(
			EventPosition customEventPosition, List<Schedule> saveSchedules);
	public List<Schedule> getAllScheduledEventPositions(Employee employee,
			Tenant tenant);
	public List<Schedule> getAllScheduledEventPositionsByDate(Employee employee, Tenant tenant, String mode);
	public Date getScheduleExpireTime(Integer employeeId, Integer companyId,Integer eventPosId);
	public void updateScheduleStatus(Integer scheduleI,Integer employeeId, Integer companyId,Integer eventPosId, Integer status);
	public List<Schedule> getAllNotifiedSchedules(Employee employee,Tenant tenant);
	public List<Employee> getNewAvailableEmployeesByEventPosition(EventPosition eventPosition, Event event, Tenant tenant, Region region);
	public List<Schedule> updateSchedules(Event event, EventPosition scheduledEventPosition, List<Schedule> saveSchedules, List<Schedule> deleteSchedules, List<Schedule> updateSchedules);
	public List<EventPosition> getNotifiedScheduleByEventPosition(Event event,Tenant tenant);
	public void updateEventPositionNotes(EventPosition scheduledEventPosition);
	public void saveOverrideSchedules(Event event,
			EventPosition scheduledEventPosition,
			List<Schedule> overrideSchedules);
	public List<Schedule> getAllNotifiedEventPositionsByDate(Employee employee,Tenant tenant, Date eventDate);
	public List<Employee> getAllScheduledEmployeeByEventPosition(EventPosition scheduledEventPosition, Tenant tenant);
	/*
	 * save schedules in the offerschedule table temporarily
	 */
	public List<Schedule> saveOfferSchedules(Event event,EventPosition scheduledEventPosition, List<Schedule> offerSchedules, List<Schedule> deleteSchedules);
	/**
	 * move temporary offer schedule to schedule table
	 * @param schedule
	 * @param status
	 */
	public boolean saveOfferedSchedule(Schedule schedule, int status);
	public Schedule getOfferSchedule(Integer scheduleId, Integer eventPosId,
			Integer employeeId, Integer companyId);
	public List<Employee> getAllOfferedEmployeesByEventPosition(
			EventPosition offerEventPosition, Tenant tenant);
	public Schedule reAssignEmployee(Schedule schedule, List<Schedule> deleteSchedules);
	public List<EventPosition> getTransferredEventPositionsByEmployee(
			Employee transferEmployee, Event transferEvent, Tenant tenant);
	
	
}
