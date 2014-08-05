package com.onetouch.model.service.event;

import java.util.Date;
import java.util.List;

import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.DressCode;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.EventType;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TimeOffRequest;

public interface IEventService {

	public List<Event> getAllEvents(Tenant tenant,Region region,CustomUser user);
	public Event addEvent(Event event);
	public void editEvent(Event event);
	public Integer saveEventPosition(EventPosition eventPosition);
	public List<Event> getAllEventsByLocation(Tenant tenant,Location location);
	public Event getEvent(Integer id, Tenant tenant, Region region);
	public Event getEventDetail(Integer id, Tenant tenant);
	public List<Event> getAllPublishedEvents(Tenant tenant,CustomUser user);
	public List<EventPosition> getAllAvailableEmployeesByPosition(Event event, Tenant tenant) ;
	public List<Event> getAllPublishedEventsByDateRange(Tenant tenant,Date start, Date end,CustomUser customUser);
	public List<Event> getAllPublishedEventsByDateRangeAndLocation(Tenant tenant,Date start, Date end,CustomUser customUser,Location selectedLocation, Region currentRegion);
	public List<Event> getAllEventsByEmployee(Employee employee,TimeOffRequest timeOff,
			Tenant tenant);
	
	public void deleteEventAndPositions(Event event, Tenant tenant);
	public List<Schedule> deletePublishedEvent(Event event, Tenant tenant);
	public List<EventType> getAllEventTypesByRegion(Tenant tenant,
			Region selectedRegion);
	public EventType addEventType(EventType eventType);
	public List<EventType> getAllEventTypes(Tenant tenant);
	public void updateEventType(EventType eventType);
	public List<Event> getAllSchedulesByEmployee(Date rosterStartDate,Date rosterEndDate, Employee selectedEmployee, Tenant tenant,
			Region selectedRegion);
	public List<EventPosition> getTempAllAvailableEmployeesByPosition(Event event, Tenant tenant,Region region);
	public List<DressCode> getAllDressCodes(Tenant tenant, Region region);
	public void updateDressCode(DressCode dressCode);
	public void saveDressCode(DressCode dressCode);
	
}
