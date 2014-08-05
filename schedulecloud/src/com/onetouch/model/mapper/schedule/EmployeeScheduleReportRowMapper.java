package com.onetouch.model.mapper.schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.util.DateUtil;

public class EmployeeScheduleReportRowMapper implements RowMapper<Schedule>{

	
	@Override
	public Schedule mapRow(ResultSet rs, int row) throws SQLException {
		Schedule schedule = new Schedule();
		schedule.setId(rs.getInt("scheduleId"));
		
		
		Event event = new Event();
		event.setId(rs.getInt("eventId"));
		event.setName(rs.getString("eventName"));
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			event.setStartDate(dateformat.parse(rs.getString("startDate")));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		schedule.setEvent(event);
		SignInOut signInOut = new SignInOut();
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
		int workingHours = 0,workingMinutes = 0,breakHrs=0,breakMins=0;
		try {
			String startTime = rs.getString("startTime");
			if(startTime!=null)
				signInOut.setStartTime(format.parse(startTime));
			String endTime = rs.getString("endTime");
			if(endTime!=null)
				signInOut.setEndTime(format.parse(endTime));
			String breakStartTime = rs.getString("breakStartTime");
			if(breakStartTime!=null)
				signInOut.setBreakStartTime(format.parse(breakStartTime));
			String breakEndTime = rs.getString("breakEndTime");
			if(breakEndTime!=null)
				signInOut.setBreakEndTime(format.parse(breakEndTime));
			if(signInOut.getStartTime()!=null && signInOut.getEndTime()!=null){
				if(DateUtil.compareTimeOnly(signInOut.getStartTime(), signInOut.getEndTime())<=0){
					workingHours = DateUtil.getHours(signInOut.getStartTime(), signInOut.getEndTime());
					workingMinutes = DateUtil.getMinutes(signInOut.getStartTime(), signInOut.getEndTime());
				}else{
				workingHours = 23 - (DateUtil.getHours(signInOut.getEndTime(),signInOut.getStartTime()));
				workingMinutes = 60-(DateUtil.getHours(signInOut.getEndTime(),signInOut.getStartTime()));
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedule.setSignInOut(signInOut);
		schedule.setWorkingHours(workingHours);
		schedule.setWorkingMinutes(workingMinutes);
		schedule.setBreakHours(breakHrs);
		schedule.setBreakMinutes(breakMins);
		return schedule;
	}

}
