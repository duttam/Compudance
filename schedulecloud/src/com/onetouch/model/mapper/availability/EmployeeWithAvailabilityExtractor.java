package com.onetouch.model.mapper.availability;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.State;
import com.onetouch.view.util.DateUtil;


public class EmployeeWithAvailabilityExtractor implements ResultSetExtractor<List<Employee>>{

	@Override
	public List<Employee> extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		
		Map<Integer,Employee> employeeMap = new HashMap<Integer, Employee>();
		Employee employee = null;
		while(rs.next()){
			Integer id = rs.getInt("id");
			employee = employeeMap.get(id);
			if(employee==null){
				employee  = new Employee();
				employee.setId(rs.getInt("id"));
				employee.setFirstname(rs.getString("firstname"));
				employee.setLastname(rs.getString("lastname"));
				employee.setAddress1(rs.getString("address1"));
				employee.setAddress2(rs.getString("address2"));
				employee.setCity(rs.getString("city"));
				State state = new State();
				state.setStateName(rs.getString("state"));
				state.setStateCode(rs.getString("statecode"));
				employee.setState(state);
				employee.setZipcode(rs.getString("zipcode"));
				employee.setHomephone(rs.getString("homephone"));
				employee.setCellphone(rs.getString("cellphone"));
				employee.setFax(rs.getString("fax"));
				employee.setEmail(rs.getString("email"));
				if(rs.getString("getemail")!=null && rs.getString("getemail").equalsIgnoreCase("Y"))
					employee.setGetEmail(true);
				else
					employee.setGetEmail(false);
				if(rs.getString("getemail")!=null && rs.getString("getsms").equalsIgnoreCase("N"))
					employee.setGetSMS(true);
				else
					employee.setGetSMS(false);
				
				employee.setImageName(rs.getString("image_name"));
				employee.setImageType(rs.getString("image_type"));
				//employee.setImageBytes(rs.getBytes("image_bytes"));
				employee.setInputStream(rs.getBinaryStream("image_bytes"));
				//employee.setHourlyRate(rs.getBigDecimal("hourly_rate"));
				employee.setAdminNotes(rs.getString("adminnotes"));
				
				employeeMap.put(id,employee);
			}
			List<Availability> allAvailability = employee.getAllAvailability();
			if(allAvailability==null){
				allAvailability = new ArrayList<Availability>();
				employee.setAllAvailability(allAvailability);
			}
			
			Integer employeeAvailabilityId = rs.getInt("availability_id");
			if(employeeAvailabilityId>0){
				Availability availability = new Availability();
				availability.setId(employeeAvailabilityId);
				availability.setTitle(rs.getString("title"));
				SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
				try {
					availability.setStartTime(timeformat.parse(rs.getString("starttime")));
					availability.setEndTime(timeformat.parse(rs.getString("endtime")));
					Date testStartTime = DateUtil.getDate("00:00 AM","hh:mm a");
					Date testEndTime = DateUtil.getDate("11:59 PM","hh:mm a");
					int a = DateUtil.compareTimeOnly(availability.getStartTime(),testStartTime);
					int b = DateUtil.compareTimeOnly(availability.getEndTime(),testEndTime);
					
					if(a==0 && b==0)
						availability.setAllday(true);
					else
						availability.setAllday(false);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					availability.setAvailDate(dateformat.parse(rs.getString("avail_date")));
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				allAvailability.add(availability);
			}
			
		}
		return new ArrayList<Employee>(employeeMap.values());
	}
}