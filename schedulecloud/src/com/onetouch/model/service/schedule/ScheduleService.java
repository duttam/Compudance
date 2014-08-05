package com.onetouch.model.service.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.availability.AvailabilityDAO;
import com.onetouch.model.dao.employee.EmployeeDAO;
import com.onetouch.model.dao.event.EventDAO;
import com.onetouch.model.dao.eventposition.EventPositionDAO;
import com.onetouch.model.dao.position.PositionDAO;
import com.onetouch.model.dao.schedule.OfferScheduleDAO;
import com.onetouch.model.dao.schedule.ScheduleDAO;
import com.onetouch.model.dao.timeoff.TimeoffDAO;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeePosition;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Schedule;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.comparator.PositionDateTimeComparator;
import com.onetouch.view.util.DateUtil;

@Service
public class ScheduleService implements IScheduleService{

	@Autowired
	private ScheduleDAO scheduleDAO;
	@Autowired
	private OfferScheduleDAO offerScheduleDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	
	@Autowired
	private PositionDAO positionDAO;
	@Autowired
	private EventDAO eventDAO;
	@Autowired
	private EventPositionDAO eventPositionDAO;
	@Autowired
	private AvailabilityDAO availabilityDAO;
	@Autowired
	private TimeoffDAO timeoffDAO;
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Schedule> saveSchedules(Event event,List<Schedule> saveSchedules) {
		if(saveSchedules!=null && saveSchedules.size()>0){
			for(Schedule schedule : saveSchedules){
				Integer id = scheduleDAO.save(schedule);
				schedule.setId(id);
			}
		}
		eventDAO.updateEventStatus(event.getId(),event.getTenant().getId());
		return saveSchedules;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Integer updateSchedule(Event event,
			List<Schedule> saveSchedules,List<Schedule> deleteSchedules) {
		Position tempPosition=null;
		
		Event tempEvent=null;
		List<Schedule> updateSchedules = new ArrayList<Schedule>();
		if(saveSchedules!=null && saveSchedules.size()>0){
			
			Iterator<Schedule> saveSchIte = saveSchedules.iterator();
			while(saveSchIte.hasNext()){
				Schedule saveSchedule = saveSchIte.next();
				Schedule updateSchedule = null;
				Integer scheduleId = scheduleDAO.findSchedule(saveSchedule.getEventPositionId(), 
																saveSchedule.getEmployee().getId(), saveSchedule.getTenant().getId());
				// check if schedule already present in the table, if yes add to update schedule list and remove from save schedule list
				if(scheduleId!=null){
					updateSchedule = saveSchedule;
					updateSchedule.setId(scheduleId);
					updateSchedules.add(updateSchedule);
					saveSchIte.remove();
					
				}
			}
			if(updateSchedules.size()>0)
				scheduleDAO.update(updateSchedules); // update schedules
			if(saveSchedules.size()>0)
				scheduleDAO.save(saveSchedules); // save schedules
		}
		if(deleteSchedules!=null && deleteSchedules.size()>0){
			
			scheduleDAO.delete(deleteSchedules);// delete schedules, make schedule inactive
		}
		//get the position, shift and event for the employees
		if(saveSchedules!=null && saveSchedules.size()>0){
			tempPosition = saveSchedules.get(0).getPosition();
			
			tempEvent = saveSchedules.get(0).getEvent();
		}
		if(updateSchedules!=null && updateSchedules.size()>0){
			tempPosition = (tempPosition==null) ? updateSchedules.get(0).getPosition() : tempPosition;
			
			tempEvent = (tempEvent==null) ? updateSchedules.get(0).getEvent() : tempEvent;
		}
		if(deleteSchedules!=null && deleteSchedules.size()>0){
			tempPosition = (tempPosition==null) ? deleteSchedules.get(0).getPosition() : tempPosition;
			
			tempEvent = (tempEvent==null) ? deleteSchedules.get(0).getEvent() : tempEvent;
		}
		
		return 0;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Position> getScheduleByEvent(Event event, Tenant tenant) {
		
		List<Position> scheduledPositionList = scheduleDAO.findByEvent(event.getId(),tenant.getId()); // get all active schedules from schedule tables
		
		return scheduledPositionList;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<EventPosition> getScheduleByEventPosition(Event event, Tenant tenant){
		
		List<EventPosition> eventPositions = eventPositionDAO.findSchedulesByEvent(event.getId(),tenant.getId());
		
		return this.getFinalEventPositions(eventPositions);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<EventPosition> getTransferredEventPositionsByEmployee(
			Employee transferEmployee, Event transferEvent, Tenant tenant) {
		List<EventPosition> eventPositions = eventPositionDAO.findTransferredEventPositionByEmployee(transferEmployee.getId(),transferEvent.getId(),tenant.getId());
		return this.getFinalEventPositions(eventPositions);
		
	}
	public List<EventPosition> getFinalEventPositions(List<EventPosition> eventPositions){
		Map<Integer,List<EventPosition>> orderedEventPositions = new LinkedHashMap<Integer, List<EventPosition>>();
		
		
		for(EventPosition eventPosition : eventPositions){
			Position position = eventPosition.getPosition();
			
			List<EventPosition> groupedEventPositionList = orderedEventPositions.get(position.getId());
			if(groupedEventPositionList==null){
				groupedEventPositionList = new ArrayList<EventPosition>();
				groupedEventPositionList.add(eventPosition);
				orderedEventPositions.put(position.getId(),groupedEventPositionList);
			}else{
				if(!groupedEventPositionList.contains(eventPosition))
					groupedEventPositionList.add(eventPosition);
					
			}
		}
		for(Integer positionId : orderedEventPositions.keySet()){
			List<EventPosition> groupedEventPositionList = orderedEventPositions.get(positionId);
			Collections.sort(groupedEventPositionList,new PositionDateTimeComparator());
		}
		List<EventPosition> finalEventPositions = new ArrayList<EventPosition>();
		for(Integer positionId : orderedEventPositions.keySet()){
			List<EventPosition> groupedEventPositionList = orderedEventPositions.get(positionId);
			finalEventPositions.addAll(groupedEventPositionList);
		}
		return finalEventPositions;
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Map<Integer,EventPosition> getTempScheduleByEventPosition(Event event, Tenant tenant){
		
		return null;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Position> getPositionsByShift(Integer eventId, Integer shiftId,Integer tenantId) {
		return scheduleDAO.findPositionByShift(eventId,shiftId,tenantId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getAvailableEmployeesByEventPosition(
			EventPosition eventPosition, Event event, Tenant tenant, Region region) {
		List<Employee> sickEmployees = timeoffDAO.findAllCallOutByDate(event.getStartDate(),event.getTenant().getId());
		Set<Employee> availableEmployees = new HashSet<Employee>(); 
		List<Employee> reqdEmployees = employeeDAO.findEmployeesByPosition(eventPosition,event,tenant.getId(),region.getId());
		List<Employee> allScheduledEmployeesByDate = scheduleDAO.findAllScheduledEmployeesByDate(eventPosition.getStartDate(),tenant.getId());
		List<Integer> overlappingEventPositions = eventPositionDAO.findOverlappingEventPosition(event, eventPosition);
		Iterator<Employee> overlappingIterator = allScheduledEmployeesByDate.iterator();
		while(overlappingIterator.hasNext()){
			Employee employee = overlappingIterator.next();
			if(!overlappingEventPositions.contains(employee.getEventPositionId()))
				overlappingIterator.remove();
			/*else
				System.out.print(employee.getEventPositionId());*/
		}
		// we need to filter out employees who are scheduled in a shift that has conflict start/end time with the selected shift
		for(Employee employee : reqdEmployees){
			
			List<Location> restrictEmpLoc = employeeDAO.findRestrictEmployeeLocation(employee.getId(), tenant.getId());
			if(!allScheduledEmployeesByDate.contains(employee) && !restrictEmpLoc.contains(event.getLocation())){
				List<Availability> allAvailabilityByDate = availabilityDAO.findAllAvailableTime(eventPosition.getStartDate(),eventPosition.getEndDate(), employee.getId(), tenant.getId());
				for(Availability availability : allAvailabilityByDate){
					Date ast = availability.getStartTime();
					Date aet = availability.getEndTime();
					if(ast!=null && aet!=null){
						
						Date epst = eventPosition.getStartTime();
						Date epet = eventPosition.getEndTime();
						Integer declined = scheduleDAO.findIfEmployeeDeclinedBefore(eventPosition.getStartDate(),employee.getId(),tenant.getId());
						if(declined>0)
							employee.setDeclinedBefore(true);
						if(sickEmployees.contains(employee)){
							employee.setSick(true);
							employee.setSickStyle("width:65%;color:green");
						}else
							employee.setSickStyle("width:65%;");
						if(epst!=null && epet!=null){
							if(DateUtil.compareTimeOnly(ast,epst)<=0 && DateUtil.compareTimeOnly(aet,epet)>=0){
								employee.setAvailableLabel("Avail.png");
								availableEmployees.add(employee);	
							}else if((DateUtil.compareTimeOnly(epst,ast)<=0 && DateUtil.compareTimeOnly(epet,ast)>=0)||(DateUtil.compareTimeOnly(epst,aet)<=0 && DateUtil.compareTimeOnly(epet,aet)>=0)){
								employee.setAvailableLabel("NAvail.png");
								availableEmployees.add(employee);
								
							}else{
								
								// add previously scheduled employees to be deleted from the schedule table
							}
						}
					}
				}
			}	
			
		}
		
		return new ArrayList<Employee>(availableEmployees); 
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getTempAvailableEmployeesByEventPosition(
			EventPosition eventPosition, Event event, Tenant tenant, Region region) {
		event.setEventRegion(region);
		
		List<Employee> reqdEmployees = eventPositionDAO.findTempAllAvailabileEmployees(event, eventPosition, tenant, region);
		List<Integer> declinedEmps = eventPositionDAO.findAllDeclinedEmployeesByEventPosition(event,eventPosition,tenant,region);
		for(Integer i : declinedEmps){
			Employee declineEmployee = new Employee(i);
			int index = reqdEmployees.indexOf(declineEmployee);
			if(index!=-1)
				reqdEmployees.get(index).setDeclinedBefore(true);
				
		}
			
		return reqdEmployees;
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getNewAvailableEmployeesByEventPosition(
			EventPosition eventPosition, Event event, Tenant tenant,Region region) {
		List<Employee> availableEmployees = new ArrayList<Employee>(); 
		List<Employee> reqdEmployees = employeeDAO.findEmployeesByPosition(eventPosition,event,tenant.getId(),region.getId());
		List<Employee> allScheduledEmployeesByDate = scheduleDAO.findAllScheduledEmployeesByDate(eventPosition.getStartDate(),tenant.getId());
		for(Employee employee : reqdEmployees){
			if(!allScheduledEmployeesByDate.contains(employee)){
				List<Availability> allAvailabilityByDate = availabilityDAO.findAllAvailableTime(eventPosition.getStartDate(),eventPosition.getEndDate(), employee.getId(), tenant.getId());
				for(Availability availability : allAvailabilityByDate){
					Date ast = availability.getStartTime();
					Date aet = availability.getEndTime();
					if(ast!=null && aet!=null){
						
						Date epst = eventPosition.getStartTime();
						Date epet = eventPosition.getEndTime();
						if(epst!=null && epet!=null){
							if(DateUtil.compareTimeOnly(ast,epst)<=0 && DateUtil.compareTimeOnly(aet,epet)>=0){
								employee.setAvailableLabel("Avail.png");
								availableEmployees.add(employee);	
							}else if((DateUtil.compareTimeOnly(epst,ast)<=0 && DateUtil.compareTimeOnly(epet,ast)>=0)||(DateUtil.compareTimeOnly(epst,aet)<=0 && DateUtil.compareTimeOnly(epet,aet)>=0)){
								employee.setAvailableLabel("NAvail.png");
								availableEmployees.add(employee);
								
							}else{
								
							}
						}
					}
				}
			}	
			
		}
		List<Employee> pos_scheduledEmps = eventPosition.getPosition().getScheduledEmployees();
		for(Employee employee : pos_scheduledEmps){
			
				List<Availability> allAvailabilityByDate = availabilityDAO.findAllAvailableTime(eventPosition.getStartDate(),eventPosition.getEndDate(), employee.getId(), tenant.getId());
				for(Availability availability : allAvailabilityByDate){
					Date ast = availability.getStartTime();
					Date aet = availability.getEndTime();
					if(ast!=null && aet!=null){
						
						Date epst = eventPosition.getStartTime();
						Date epet = eventPosition.getEndTime();
						if(epst!=null && epet!=null){
							if(DateUtil.compareTimeOnly(ast,epst)<=0 && DateUtil.compareTimeOnly(aet,epet)>=0){
								employee.setAvailableLabel("Avail.png");
								availableEmployees.add(employee);	
							}else if((DateUtil.compareTimeOnly(epst,ast)<=0 && DateUtil.compareTimeOnly(epet,ast)>=0)||(DateUtil.compareTimeOnly(epst,aet)<=0 && DateUtil.compareTimeOnly(epet,aet)>=0)){
								employee.setAvailableLabel("NAvail.png");
								availableEmployees.add(employee);
								
							}else{
								
							}
						}
					}
				}
				
			
		}
		return availableEmployees; 
	}

	
	@Override
	/*@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateTempSchedule(Event event, EventPosition scheduledEventPosition, List<Schedule> saveSchedules, List<Schedule> deleteSchedules) {
		eventPositionDAO.updateTimeAndReqdPositionCount(scheduledEventPosition,event.getTenant().getId());
		List<Schedule> updateSchedules = new ArrayList<Schedule>();
		if(saveSchedules!=null && saveSchedules.size()>0){
			
			Iterator<Schedule> saveSchIte = saveSchedules.iterator();
			while(saveSchIte.hasNext()){
				Schedule saveSchedule = saveSchIte.next();
				Schedule updateSchedule = null;
				Integer scheduleId = scheduleDAO.findSchedule(saveSchedule.getEventPositionId(), saveSchedule.getEmployee().getId(), saveSchedule.getTenant().getId());
				// check if schedule already present in the table, if yes add to update schedule list and remove from save schedule list
				if(scheduleId!=null){
					updateSchedule = saveSchedule;
					updateSchedule.setId(scheduleId);
					updateSchedules.add(updateSchedule);
					saveSchIte.remove();
					
				}
			}
			if(updateSchedules.size()>0)
				scheduleDAO.update(updateSchedules); // update schedules
			if(saveSchedules.size()>0){
				scheduleDAO.save(saveSchedules); // save schedules
				
			}
			
		}
		if(deleteSchedules!=null && deleteSchedules.size()>0){
			
			scheduleDAO.deleteSchedule(deleteSchedules);// delete schedules, make schedule inactive
		}
		
		
	}*/
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Schedule> updateSchedules(Event event, EventPosition scheduledEventPosition, List<Schedule> saveSchedules, List<Schedule> deleteSchedules, List<Schedule> updateSchedules) {
		eventPositionDAO.updateTimeAndReqdPositionCount(scheduledEventPosition,event.getTenant().getId());
		if(saveSchedules!=null && saveSchedules.size()>0)
			
			for(Schedule schedule : saveSchedules){
				Integer scheduleId = scheduleDAO.save(schedule);
				schedule.setId(scheduleId);
			}
			//scheduleDAO.save(saveSchedules);
		if(updateSchedules!=null && updateSchedules.size()>0)
			scheduleDAO.update(updateSchedules);
			
		if(deleteSchedules!=null && deleteSchedules.size()>0)
			scheduleDAO.deleteSchedule(deleteSchedules);// delete schedules, make schedule inactive
		
		
		return saveSchedules;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteScheduleByEventPosition(EventPosition scheduledEventPosition, List<Schedule> deleteSchedules, Tenant tenant) {
		//eventPositionDAO.updateEventPositionStatus(scheduledEventPosition,tenant.getId());
		//scheduleDAO.delete(deleteSchedules);
		eventPositionDAO.deleteEventPosition(scheduledEventPosition,tenant.getId());
		scheduleDAO.deleteSchedule(deleteSchedules);
	}

	/*@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public EventPosition getAvailableEmployeesByPosition(Event event,
			EventPosition customEventPosition) {
	 	List<Employee> availableEmployees = eventPositionDAO.findEmployeeByPosition(event,customEventPosition);
	 	if(availableEmployees.size()==0)
	 		availableEmployees = availabilityDAO.findAvailableEmployees(event,customEventPosition);
	 	customEventPosition.getPosition().setEmployees(availableEmployees);
	 	return customEventPosition;
	}*/

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Schedule> scheduleNewEventPosition(
			EventPosition customEventPosition, List<Schedule> saveSchedules) {
		Integer eventPosCnt = eventPositionDAO.findEventPosCnt(customEventPosition.getEvent().getId(),customEventPosition.getCompanyId());
		customEventPosition.setDisplayOrder(eventPosCnt);
		Integer eventPositionId = eventPositionDAO.save(customEventPosition);
		for(Schedule schedule : saveSchedules){
			schedule.setEventPositionId(eventPositionId);
			Integer id = scheduleDAO.save(schedule);
			schedule.setId(id);
		}
				
		return saveSchedules;
		//scheduleDAO.save(saveSchedules); initially it was batch but i need schedule ids to send in email
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Schedule> getAllScheduledEventPositions(Employee employee,
			Tenant tenant) {
		
		return scheduleDAO.findAllScheduledEventPosition(employee.getId(),tenant.getId());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Schedule> getAllScheduledEventPositionsByDate(
			Employee employee, Tenant tenant, String mode) {
		return scheduleDAO.findAllScheduledEventPositionByDate(employee.getId(),tenant.getId(),mode);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Date getScheduleExpireTime(Integer employeeId, Integer companyId,
			Integer eventPosId) {
		// TODO Auto-generated method stub
		return scheduleDAO.findScheduleExpireTime(employeeId,companyId,eventPosId);
		//return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateScheduleStatus(Integer scheduleId,Integer employeeId, Integer companyId,
			Integer eventPosId, Integer status) {
		scheduleDAO.updateStatus(scheduleId,employeeId,companyId,eventPosId,status);
	}

	@Override
	public List<Schedule> getAllNotifiedSchedules(Employee employee,
			Tenant tenant) {
		
		return scheduleDAO.findAllNotifiedSchedules(employee.getId(),tenant.getId());
	}

	@Override
	public Schedule getSchedule(Integer scheduleId,Integer eventPositionId, Integer employeeId,
			Integer companyId) {
		return scheduleDAO.getSchedule(scheduleId,eventPositionId, employeeId, companyId);
	}

	@Override
	public List<EventPosition> getNotifiedScheduleByEventPosition(Event event,Tenant tenant) {
		Map<Integer,List<EventPosition>> orderedEventPositions = new LinkedHashMap<Integer, List<EventPosition>>();
		List<EventPosition> eventPositions = scheduleDAO.findNotifiedSchedulesByEvent(event.getId(),tenant.getId());
		return eventPositions;
	}

	@Override
	public void updateEventPositionNotes(EventPosition scheduledEventPosition) {
		eventPositionDAO.updateEventPosition(scheduledEventPosition);
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void saveOverrideSchedules(Event event,EventPosition scheduledEventPosition, List<Schedule> overrideSchedules) {
		eventPositionDAO.updateTimeAndReqdPositionCount(scheduledEventPosition,event.getTenant().getId());
		scheduleDAO.overrideSchedules(overrideSchedules);
	}

	@Override
	public List<Schedule> getAllNotifiedEventPositionsByDate(Employee employee,
			Tenant tenant, Date eventDate) {
		// TODO Auto-generated method stub
		return scheduleDAO.findAllNotifiedEventPosition(employee.getId(),tenant.getId(),eventDate);
	}

	@Override
	public List<Employee> getAllScheduledEmployeeByEventPosition(EventPosition scheduledEventPosition, Tenant tenant) {
		return eventPositionDAO.findAllScheduledEmployeeByEventPosition(scheduledEventPosition,tenant);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Schedule> saveOfferSchedules(Event event,EventPosition scheduledEventPosition, List<Schedule> offerSchedules,List<Schedule> deleteSchedules) {
		eventPositionDAO.updateOfferNotes(scheduledEventPosition,event.getTenant().getId());
		for(Schedule schedule : offerSchedules){
			Integer id = offerScheduleDAO.offerSchedules(schedule);
			schedule.setId(id);
		}
		for(Schedule schedule : deleteSchedules){
			scheduleDAO.expireOldNotifiedSchedulesByEventPosition(schedule.getId(),scheduledEventPosition.getId(),event.getTenant().getId());
		}
			
		return offerSchedules;
		
	}

	@Override
	public Schedule getOfferSchedule(Integer scheduleId, Integer eventPosId,
			Integer employeeId, Integer companyId) {
		return offerScheduleDAO.findOfferedSchedule(scheduleId,eventPosId,employeeId,companyId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public boolean saveOfferedSchedule(Schedule schedule, int status) {
		int offerCount = offerScheduleDAO.findOfferCountByEventPosition(schedule.getEventPositionId());
		offerScheduleDAO.updateOfferedSchedule(schedule,status);
		if(offerCount>0){
			offerScheduleDAO.saveOfferedSchedule(schedule,status);
			offerScheduleDAO.updateOfferCountByEventPosition(offerCount-1, schedule.getEventPositionId());
			return true;
		}else
			return false;
	}

	@Override
	public List<Employee> getAllOfferedEmployeesByEventPosition(
			EventPosition offerEventPosition, Tenant tenant) {
		return offerScheduleDAO.findOfferedEmployeesByEventPosition(offerEventPosition, tenant);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Schedule reAssignEmployee(Schedule schedule, List<Schedule> deleteSchedules) {
		Integer scheduleId = scheduleDAO.save(schedule);
		schedule.setId(scheduleId);
		scheduleDAO.deleteSchedule(deleteSchedules);
		return schedule;
	}

	

	
	
	
}
