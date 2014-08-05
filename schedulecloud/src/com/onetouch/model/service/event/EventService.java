package com.onetouch.model.service.event;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.audit.AuditDAO;

import com.onetouch.model.dao.availability.AvailabilityDAO;
import com.onetouch.model.dao.dresscode.DressCodeDAO;
import com.onetouch.model.dao.employee.EmployeeDAO;
import com.onetouch.model.dao.event.EventDAO;
import com.onetouch.model.dao.eventposition.EventPositionDAO;
import com.onetouch.model.dao.schedule.ScheduleDAO;

import com.onetouch.model.dao.signinout.SignInOutDAO;
import com.onetouch.model.dao.timeoff.TimeoffDAO;
import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.DressCode;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.EventType;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.TimeOffRequest;

import com.onetouch.model.domainobject.SignInOut;

import com.onetouch.model.domainobject.Location;

import com.onetouch.model.domainobject.Position;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.EmployeeUtil;
@Service
public class EventService implements IEventService{

	@Autowired
	private EventDAO eventDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	
	@Autowired
	private EventPositionDAO eventPositionDAO;
	@Autowired
	private ScheduleDAO scheduleDAO;
	@Autowired
	private AvailabilityDAO availabilityDAO;
	@Autowired
	private SignInOutDAO signInOutDAO;
	@Autowired
	private TimeoffDAO timeoffDAO;
	
