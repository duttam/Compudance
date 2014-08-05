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

public class EmployeeScheduleRowMapper implements RowMapper<Schedule>{

	@Override
	public Schedule mapRow(ResultSet rs, int row) throws SQLException {
		Schedule schedule = new Schedule();
		schedule.setId(rs.getInt("scheduleId"));
		
		EventPosition eventPosition = new EventPosition();
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
		try {
			eventPosition.setStartTime(format.parse(rs.getString("positionStartTime")));
			eventPosition.setEndTime(format.parse(rs.getString("positionEndTime")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedule.setEventPosition(eventPosition);
		
		Event event = new Event();
		event.setId(rs.getInt("eventId"));
		event.setName(rs.getString("eventName"));
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
		event.setDressCode(rs.getString("eventDressCode"));
		event.setNotes(rs.getString("eventNotes"));
		event.setHostname(rs.getString("eventHost"));
		event.setDescription(rs.getString("eventDesc"));
		schedule.setEvent(event);
		
		Location location = new Location();
		//location.setId(rs.getInt("locId"));
		location.setAddress1(rs.getString("locAddr1"));
		location.setAddress2(rs.getString("locAddr2"));
		location.setCity(rs.getString("locCity"));
		//location.setState(new State(rs.getString("locStateCode"), rs.getString("locStateName")));
		location.setZipcode(rs.getString("locZipCode"));
		location.setContactName(rs.getString("locContactName"));
		location.setContactPhone(rs.getString("locPhone"));
		location.setContactEmail(rs.getString("locEmail"));
		if(rs.getString("parking_direction")!=null){
			location.setParkingDirection(rs.getString("parking_direction"));
		}
		schedule.getEvent().setLocation(location);
		
		Position position = new Position();
		position.setViewReports(rs.getBoolean("view_report"));
		position.setName(rs.getString("positionName"));
		position.setNotes(rs.getString("positionNotes")); // this is actually event position notes saved in the event position table
		schedule.setPosition(position);
		SignInOut signInOut = new SignInOut();
		
		try {
			String startTime = rs.getString("start_time");
			if(startTime!=null)
				signInOut.setStartTime(format.parse(startTime));
			String endTime = rs.getString("end_time");
			if(endTime!=null)
				signInOut.setEndTime(format.parse(endTime));
			String breakStartTime = rs.getString("break_start");
			if(breakStartTime!=null)
				signInOut.setBreakStartTime(format.parse(breakStartTime));
			String breakEndTime = rs.getString("break_end");
			if(breakEndTime!=null)
				signInOut.setBreakEndTime(format.parse(breakEndTime));
				signInOut.setRemarks(rs.getString("remarks"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedule.setSignInOut(signInOut);
		return schedule;
	}
	
}
