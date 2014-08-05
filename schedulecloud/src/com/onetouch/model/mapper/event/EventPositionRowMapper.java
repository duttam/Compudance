package com.onetouch.model.mapper.event;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;

import com.onetouch.model.domainobject.Tenant;

public class EventPositionRowMapper implements RowMapper<EventPosition>{
	
		@Override
	public EventPosition mapRow(ResultSet rs, int row) throws SQLException {
		EventPosition eventPosition = new EventPosition();
		eventPosition.setId(rs.getInt("id"));
		eventPosition.setEvent_id(rs.getInt("event_id"));
		Position position = new Position();
		position.setId(rs.getInt("position_id"));
		eventPosition.setPosition(position);
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
		try {
			eventPosition.setStartTime(timeformat.parse(rs.getString("start_time")));
			eventPosition.setEndTime(timeformat.parse(rs.getString("end_time")));
			eventPosition.setStartDate(dateformat.parse(rs.getString("start_date")));
			eventPosition.setEndDate(dateformat.parse(rs.getString("end_date")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		eventPosition.setReqdNumber(rs.getInt("reqd_number"));
		eventPosition.setDisplayOrder(rs.getInt("displayOrder"));
		eventPosition.setCompanyId(rs.getInt("company_id"));		
		return eventPosition;
	}
}
