package com.onetouch.model.mapper.auditlog;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.AuditLog;
import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.view.util.DateUtil;

public class AuditLogRowMapper implements RowMapper<AuditLog> {

	@Override
	public AuditLog mapRow(ResultSet rs, int row) throws SQLException {
		AuditLog auditLog = new AuditLog();
		auditLog.setId(rs.getInt("id"));
		auditLog.setEmployeeId(rs.getInt("employee_id"));
		auditLog.setRecordId(rs.getInt("record_id"));
		auditLog.setObjectType(rs.getString("object_type"));
		auditLog.setOperation(rs.getString("operation"));
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			auditLog.setAuditDate(dateformat.parse(rs.getString("audit_date")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		auditLog.setDescription(rs.getString("description"));
		auditLog.setId(rs.getInt("region_id"));
		auditLog.setId(rs.getInt("company_id"));
		return auditLog;
	}

	}