package com.onetouch.model.mapper.event;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;


public class EventDetailExtractor implements ResultSetExtractor<List<Event>>{

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
				event.setId(id);
				event.setName(rs.getString("name"));
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
				
				event.setStatus(rs.getString("status"));
				event.setDressCode(rs.getString("dress_code"));
				event.setNotes(rs.getString("Notes"));
				event.setDescription(rs.getString("description"));
				event.setHostname(rs.getString("host"));
				event.setEventRegion(new Region(rs.getInt("region_id")));
				eventMap.put(id,event);
			}
			Integer locationId = rs.getInt("loc_id");
			if(locationId>0){
				Location location = new Location();
				location.setName(rs.getString("loc_name"));
				location.setCode(rs.getString("loc_code"));
				location.setAddress1(rs.getString("loc_address1"));
				location.setCity(rs.getString("loc_city"));
				location.setState(new State(rs.getString("loc_statecode"),rs.getString("loc_statename")));
				location.setContactName(rs.getString("loc_contactname"));
				location.setZipcode(rs.getString("zipcode"));
				location.setContactEmail(rs.getString("email"));
				if(rs.getString("parking_direction")!=null){
					location.setParkingDirection(rs.getString("parking_direction"));
				}
				event.setLocation(location);
			}
			Integer ownerId = rs.getInt("owner_id");
			if(ownerId>0){
				Employee owner = new Employee(ownerId);
				owner.setFirstname(rs.getString("owner_firstname"));
				owner.setLastname(rs.getString("owner_lastname"));
				event.setOwner(owner);
			}
			Integer coordinatorId = rs.getInt("coordinator_id");
			if(coordinatorId>0){
				Employee coordinator = new Employee(coordinatorId);
				coordinator.setFirstname(rs.getString("coordinator_firstname"));
				coordinator.setLastname(rs.getString("coordinator_lastname"));
				event.setCoordinator(coordinator);
			}
		}
		return new ArrayList<Event>(eventMap.values());
	}

}
