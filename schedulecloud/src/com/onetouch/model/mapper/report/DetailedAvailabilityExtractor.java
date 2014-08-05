package com.onetouch.model.mapper.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.DetailedAvailReport;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Schedule;


public class DetailedAvailabilityExtractor implements ResultSetExtractor<List<DetailedAvailReport>>{
	
	@Override
	public List<DetailedAvailReport> extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		
		Map<Integer,DetailedAvailReport> detailAvailMap = new LinkedHashMap<Integer, DetailedAvailReport>();
		DetailedAvailReport detailedAvailReport = null;
		while(rs.next()){
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm:ss");
			Integer employeeId = rs.getInt("empId");
			detailedAvailReport =  detailAvailMap.get(employeeId);
			if(detailedAvailReport==null){
				detailedAvailReport = new DetailedAvailReport();
				Employee employee = new Employee();
				employee.setId(employeeId);
				employee.setFirstname(rs.getString("firstname"));
				employee.setLastname(rs.getString("lastname"));
				
				detailedAvailReport.setEmployee(employee);
				Location location = new Location();
				location.setName(rs.getString("loc_name"));
				location.setCode(rs.getString("loc_code"));
				detailedAvailReport.setLocation(location);
				Event event = new Event();
				event.setId(rs.getInt("event_id"));
				event.setName(rs.getString("event_name"));
				detailedAvailReport.setEvent(event);
				detailAvailMap.put(employeeId, detailedAvailReport);
			}
				Set<Availability> availabilities = detailedAvailReport.getAvailabilities();
				if(availabilities==null){
					availabilities = new HashSet<Availability>();
					detailedAvailReport.setAvailabilities(availabilities);
				}
				Availability availability = new Availability();
				try {
					availability.setId(rs.getInt("avlId"));
                	availability.setStartTime(timeformat.parse(rs.getString("starttime")));
                	availability.setEndTime(timeformat.parse(rs.getString("endtime")));
                    

                } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                availabilities.add(availability);
				Set<Schedule> schedules = detailedAvailReport.getSchedules();
				if(schedules==null){
					schedules = new HashSet<Schedule>();
					detailedAvailReport.setSchedules(schedules);
				}
				
				Schedule schedule = new Schedule();
				
				schedule.setId(rs.getInt("SchId"));
                try {
                	EventPosition eventPosition = new EventPosition();
                	eventPosition.setScheduleStatus(rs.getString("status"));
                	if(rs.getString("start_time")!=null)
                		eventPosition.setStartTime(timeformat.parse(rs.getString("start_time")));
                	if(rs.getString("end_time")!=null)
                		eventPosition.setEndTime(timeformat.parse(rs.getString("end_time")));
                	schedule.setEventPosition(eventPosition);
                    

                } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }  
                schedules.add(schedule);
               
		}
            
		return new ArrayList<DetailedAvailReport>(detailAvailMap.values());
	}
	
}
