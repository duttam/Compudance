package com.onetouch.model.mapper.signinout;





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
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;

import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.util.DateUtil;

public class EmployeeSignInOutMapper implements RowMapper<Employee>{

	@Override
	public Employee mapRow(ResultSet rs, int i) throws SQLException {
		Position position = new Position();
		position.setId(rs.getInt("position_id"));
		position.setName(rs.getString("posname"));
		
		Employee employee = new Employee();
		employee.setId(rs.getInt("employee_id"));
		employee.setFirstname(rs.getString("firstname"));
		employee.setLastname(rs.getString("lastname"));
		employee.setScheduleId(rs.getInt("schId"));
		SignInOut signInOut = new SignInOut();
		signInOut.setId(rs.getInt("signinid"));
		//signInOut.setSchedule_id(rs.getInt("schedule_id"));
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*if(signInOut.getStartTime()!=null && signInOut.getEndTime()!=null){
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
		}*/
		int shiftHrs = DateUtil.getHours(signInOut.getStartTime(),signInOut.getEndTime());
		int shiftMins = DateUtil.getMinutes(signInOut.getStartTime(),signInOut.getEndTime());
		int breakHrs = DateUtil.getHours(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
		int breakMins = DateUtil.getMinutes(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
		 
		employee.setHours(shiftHrs);
		employee.setMinutes(shiftMins);
		employee.setBreakHours(breakHrs);
		employee.setBreakMinutes(breakMins);
		employee.setSignInOut(signInOut);
		
		employee.setPosition(position);
		employee.setSignInOut(signInOut);
		
		return employee;
	}

	
	
}
