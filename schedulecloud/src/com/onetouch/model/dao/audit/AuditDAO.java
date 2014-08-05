package com.onetouch.model.dao.audit;





import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.joda.time.Hours;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.AuditLog;
import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.EmailGroup;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.SalesPersonLocation;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.mapper.auditlog.AuditLogRowMapper;
import com.onetouch.model.mapper.availability.AvailabilityRowMapper;
import com.onetouch.model.mapper.availability.EmployeeWithAvailabilityExtractor;
import com.onetouch.model.mapper.employee.EmployeePositionExtractor;
import com.onetouch.model.mapper.employee.EmployeeRateRowMapper;
import com.onetouch.model.mapper.employee.EmployeeRowMapper;
import com.onetouch.model.mapper.employee.EmployeeWithRateMapper;
import com.onetouch.model.mapper.location.LocationRowMapper;

/**
 * 
 * @author duttam
 * 
 */
@Repository
@Qualifier("auditDAO")
public class AuditDAO extends BaseDAO{

	private final String createAuditLogSql = "insert into scloud_audit.auditlog(employee_id,record_id,object_type,operation,audit_date,description,region_id,company_id) values (?,?,?,?,?,?,?,?)"; 
	public List<AuditLog> findAll(Integer companyId, Integer regionId) {
		return getJdbcTemplate().query("select * from scloud_audit.auditlog where company_id = ? and region_id = ? ",new Object[]{companyId,regionId}, new AuditLogRowMapper());
		
	}
	public List<AuditLog> findAll(Integer companyId, Integer regionId, Integer employeeId) {
		return getJdbcTemplate().query("select * from scloud_audit.auditlog where company_id = ? and region_id = ? and employee_id = ?",new Object[]{companyId,regionId,employeeId}, new AuditLogRowMapper());
		
	}
	public Integer save(final AuditLog auditLog) {
		
		getJdbcTemplate().update(createAuditLogSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, auditLog.getEmployeeId());
				ps.setInt(2, auditLog.getRecordId());
				ps.setString(3, auditLog.getObjectType());
				ps.setString(4, auditLog.getOperation());
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				ps.setString(5,dateformat.format(auditLog.getAuditDate()));
				ps.setString(6, auditLog.getDescription());
				ps.setInt(7, auditLog.getRegionId());
				ps.setInt(8, auditLog.getCompanyId());
				
			}
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
}
