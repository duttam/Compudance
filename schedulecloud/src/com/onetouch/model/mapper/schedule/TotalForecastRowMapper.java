package com.onetouch.model.mapper.schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.view.util.DateUtil;

public class TotalForecastRowMapper implements RowMapper<Employee>{

	@Override
	public Employee mapRow(ResultSet rs, int row) throws SQLException {
		Employee employee= new Employee();
		employee.setId(rs.getInt("emp_id"));
		
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
		int workingHours = 0,workingMinutes = 0;
		try {
			String startTime = rs.getString("startTime");
			String endTime = rs.getString("endTime");
			if(startTime!=null && endTime!=null){
				Date sTime = format.parse(startTime);
				Date eTime = format.parse(endTime);
				if(DateUtil.compareTimeOnly(sTime,eTime)<=0){
					workingHours = DateUtil.getHours(sTime,eTime);
					workingMinutes = DateUtil.getMinutes(sTime,eTime);
				}else{
				workingHours = 23 - (DateUtil.getHours(sTime,eTime));
				workingMinutes = 60-(DateUtil.getHours(sTime,eTime));
			}
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		employee.setHours(workingHours);
		employee.setMinutes(workingMinutes);
		
		return employee;
	}

}
