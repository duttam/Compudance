package com.onetouch.model.mapper.emailstatus;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.AuditLog;
import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.EmailStatus;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.view.util.DateUtil;

public class EmailStatusRowMapper implements RowMapper<EmailStatus> {

	@Override
	public EmailStatus mapRow(ResultSet rs, int row) throws SQLException {
		EmailStatus emailStatus  = new EmailStatus();
		emailStatus.setId(rs.getInt("id"));
		emailStatus.setFromEmail(rs.getString("from_email"));
		emailStatus.setToEmail(rs.getString("to_email"));
		emailStatus.setOperation(rs.getString("operation"));
		emailStatus.setContent(rs.getString("content"));
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			emailStatus.setCreateTime(dateformat.parse(rs.getString("create_time")));
			if(rs.getString("update_time")!=null)
				emailStatus.setUpdateTime(dateformat.parse(rs.getString("update_time")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		emailStatus.setSubject(rs.getString("subject"));
		emailStatus.setStatus(rs.getString("status"));
		emailStatus.setRegionId(rs.getInt("region_id"));
		emailStatus.setCompanyId(rs.getInt("tenant_id"));
		emailStatus.setEmployeeId(rs.getInt("employee_id"));
		emailStatus.setScheduleId(rs.getInt("schedule_id"));
		return emailStatus;
	}

	}