package com.onetouch.model.mapper.schedule;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;


import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Location;



public class ScheduleRowMapper implements RowMapper<Schedule> {

	@Override
	public Schedule mapRow(ResultSet rs, int row) throws SQLException {
		Schedule schedule = new Schedule();
		schedule.setId(rs.getInt("id"));
		schedule.setSchedulestatus(rs.getInt("status"));
		schedule.setActive(rs.getBoolean("active"));
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			schedule.setExpiretime(dateformat.parse(rs.getString("notify_expiretime")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(rs.getObject("event_pos_id")!=null)
			schedule.setEventPositionId(rs.getInt("event_pos_id"));
		if(rs.getObject("employee_id")!=null)
			schedule.setEmployee(new Employee(rs.getInt("employee_id")));
		if(rs.getObject("company_id")!=null)
			schedule.setTenant(new Tenant(rs.getInt("company_id")));
			
		return schedule;
	}

}