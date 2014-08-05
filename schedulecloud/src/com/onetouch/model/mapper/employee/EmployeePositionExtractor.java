package com.onetouch.model.mapper.employee;

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
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.State;

public class EmployeePositionExtractor implements ResultSetExtractor<List<Employee>>{

	private LobHandler lobHandler =new DefaultLobHandler();;
	
	@Override
	public List<Employee> extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		
		Map<Integer,Employee> employeeMap = new HashMap<Integer, Employee>();
		Employee employee = null;
		while(rs.next()){
			Integer id = rs.getInt("id");
			employee = employeeMap.get(id);
			if(employee==null){
				employee = new Employee();
				employee.setId(rs.getInt("id"));
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
				if(rs.getString("getsms")!=null && rs.getString("getsms").equalsIgnoreCase("Y"))
					employee.setGetSMS(true);
				else
					employee.setGetSMS(false);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					
					if(rs.getString("dob")!=null)
						employee.setDob(format.parse(rs.getString("dob")));
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				employee.setImageName(rs.getString("image_name"));
				employee.setImageType(rs.getString("image_type"));
				//employee.setImageBytes(lobHandler.getBlobAsBytes(rs,"image_bytes"));
				employee.setInputStream(rs.getBinaryStream("image_bytes"));
				//employee.setHourlyRate(rs.getBigDecimal("hourly_rate"));
				employee.setAdminNotes(rs.getString("adminnotes"));
				employee.setEmployeeStatus(rs.getString("status"));
				employee.setScAdmin(rs.getBoolean("scAdmin"));
				employee.setScManager(rs.getBoolean("scManager"));
				employee.setScSalesPerson(rs.getBoolean("scSalesPerson"));
				
				employee.setCode(rs.getInt("code"));
				employee.setEmergencyPhone(rs.getString("emergency_phone"));
				employee.setEmergencyContact(rs.getString("emergency_contact"));
				employeeMap.put(id,employee);
			}
			List<Position> allAssignedPositions = employee.getAssignedPositions();
			if(allAssignedPositions==null){
				allAssignedPositions = new ArrayList<Position>();
				employee.setAssignedPositions(allAssignedPositions);
			}
			
			
			Integer employeePositionId = rs.getInt("position_id");
			if(employeePositionId>0){
				Position position = new Position();
				position.setId(employeePositionId);
				position.setName(rs.getString("name"));
				position.setDescription(rs.getString("description"));
				position.setNotes(rs.getString("notes"));
				allAssignedPositions.add(position);
			}
			
		
		}
		return new ArrayList<Employee>(employeeMap.values());
	}
}
