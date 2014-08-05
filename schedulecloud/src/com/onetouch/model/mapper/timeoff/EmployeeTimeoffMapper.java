package com.onetouch.model.mapper.timeoff;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.TimeOffRequest;

    public class EmployeeTimeoffMapper implements RowMapper<TimeOffRequest> {

	@Override
	public TimeOffRequest mapRow(ResultSet rs, int row) throws SQLException {
		TimeOffRequest timeOff = new TimeOffRequest();		
		timeOff.setRequestType(rs.getString("time_off_type"));
		
		timeOff.setEmployeeId(rs.getInt("employee_id"));
		//String emp_firstname = rs.getString("firstname");
		//String emp_lastname = rs.getString("lastname");
		//timeOff.setEmpName(emp_firstname+" "+emp_lastname);
                timeOff.setRequestType(rs.getString("time_off_type"));
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        		try {
        			timeOff.setBeginDate(dateformat.parse(rs.getString("start_date")));
                    timeOff.setEndDate(dateformat.parse(rs.getString("end_date")));
        			//event.setEventDate(dateformat.parse(rs.getString("event_date")));
        			//event.setStartDate(dateformat.parse(rs.getString("event_date")));
        			//event.setEndDate(dateformat.parse(rs.getString("event_date")));
        			
        		} catch (ParseException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		timeOff.setNumDays(rs.getInt("num_days"));
        		timeOff.setReason(rs.getString("reason"));
                timeOff.setCreateDate(rs.getDate("create_date"));
                timeOff.setCompanyId(rs.getInt("company_id"));
                if(timeOff.getRequestType().equalsIgnoreCase("Sick")){
                	Event event = new Event();
                	event.setId(rs.getInt("eventId"));
                	event.setName(rs.getString("eventName"));
                	timeOff.setSickEvent(event);
                }
		return timeOff;
	}

    }