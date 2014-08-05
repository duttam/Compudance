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


public class EmployeeWithRateMapper implements RowMapper<Employee> {
	
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
		employee.setSpecialNotes(rs.getString("specialnotes"));
		employee.setCode(rs.getInt("code"));
		employee.setEmergencyPhone(rs.getString("emergency_phone"));
		employee.setEmergencyContact(rs.getString("emergency_contact"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			
			if(rs.getString("dob")!=null)
				employee.setDob(format.parse(rs.getString("dob")));
			if(rs.getString("hiredate")!=null)
				employee.setHireDate(format.parse(rs.getString("hiredate")));
			
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
		 
		employee.setRating(rs.getInt("rating"));
		employee.setScAdmin(rs.getBoolean("scAdmin"));
		employee.setScManager(rs.getBoolean("scManager"));
		EmployeeRate employeeRate = new EmployeeRate();
		employeeRate.setId(rs.getInt("empRateId"));
		employeeRate.setEmployeeId(employeeId);
		employeeRate.setHourlyRate(rs.getBigDecimal("rate"));
		employeeRate.setOldHourlyRate(rs.getBigDecimal("rate"));
		try {
			if(rs.getString("start_date")!=null){
				employeeRate.setRateStartDate(format.parse(rs.getString("start_date")));
				employeeRate.setOldRateStartDate(format.parse(rs.getString("start_date")));
			}
			if(rs.getString("end_date")!=null)
				employeeRate.setRateEnddate(format.parse(rs.getString("end_date")));
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		employee.setEmpRate(employeeRate);
		employee.setScSalesPerson(rs.getBoolean("scSalesPerson"));
		return employee;
	}

	}