package com.onetouch.model.mapper.availability;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.view.util.DateUtil;

public class AvailabilityRowMapper implements RowMapper<Availability> {

	@Override
	public Availability mapRow(ResultSet rs, int row) throws SQLException {
		Availability availability = new Availability();
		availability.setId(rs.getInt("id"));
		availability.setTitle(rs.getString("title"));
		/*SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
		try {
			Date startTime = timeformat.parse(rs.getString("starttime"));
			Date endTime = timeformat.parse(rs.getString("endtime"));
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(startTime);
			int startHr = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
			int i = calendar.get(Calendar.HOUR);   
			int startMin = calendar.get(Calendar.MINUTE);
			calendar.setTime(endTime);
			int endHr = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
			i = calendar.get(Calendar.HOUR);   
			int endMin = calendar.get(Calendar.MINUTE);
			
			availability.setStartHr(startHr);
			availability.setStartMinute(startMin);
			
			availability.setEndHr(endHr);
			availability.setEndMinute(endMin);
			availability.setStartTime(timeformat.parse(rs.getString("starttime")));
			availability.setEndTime(timeformat.parse(rs.getString("endtime")));
			availability.setsTime(rs.getString("starttime"));
			availability.seteTime(rs.getString("endtime"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		try {
			availability.setStartTime(timeformat.parse(rs.getString("starttime")));
			availability.setEndTime(timeformat.parse(rs.getString("endtime")));

			Date testStartTime = DateUtil.getDate("06:00 AM","hh:mm a");
			Date testEndTime = DateUtil.getDate("11:59 PM","hh:mm a");
			int a = DateUtil.compareTimeOnly(availability.getStartTime(),testStartTime);
			int b = DateUtil.compareTimeOnly(availability.getEndTime(),testEndTime);
			
			if(a==0 && b==0){
				availability.setAllday(true);
				//availability.setTitle("Available All Day");
			}
			else{
				availability.setAllday(false);
				//Date testStartTime = DateUtil.getDate("00:00 AM","hh:mm a");
			}
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
		Date d = DateUtil.getDateAndTime(availability.getAvailDate(),availability.getStartTime());
		availability.setTenant(new Tenant(rs.getInt("company_id")));
		availability.setEmployee(new Employee(rs.getInt("employee_id")));
		return availability;
	}

	}