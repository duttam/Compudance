package com.onetouch.model.mapper.event;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.EventType;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;

import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;

public class EventWithEmployeeScheduleExtractor implements ResultSetExtractor<List<Event>>{

	private Employee selectedEmployee;
	public EventWithEmployeeScheduleExtractor(){
		
	}
	public EventWithEmployeeScheduleExtractor(Employee selectedEmployee){
		this.selectedEmployee = selectedEmployee;
	}
	
	@Override
	public List<Event> extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		
		Map<Integer,Event> eventMap = new LinkedHashMap<Integer, Event>();
		Event event = null;
		while(rs.next()){
			Integer id = rs.getInt("id");
			event = eventMap.get(id);
			if(event==null){
					event = new Event();
					event.setId(rs.getInt("id"));
					event.setName(rs.getString("name"));
					event.setOwner(new Employee(rs.getInt("owner_id")));
					event.setCoordinator(new Employee(rs.getInt("coordinator_id")));
					SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
					try {
						event.setStartTime(timeformat.parse(rs.getString("start_time")));
						event.setEndTime(timeformat.parse(rs.getString("end_time")));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
					try {
						event.setStartDate(dateformat.parse(rs.getString("event_startdate")));
						event.setEndDate(dateformat.parse(rs.getString("event_enddate")));
						
						
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					event.setDressCode(rs.getString("dress_code"));
					event.setNotes(rs.getString("Notes"));
					event.setDescription(rs.getString("description"));
					event.setHostname(rs.getString("host"));
					event.setStatus(rs.getString("status"));
					event.setLocation(new Location(rs.getInt("location_id")));
					event.setTenant(new Tenant(rs.getInt("company_id")));
					event.setCreatedBy(rs.getInt("created_by"));
					event.setGuestCount(rs.getInt("guest_count"));
					/*Employee creator = new Employee();
					creator.setId(rs.getInt("creator_id"));
					creator.setFirstname(rs.getString("creator_firstname"));
					creator.setLastname(rs.getString("creator_lastname"));
					event.setSalesPerson(creator);*/
					EventType eventType = new EventType();
					eventType.setId(rs.getInt("eventtype_id"));
					eventType.setName(rs.getString("eventtype_name"));
					event.setEventType(eventType);
					event.setSalesPersonNotes(rs.getString("salesPersonNote"));
					Employee admin = new Employee(rs.getInt("adminId"));
					admin.setFirstname(rs.getString("adminFirstname"));
					admin.setLastname(rs.getString("adminLastname"));
					admin.setHomephone(rs.getString("adminHomephone"));
					event.setOwner(admin);
					Employee manager = new Employee(rs.getInt("managerId"));
					manager.setFirstname(rs.getString("managerFirstname"));
					manager.setLastname(rs.getString("managerLastname"));
					event.setCoordinator(manager);
					Employee salesPerson = new Employee(rs.getInt("salesPersonId"));
					salesPerson.setFirstname(rs.getString("salesPersonFirstname"));
					salesPerson.setLastname(rs.getString("salesPersonLastname"));
					salesPerson.setHomephone(rs.getString("salesHomephone"));
					event.setSalesPerson(salesPerson);
					
					Location location = new Location();
					location.setId(rs.getInt("locId"));
					location.setName(rs.getString("locName"));
					location.setCode(rs.getString("code"));
					location.setAddress1(rs.getString("address1"));
					location.setAddress2(rs.getString("address2"));
					location.setCity(rs.getString("city"));
					State state = new State();
					state.setStateName(rs.getString("state"));
					location.setState(state);
					location.setZipcode(rs.getString("zipcode"));
					location.setContactName(rs.getString("contact_name"));
					location.setContactEmail(rs.getString("email"));
					event.setLocation(location);
					eventMap.put(id,event);
			}
			List<EventPosition> assignedEventPositions = event.getAssignedEventPosition();
			if(assignedEventPositions==null){
				assignedEventPositions = new ArrayList<EventPosition>();
				event.setAssignedEventPosition(assignedEventPositions);
			}
			
			
			Integer eventPositionId = rs.getInt("eventPosId");
			if(eventPositionId>0){
				EventPosition eventPosition = new EventPosition(event);
				eventPosition.setId(eventPositionId);
				eventPosition.setEvent_id(id);
				Position position = new Position();
				Integer positionId = rs.getInt("position_id");
				String positionname = rs.getString("position_name");
				position.setId(positionId);
				position.setName(positionname);
				eventPosition.setPosition(position);
				List<Employee> scheduledEmps = new ArrayList<Employee>();
				Schedule schedule = new Schedule();
				schedule.setId(rs.getInt("schId"));
				schedule.setSchedulestatus(rs.getInt("scheduleStatus"));
				selectedEmployee.setSchedule(schedule);
				scheduledEmps.add(selectedEmployee);
				position.setScheduledEmployees(scheduledEmps);
				SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
				DateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
				try {
					String sdate = rs.getString("start_date");
					String edate = rs.getString("end_date");
					String stime=rs.getString("starttime");
					String etime = rs.getString("endtime");
					
					event.setStartDate(dateformat.parse(sdate));
					event.setEndDate(dateformat.parse(edate));
					eventPosition.setStartTime(dateTimeFormat.parse(sdate+" "+stime));
					eventPosition.setEndTime(dateTimeFormat.parse(edate+" "+etime));
					eventPosition.setNotes(rs.getString("evntPosNotes"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				eventPosition.setReqdNumber(rs.getInt("reqd_number"));
				eventPosition.setDisplayOrder(rs.getInt("displayOrder"));
				assignedEventPositions.add(eventPosition);
			}
			else
			{}
		
		}
		return new ArrayList<Event>(eventMap.values());
	}
}
