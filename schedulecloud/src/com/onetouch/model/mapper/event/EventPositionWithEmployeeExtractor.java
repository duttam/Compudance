package com.onetouch.model.mapper.event;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;

import com.onetouch.model.domainobject.Tenant;

public class EventPositionWithEmployeeExtractor implements ResultSetExtractor<List<EventPosition>>{
	
	@Override
	public List<EventPosition> extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		
		Map<Integer,EventPosition> eventPositionMap = new HashMap<Integer, EventPosition>();
		EventPosition eventPosition = null;
		while(rs.next()){
			Integer posId = rs.getInt("eposId");
			eventPosition = eventPositionMap.get(posId);
			if(eventPosition==null){
				eventPosition = new EventPosition();
				eventPosition.setId(rs.getInt("eposId"));
				Position position = new Position();
				position.setId(rs.getInt("position_id"));
				position.setName(rs.getString("posName"));
				eventPosition.setPosition(position);
				SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
				try {
					eventPosition.setStartTime(timeformat.parse(rs.getString("start_time")));
					eventPosition.setEndTime(timeformat.parse(rs.getString("end_time")));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				eventPosition.setReqdNumber(rs.getInt("reqd_number"));
				eventPosition.setDisplayOrder(rs.getInt("displayOrder"));
				eventPositionMap.put(posId,eventPosition);
			}
			List<Employee> allAvailableEmployee = eventPosition.getPosition().getEmployees();
			if(allAvailableEmployee==null){
				allAvailableEmployee = new ArrayList<Employee>();
				eventPosition.getPosition().setEmployees(allAvailableEmployee);
				
			}
			
			
			Integer employeeId = rs.getInt("emp_id");
			if(employeeId>0){
				Employee employee = new Employee();
				employee.setId(employeeId);
				employee.setFirstname(rs.getString("firstname"));
				employee.setLastname(rs.getString("lastname"));
				employee.setImageName(rs.getString("image_name"));
				employee.setImageType(rs.getString("image_type"));
				employee.setInputStream(rs.getBinaryStream("image_bytes"));
				employee.setRating(rs.getInt("rating"));
				allAvailableEmployee.add(employee);
			}
			else
			{}
		
		}
		return new ArrayList<EventPosition>(eventPositionMap.values());
	}
}
