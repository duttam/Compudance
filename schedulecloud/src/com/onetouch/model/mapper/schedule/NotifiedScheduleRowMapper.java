package com.onetouch.model.mapper.schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;
 
import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;

public class NotifiedScheduleRowMapper implements RowMapper<Schedule>{

	@Override
	public Schedule mapRow(ResultSet rs, int row) throws SQLException {
		Schedule schedule = new Schedule();
		schedule.setId(rs.getInt("scheduleId"));
		schedule.setSchedulestatus(rs.getInt("scheduleStatus"));
		schedule.setActive(rs.getBoolean("active"));
		EventPosition eventPosition = new EventPosition();
		eventPosition.setId(rs.getInt("eventPosId"));
		SimpleDateFormat scdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			schedule.setExpiretime(scdateformat.parse(rs.getString("notify_expiretime")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
		try {
			eventPosition.setStartTime(format.parse(rs.getString("positionStartTime")));
			eventPosition.setEndTime(format.parse(rs.getString("positionEndTime")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eventPosition.setNotes(rs.getString("eventPosNotes"));
		schedule.setEventPosition(eventPosition);
		
		Event event = new Event();
		event.setId(rs.getInt("eventId"));
		event.setName(rs.getString("eventName"));
		event.setDressCode(rs.getString("eventDressCode"));
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			event.setStartDate(dateformat.parse(rs.getString("startDate")));
			event.setEndDate(dateformat.parse(rs.getString("endDate")));
			String startTime = rs.getString("startTime");
			if(startTime!=null)
				event.setStartTime(format.parse(startTime));
			String endTime = rs.getString("endTime");
			if(endTime!=null)
				event.setEndTime(format.parse(endTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		schedule.setEvent(event);
		
		Location location = new Location();
		location.setName(rs.getString("locName"));
		location.setCode(rs.getString("locCode"));
		location.setAddress1(rs.getString("locAddr1"));
		location.setAddress2(rs.getString("locAddr2"));
		location.setCity(rs.getString("locCity"));
		//location.setState(new State(rs.getString("locStateCode"), rs.getString("locStateName")));
		location.setZipcode(rs.getString("locZipCode"));
		location.setContactName(rs.getString("locContactName"));
		location.setContactPhone(rs.getString("locPhone"));
		location.setContactEmail(rs.getString("locEmail"));
		schedule.getEvent().setLocation(location);
		
		Position position = new Position();
		//position.setId(rs.getInt("positionId"));
		position.setName(rs.getString("positionName"));
		
		schedule.setPosition(position);
		Employee employee = new Employee();
		if(rs.getString("firstname")!=null)
			employee.setFirstname(rs.getString("firstname"));
		if(rs.getString("lastname")!=null)
			employee.setLastname(rs.getString("lastname"));
		schedule.setEmployee(employee);
		return schedule;
	}
	
}
