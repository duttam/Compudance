package com.onetouch.model.mapper.event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;


import com.onetouch.model.domainobject.EventType;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Location;



public class EventHomeRowMapper implements RowMapper<Event> {

	@Override
	public Event mapRow(ResultSet rs, int row) throws SQLException {
		Event event = new Event();
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
			//event.setEventDate(dateformat.parse(rs.getString("event_date")));
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
		Employee creator = new Employee();
		creator.setId(rs.getInt("creator_id"));
		creator.setFirstname(rs.getString("creator_firstname"));
		creator.setLastname(rs.getString("creator_lastname"));
		event.setSalesPerson(creator);
		event.setGuestCount(rs.getInt("guest_count"));
		EventType eventType = new EventType();
		eventType.setId(rs.getInt("eventtype_id"));
		eventType.setName(rs.getString("eventtype_name"));
		event.setEventType(eventType);
		event.setSalesPersonNotes(rs.getString("salesPersonNote"));
		return event;
	}

	}