	@Autowired
	private DressCodeDAO dressCodeDAO  ;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Event> getAllEvents(Tenant tenant,Region region,CustomUser user) {
		Integer companyId = (tenant!=null) ? tenant.getId() : null;
		List<Event> eventList = eventDAO.findAll(companyId,region,user);
		
		/*if(eventList!=null && eventList.size()>0){
			for(Event event : eventList){
				Integer ownerId = event.getOwner().getId();
				Employee owner = employeeDAO.findById(ownerId,companyId);
				event.setOwner(owner);
				Employee salesPerson = employeeDAO.findById(event.getCreatedBy(),companyId);
				event.setSalesPerson(salesPerson);
				
			}
		}*/
		return eventList;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Event addEvent(Event event) {
		Integer event_id;
		
		//if its a multiple day event enter day count also
	
		DateTime eventStartdate = event.getStartDate()==null?null:new DateTime(event.getStartDate());
		//DateTime eventEnddate = event.getEndDate()==null?null:new DateTime(event.getEndDate());
		Date stime = event.getStartTime();
    	Date etime = event.getEndTime();
    	
		int days = DateUtil.dateRange(event.getStartDate(),event.getEndDate());
		if(event.isMultidayevent() && days>0 ){
			
			List<Event> events = new ArrayList<Event>();
		
			for(int i=0;i<=days;i++){
				
				Event dayEvent = new Event();
				
				BeanUtils.copyProperties(event,dayEvent,new String[]{"name","owner","coordinator","location","tenant","eventRegion","eventType"});
				dayEvent.setName("DAY"+(i+1)+"/"+event.getName());
				
				Employee eventOwner = new Employee();
				BeanUtils.copyProperties(event.getOwner(),eventOwner);
				Employee eventCoordinator = new Employee();
				BeanUtils.copyProperties(event.getCoordinator(),eventCoordinator);
				
				Location eventLocation = new Location();
				BeanUtils.copyProperties(event.getLocation(),eventLocation);
				String timezone = eventLocation.getTimezone()==null ? "EST" : eventLocation.getTimezone(); 
				dayEvent.setTimezone(timezone);
				Tenant eventTenant = new Tenant();
				BeanUtils.copyProperties(event.getTenant(),eventTenant);
				EventType eventType = new EventType();
				BeanUtils.copyProperties(event.getEventType(),eventType);
				Region eventRegion = new Region();
				BeanUtils.copyProperties(event.getEventRegion(),eventRegion);
				
				dayEvent.setOwner(eventOwner);
				dayEvent.setCoordinator(eventCoordinator);
				dayEvent.setLocation(eventLocation);
				dayEvent.setTenant(eventTenant);
				dayEvent.setEventType(eventType);
				dayEvent.setEventRegion(eventRegion);
				
				dayEvent.setStartDate(eventStartdate.toDate());
				if(stime !=null && etime !=null && DateUtil.compareTimeOnly(stime,etime)>0){
					Date endDate = DateUtil.incrementByDay(event.getStartDate(),1);
					dayEvent.setEndDate(endDate);
				}else{
					dayEvent.setEndDate(eventStartdate.toDate());
				}
				
				events.add(dayEvent);
				// increment start date and end date for the next event
				eventStartdate = eventStartdate.plus(Period.days(1));
				//eventEnddate = eventEnddate.plus(Period.days(1));
				
			}
			Event firstDayEvent = events.get(0);
			event_id =  eventDAO.save(firstDayEvent);// save the first event as we need the id
			firstDayEvent.setId(event_id);
			event = firstDayEvent;//eventDAO.findById(event_id, event.getTenant().getId());
			eventDAO.saveBatch(events.subList(1,events.size()));
		}
		else{
			//event.setEventDate(event.getStartDate());
			
			if(event.getLocation()!=null){
				String timezone = event.getLocation().getTimezone(); 
				if(timezone!=null)
					event.setTimezone(timezone);
				else
					event.setTimezone("EST");
			}
			event_id = eventDAO.save(event);
			event.setId(event_id);
		}
		
		//}
			event.setStatus("active");
			return event;
		
		
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void editEvent(Event event) {
		eventDAO.update(event);
		if(!event.getStatus().equalsIgnoreCase("published") && event.getAssignedEventPosition()!=null){
			for(EventPosition eventPosition : event.getAssignedEventPosition()){
				if(eventPosition.getId()!=null)
					eventPositionDAO.updateEventPosition(eventPosition);
				else{
					Integer evePosId = eventPositionDAO.save(eventPosition);
					//eventPosition.setId(evePosId);
				}
				
			}
		}
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Integer saveEventPosition(EventPosition eventPosition) {
		
		Integer evePosId = eventPositionDAO.save(eventPosition);
		return evePosId;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Event> getAllEventsByLocation(Tenant tenant, Location location) {
		
		//return eventDAO.findByLocation(tenant.getId(),location.getId());
		
		Integer companyId = (tenant!=null) ? tenant.getId() : null;
		List<Event> eventList = eventDAO.findByLocation(companyId,location.getId());
		if(eventList!=null && eventList.size()>0){
			for(Event event : eventList){
				Integer ownerId = event.getOwner().getId();
				Employee owner = employeeDAO.findById(ownerId,companyId);
				event.setOwner(owner);
				Employee salesPerson = employeeDAO.findById(event.getCreatedBy(),companyId);
				event.setSalesPerson(salesPerson);
				Integer pendingSchCnt = scheduleDAO.findPendingScheduleForEvent(event.getId(), companyId);
				if(pendingSchCnt>0)
					event.setPendingSchedule(true);
			}
		}
		return eventList;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Event getEvent(Integer id, Tenant tenant, Region region) {
		
		Event event = eventDAO.findById(id,tenant.getId(),region.getId());
		/*Employee salesPerson = employeeDAO.findById(event.getCreatedBy(),tenant.getId());
		event.setSalesPerson(salesPerson);*/
		return event;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Event getEventDetail(Integer id, Tenant tenant) {
		
		return eventDAO.findDetailById(id, tenant.getId());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Event> getAllPublishedEvents(Tenant tenant,CustomUser user) {
		Integer companyId = (tenant!=null) ? tenant.getId() : null;
		List<Event> eventList = eventDAO.findAll(companyId,user);
		for(Event event : eventList){
			Integer pendingSchCnt = scheduleDAO.findPendingScheduleForEvent(event.getId(), companyId);
			if(pendingSchCnt>0)
				event.setPendingSchedule(true);
		}
		return eventList;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<EventPosition> getAllAvailableEmployeesByPosition(Event event,Tenant tenant) {
		List<EventPosition> allEventPositions = null;
		long startTime = System.currentTimeMillis();
			allEventPositions = eventPositionDAO.findAllEventPositionByEvent(event.getId(),tenant.getId(),event.getEventRegion().getId());
			List<Employee> sickEmployees = timeoffDAO.findAllCallOutByDate(event.getStartDate(),event.getTenant().getId());
			for(EventPosition eventPosition : allEventPositions){
				Position position = eventPosition.getPosition();
				List<Employee> employees = position.getEmployees()!=null?position.getEmployees():new ArrayList<Employee>(); // store available employees here
				List<Employee> reqdEmployees = eventPosition.getReqdAvailabilityEmployees(); // employee that are assigned to this positions, if null means display no Employees assigned
				List<Employee> allScheduledEmployeesByDate = scheduleDAO.findAllScheduledEmployeesByDate(eventPosition.getStartDate(),tenant.getId());
				List<Integer> overlappingEventPositions = eventPositionDAO.findOverlappingEventPosition(event, eventPosition);
				Iterator<Employee> overlappingIterator = allScheduledEmployeesByDate.iterator();
				while(overlappingIterator.hasNext()){
					Employee employee = overlappingIterator.next();
					Integer conflictEventPositionId = employee.getEventPositionId();
					if(conflictEventPositionId.intValue()!=eventPosition.getId() && !overlappingEventPositions.contains(employee.getEventPositionId()))
						overlappingIterator.remove();
					/*else
						System.out.print(employee.getEventPositionId());*/
				}
				if(reqdEmployees.size()>0){
					
					for(Employee employee : reqdEmployees){
						List<Location> restrictEmpLoc = employeeDAO.findRestrictEmployeeLocation(employee.getId(), tenant.getId());
						
						if(!allScheduledEmployeesByDate.contains(employee) && !restrictEmpLoc.contains(event.getLocation())){ // these are all overlapping employees which needs to be filtered
							List<Availability> allAvailabilityByDate = availabilityDAO.findAllAvailableTime(eventPosition.getStartDate(),eventPosition.getEndDate(), employee.getId(), tenant.getId());
							
							Date evntStartTime = DateUtil.getDateAndTime(event.getStartDate(), event.getStartTime());
							Date evntEndTim = DateUtil.getDateAndTime(event.getEndDate(), event.getEndTime());
							if(evntStartTime!=null && evntEndTim!=null){
								for(Availability availability : allAvailabilityByDate){
									Date ast = DateUtil.getDateAndTime(availability.getAvailDate(),availability.getStartTime());
									Date aet = DateUtil.getDateAndTime(availability.getAvailDate(),availability.getEndTime());
									if(ast!=null && aet!=null){
										
										Date epst = DateUtil.getDateAndTime(eventPosition.getStartDate(), eventPosition.getStartTime());
										Date epet = DateUtil.getDateAndTime(eventPosition.getEndDate(), eventPosition.getEndTime());
										if(epst!=null && epet!=null){
											int a = DateUtil.compareDateAndTime(ast,epet);
											int b = DateUtil.compareDateAndTime(epst,aet);
											if(a>0 || b>0){
												
											}
											else{
												// considering no shift will start after midnight
												if(DateUtil.compareDateAndTime(ast,epst)<=0 && DateUtil.compareDateAndTime(aet,epet)>=0){
													employee.setAvailableLabel("Avail.png");
													employee.setAvailable(true);
												}
												else{
													if(DateUtil.isMidnight(availability.getEndTime())){
														if(DateUtil.compareDateAndTime(ast,epst)<=0 && DateUtil.compareDateAndTime(aet,epet,3)>=0){
															employee.setAvailableLabel("Avail.png");
															employee.setAvailable(true);
														}
														else{
															employee.setAvailableLabel("NAvail.png");
															employee.setAvailable(false);
														}
													}else{
														employee.setAvailableLabel("NAvail.png");
														employee.setAvailable(false);
													}
												}
												
												employee.setAllAvailability(allAvailabilityByDate);
												if(!employees.contains(employee)){
													// check if declined before
													Integer declined = scheduleDAO.findIfEmployeeDeclinedBefore(eventPosition.getStartDate(),employee.getId(),tenant.getId());
													if(declined>0)
														employee.setDeclinedBefore(true);
													employees.add(employee);
													//check if call out submitted
													if(sickEmployees.contains(employee)){
														employee.setSick(true);
														employee.setSickStyle("width:65%;color:green");
													}else
														employee.setSickStyle("width:65%;");
												}
											}
										}
									}
								}
							}
						}
					}
						
					EmployeeUtil.sortEmployeeBySchedule(employees);
					position.setEmployees(employees);
			}else{
				position.setNoEmpAssign(true);
				EmployeeUtil.sortEmployeeBySchedule(employees);
				position.setEmployees(employees);
			}
					
				
		}
			
		/*}
		else
			allEventPositions = eventPositionDAO.findPositionEmployeeByEvent(tenant.getId(), event.getId());*/
			long endTime = System.currentTimeMillis();

			System.out.println("That took " + (endTime - startTime) + " milliseconds");
		return allEventPositions;
	}
	
	public List<EventPosition> getTempAllAvailableEmployeesByPosition(Event event,Tenant tenant,Region region) {
		List<EventPosition> allEventPositions = null;
		long startTime = System.currentTimeMillis();
			allEventPositions = eventPositionDAO.findTempAllEventPositionByEvent(event.getId(),tenant.getId(),event.getEventRegion().getId());
			Integer total_EmpCnt = 0;
			for(EventPosition eventPosition : allEventPositions){
				Position position = eventPosition.getPosition();
				
				List<Employee> reqdEmployees = eventPositionDAO.findTempAllAvailabileEmployees(event, eventPosition, tenant, region);
				
				if(reqdEmployees.size()>0){
					List<Integer> declinedEmps = eventPositionDAO.findAllDeclinedEmployeesByEventPosition(event,eventPosition,tenant,region);
					for(Integer i : declinedEmps){
						Employee declineEmployee = new Employee(i);
						int index = reqdEmployees.indexOf(declineEmployee);
						if(index!=-1)
							reqdEmployees.get(index).setDeclinedBefore(true);
							
					}
						position.setEmployees(reqdEmployees);
				}
				else{
					position.setNoEmpAssign(true);
					
					position.setEmployees(new ArrayList<Employee>());
				}
				total_EmpCnt = total_EmpCnt+position.getEmployees().size();
				
			}
			if(total_EmpCnt>500){
				eventDAO.updateEventStatus(event.getId(),event.getTenant().getId());
			}
			long endTime = System.currentTimeMillis();
			System.out.println("That took " + (endTime - startTime) + " milliseconds");
			return allEventPositions;
	}
	private boolean isAvailabilityContinuous(EventPosition eventPosition, Employee employee, Tenant tenant) {
/*		List<Availability> allAvailabilityByDate = availabilityDAO.findAllAvailableTime(eventPosition.getStartDate(),eventPosition.getEndDate(), employee.getId(), tenant.getId());
		if(allAvailabilityByDate!=null && allAvailabilityByDate.size()>1){
			Availability a1 = allAvailabilityByDate.get(0);
			Availability a2 = allAvailabilityByDate.get(1);
			int size = allAvailabilityByDate.size();
			while(size!=0){
				if()
			}
		}
*/		
		return true;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Event> getAllPublishedEventsByDateRange(Tenant tenant,Date start, Date end,CustomUser customUser) {
		Integer companyId = (tenant!=null) ? tenant.getId() : null;
		List<Event> eventList = eventDAO.findAllByDateRange(companyId,start,end,customUser);
		if(eventList!=null && eventList.size()>0){
			for(Event event : eventList){
				Location location = eventDAO.findDetailById(event.getId(), companyId).getLocation();
				event.setLocation(location);
				Integer ownerId = event.getOwner().getId();
				Employee owner = employeeDAO.findById(ownerId,companyId);
				event.setOwner(owner);
				Integer pendingSchCnt = scheduleDAO.findPendingScheduleForEvent(event.getId(), companyId);
				if(pendingSchCnt>0){
					event.setPendingSchedule(true);
					
				}
				
			}
		}
		return eventList;
	}
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Event> getAllPublishedEventsByDateRangeAndLocation(Tenant tenant,Date start, Date end,CustomUser customUser,Location selectedLocation,Region currentRegion) {
		Integer companyId = (tenant!=null) ? tenant.getId() : null;
		List<Event> eventList = eventDAO.findAllByDateRangeAndLocation(companyId,start,end,customUser,selectedLocation,currentRegion);
		
		if(eventList!=null && eventList.size()>0){
			for(Event event : eventList){
				
				List<Map<String,Object>> schCnt = eventPositionDAO.findScheduleCountForEvent(event.getId(), companyId,currentRegion.getId());
				for(Map<String, Object> m : schCnt){
				   BigDecimal requiredCnt = (BigDecimal)m.get("reqdNum");
				   Integer rCnt = 0;
				   if(requiredCnt!=null)
					   rCnt = requiredCnt.intValue();
				   Long staffed = (Long)m.get("schNum");
				   event.setPositionRequiredCount(rCnt);
				   Integer sCnt = staffed.intValue();
				   event.setPositionStaffedCount(sCnt);
				   event.setPositionOpenCount(rCnt-sCnt);
				   Integer pendingSchCnt = rCnt-sCnt;
					if(pendingSchCnt>0){
						List<Map<String,Object>> notifiedCntMap = eventPositionDAO.findNotifiedCountForEvent(event.getId(), companyId,currentRegion.getId());
						
						Iterator<Map<String, Object>> items = notifiedCntMap.iterator();
						boolean isRed = false;
						int totalCnt = 0;
				        while(items.hasNext()){
				            Map<String, Object> row = (Map<String, Object>) items.next();
				            Integer reqdNumber = 0;
				            if(row.get("reqd_number")!=null)
				            	reqdNumber =  (Integer)row.get("reqd_number");
				            Long notifiedCnt = 0L;
				            if(row.get("schNum")!=null)
				            	notifiedCnt = (Long)row.get("schNum");
				            
				            if(reqdNumber - notifiedCnt.intValue()>0){
				            	isRed = true;
				            	
				            }
				            totalCnt = totalCnt+notifiedCnt.intValue();
				            
				        }
						event.setPendingNotificationCount(totalCnt-sCnt);
						if(isRed){
							event.setStatusFlag("Red_flag.gif");
							event.setColorCode("pending-schedule");
						}
						else{
							event.setStatusFlag("Yellow_flag.gif");
							event.setColorCode("booked-schedule");
						}
						
						event.setPendingSchedule(true);
						
					}else{
						event.setPendingNotificationCount(0);
					}
				} 
				
			}
		}
		return eventList;
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Event> getAllEventsByEmployee(Employee employee,TimeOffRequest timeOff,
			Tenant tenant) {
		Date timeoffDate = timeOff.getBeginDate();
		Integer employeeId = employee.getId();
		List<Event> events = eventDAO.findAllByEmployee(timeoffDate,employeeId,tenant.getId());
		return events;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteEventAndPositions(Event event,Tenant tenant) {
		// TODO Auto-generated method stub
		eventDAO.delete(event, tenant.getId());
		List<EventPosition> assignedEventPositions = event.getAssignedEventPosition();
		if(assignedEventPositions!=null && assignedEventPositions.size()>0)
		{
			eventPositionDAO.delete(event,assignedEventPositions,tenant);
			/*for(EventPosition eventPosition : assignedEventPositions){
				eventPositionDAO.delete(event,eventPosition,tenant);
			}*/
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Schedule> deletePublishedEvent(Event event, Tenant tenant) {
		Integer signInOutId = signInOutDAO.findIfExist(event.getId(),tenant.getId());
		List<Schedule> allDeletedSchedules = null;
		if(signInOutId == null){
			allDeletedSchedules = new ArrayList<Schedule>();
			eventDAO.delete(event, tenant.getId());
			List<EventPosition> eventPositions = eventPositionDAO.findSchedulesByEvent(event.getId(),tenant.getId());
			
			if(eventPositions!=null && eventPositions.size()>0)
			{
				eventPositionDAO.delete(event,eventPositions,tenant);
				for(EventPosition eventPosition : eventPositions){
					Position position = eventPosition.getPosition();
					List<Employee> pos_scheduledEmps = position.getScheduledEmployees(); // get scheduled employees for position
					List<Schedule> deleteSchedules = new ArrayList<Schedule>();
					for(Employee employee : pos_scheduledEmps){
						Schedule schedule = new Schedule();
						schedule.setId(employee.getSchedule().getId());
						schedule.setEventPositionId(eventPosition.getId());
						schedule.setEventPosition(eventPosition);
						schedule.setEmployee(employee);
						schedule.setActive(false);
						schedule.setTenant(tenant);
						Map<Object,Object> attributes = populateEmailAttributes(event,eventPosition,employee,position,tenant);
						/**/
						SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
						attributes.put("shiftStartTime",timeformat.format(eventPosition.getStartTime()));
						attributes.put("shiftEndTime",timeformat.format(eventPosition.getEndTime()));
						schedule.setEmailAttributes(attributes);
						deleteSchedules.add(schedule);
					}
					scheduleDAO.deleteSchedule(deleteSchedules);
					allDeletedSchedules.addAll(deleteSchedules);
				}
			}
			
			
		}
		return allDeletedSchedules;
	}
	
	private Map<Object, Object> populateEmailAttributes(Event event,EventPosition eventPosition, Employee employee,Position position,Tenant tenant) {
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put("emailsubject", " YOU ARE CONFIRMED TO WORK "+tenant.getName()+" Schedule ");
		/*attributes.put("companyName", tenantContext.getTenant().getName());
		String contextRoot = FacesUtils.getServletContext().getContextPath().substring(1);
		attributes.put("contextRoot", contextRoot);*/
		attributes.put("firstname",employee.getFirstname());
		attributes.put("lastname", employee.getLastname());
		attributes.put("eventName", event.getName());
		attributes.put("employeePosition",position.getName());
		attributes.put("locName",event.getLocation().getName());
		attributes.put("locAddress1",event.getLocation().getAddress1());
		String locAddr2 = (event.getLocation().getAddress2()==null)?"":event.getLocation().getAddress2();
		attributes.put("locAddress2",locAddr2);
		attributes.put("locCity",event.getLocation().getCity());
		attributes.put("locState",event.getLocation().getState().getStateName());
		attributes.put("locZipcode",event.getLocation().getZipcode());
		SimpleDateFormat dateformat = new SimpleDateFormat("EEEEEEEEE, MMMMMMMM d, yyyy");
		SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
		attributes.put("eventDate",dateformat.format(event.getStartDate()));
		attributes.put("shiftStartTime",timeformat.format(eventPosition.getStartTime()));
		attributes.put("shiftEndTime",timeformat.format(eventPosition.getEndTime()));
		return attributes;
	}

	@Override
	public List<EventType> getAllEventTypesByRegion(Tenant tenant,
			Region selectedRegion) {
		return eventDAO.findAllEventTypeByRegion(tenant,selectedRegion);
	}

	@Override
	public EventType addEventType(EventType eventType) {
		Integer id = eventDAO.saveEventType(eventType);
		eventType.setId(id);
		return eventType;
	}

	@Override
	public List<EventType> getAllEventTypes(Tenant tenant) {
		return eventDAO.findAllEventTypes(tenant);
	}

	@Override
	public void updateEventType(EventType eventType) {
		eventDAO.updateEventType(eventType);
	}

	@Override
	public List<Event> getAllSchedulesByEmployee(Date rosterStartDate,
			Date rosterEndDate, Employee selectedEmployee, Tenant tenant,
			Region selectedRegion) {
		
		return eventDAO.findAllSchedulesByEmployee(rosterStartDate,rosterEndDate, selectedEmployee, tenant.getId(), selectedRegion.getId());
	}

	@Override
	public List<DressCode> getAllDressCodes(Tenant tenant, Region region) {
		return dressCodeDAO.findAll(tenant,region);
	}

	@Override
	public void updateDressCode(DressCode dressCode) {
		dressCodeDAO.update(dressCode);
		
	}

	@Override
	public void saveDressCode(DressCode dressCode) {
		dressCodeDAO.save(dressCode);
	}
	
	
}
