package com.onetouch.model.mapper.employee;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.State;


public class EmployeeRowMapper implements RowMapper<Employee> {
	
	private LobHandler lobHandler =new DefaultLobHandler();;
	
	@Override
	public Employee mapRow(ResultSet rs, int row) throws SQLException {
		
		Employee employee = new Employee();
		Integer employeeId = rs.getInt("id"); 
		employee.setId(employeeId);
		employee.setInvitecode(rs.getString("invitecode"));
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
		//employee.setImageBytes(lobHandler.getBlobAsBytes(rs,"image_bytes"));
		employee.setInputStream(rs.getBinaryStream("image_bytes"));
		//employee.setHourlyRate(rs.getBigDecimal("hourly_rate"));
		employee.setAdminNotes(rs.getString("adminnotes"));
		employee.setEmployeeStatus(rs.getString("status"));
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			
			if(rs.getString("dob")!=null)
				employee.setDob(format.parse(rs.getString("dob")));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		  {
		    rs.findColumn("scheduleId");//no exception means exist
		    if(rs.getObject("scheduleId")!=null)
				employee.setScheduleId(rs.getInt("scheduleId"));
		    
		  } catch (SQLException sqlex)
		  {
		    
		  }
		  try
		  {
		    rs.findColumn("event_pos_id");//no exception means exist
		    if(rs.getObject("event_pos_id")!=null)
		    	employee.setEventPositionId(rs.getInt("event_pos_id"));
				
		    
		  } catch (SQLException sqlex)
		  {
		    
		  }
		 
		employee.setRating(rs.getInt("rating"));

		employee.setScAdmin(rs.getBoolean("scAdmin"));
		employee.setScManager(rs.getBoolean("scManager"));
		employee.setScSalesPerson(rs.getBoolean("scSalesPerson"));
		return employee;
	}

	}