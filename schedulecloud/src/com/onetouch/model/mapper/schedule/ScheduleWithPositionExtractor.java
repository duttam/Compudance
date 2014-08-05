package com.onetouch.model.mapper.schedule;

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
import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;

import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.util.DateUtil;

public class ScheduleWithPositionExtractor implements  ResultSetExtractor<List<Position>>{

	private Integer tenantId;
	

	public ScheduleWithPositionExtractor() {
		super();
	}


	public ScheduleWithPositionExtractor(Integer tenantId) {
		super();
		this.tenantId = tenantId;
	}


	@Override
	public List<Position> extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		
		Map<Integer,Position> positionMap = new HashMap<Integer, Position>();
		Position position = null;
		/*if(tenantId!=null)
			System.out.print(tenantId);*/
		while(rs.next()){
			Integer id = rs.getInt("position_id");
			position = positionMap.get(id);
			if(position==null){
				position = new Position();
				position.setId(id);
				position.setName(rs.getString("posname"));
				
				positionMap.put(id,position);
				
			}
			
			List<Employee> scheduledEmployees = position.getScheduledEmployees();
			if(scheduledEmployees==null){
				scheduledEmployees = new ArrayList<Employee>();
				position.setScheduledEmployees(scheduledEmployees);
			}
			
			
			Integer employeeId = rs.getInt("employee_id");
			if(employeeId>0){
				Employee employee = new Employee();
				employee.setId(employeeId);
				
				employee.setFirstname(rs.getString("firstname"));
				employee.setLastname(rs.getString("lastname"));
				employee.setScheduleId(rs.getInt("schId"));
				SignInOut signInOut = new SignInOut();
				signInOut.setId(rs.getInt("signinid"));
				//signInOut.setSchedule_id(rs.getInt("schedule_id"));
				SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
				int workingHours = 0;
				try {
					String startTime = rs.getString("start_time");
					if(startTime!=null)
						signInOut.setStartTime(format.parse(rs.getString("start_time")));
					String endTime = rs.getString("end_time");
					if(endTime!=null)
						signInOut.setEndTime(format.parse(endTime));
					String breakStartTime = rs.getString("break_start");
					if(breakStartTime!=null)
						signInOut.setBreakStartTime(format.parse(breakStartTime));
					String breakEndTime = rs.getString("break_end");
					if(breakEndTime!=null)
						signInOut.setBreakEndTime(format.parse(breakEndTime));
						signInOut.setRemarks(rs.getString("remarks"));
						
						/*if(signInOut.getStartTime()!=null && signInOut.getEndTime()!=null){
							if(DateUtil.compareTimeOnly(signInOut.getStartTime(),signInOut.getEndTime())<0)
								workingHours = DateUtil.getHours(signInOut.getStartTime(),signInOut.getEndTime());
							else
								workingHours = 24 - (DateUtil.getHours(signInOut.getEndTime(),signInOut.getStartTime()));
						}*/
						
						
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int shiftHrs=0,shiftMins=0,breakHrs=0,breakMins =0;
				if(signInOut.getStartTime()!=null && signInOut.getEndTime()!=null){
					if(DateUtil.compareTimeOnly(signInOut.getStartTime(),signInOut.getEndTime())<=0){
						shiftHrs = DateUtil.getHours(signInOut.getStartTime(),signInOut.getEndTime());
						shiftMins = DateUtil.getMinutes(signInOut.getStartTime(),signInOut.getEndTime());
					}
					else{// calculation needs to be done properly
						shiftHrs = 23 - (DateUtil.getHours(signInOut.getEndTime(),signInOut.getStartTime()));
						shiftMins = 60-(DateUtil.getHours(signInOut.getEndTime(),signInOut.getStartTime()));
					}
				}
				if(signInOut.getBreakStartTime()!=null && signInOut.getBreakEndTime()!=null){
					if(DateUtil.compareTimeOnly(signInOut.getBreakStartTime(),signInOut.getBreakEndTime())<=0){
						breakHrs = DateUtil.getHours(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
						breakMins = DateUtil.getMinutes(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
					}
					else{
						breakHrs = 24 - DateUtil.getHours(signInOut.getBreakEndTime(),signInOut.getBreakStartTime());
						breakMins = 60 - DateUtil.getMinutes(signInOut.getBreakEndTime(),signInOut.getBreakStartTime());
					}
				}
				employee.setHours(shiftHrs);
				employee.setMinutes(shiftMins);
				employee.setBreakHours(breakHrs);
				employee.setBreakMinutes(breakMins);
				employee.setSignInOut(signInOut);
				scheduledEmployees.add(employee);
			}
			
		
		}
		
		return new ArrayList<Position>(positionMap.values());
	}

	
	
}
