package com.onetouch.model.dao.dresscode;





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
import com.onetouch.model.domainobject.DressCode;
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
@Qualifier("dressCodeDAO")
public class DressCodeDAO extends BaseDAO{

	private final String createDressCodeSql = "insert into dresscode(name,description,region_id,company_id) values (?,?,?,?)"; 
	public List<DressCode> findAll(final Tenant tenant,final Region region) {
		return getJdbcTemplate().query("select * from dresscode where company_id = ? and region_id = ? ",new Object[]{tenant.getId(),region.getId()}, 
				new RowMapper<DressCode>() {

					@Override
					public DressCode mapRow(ResultSet rs, int row)
							throws SQLException {
						DressCode dressCode = new DressCode(); 
						dressCode.setId(rs.getInt("id"));
						dressCode.setName(rs.getString("name"));
						dressCode.setDescription(rs.getString("description"));
						dressCode.setTenant(tenant);
						dressCode.setRegion(region);
						return dressCode;
					}
		});
		
	}
	public Integer save(final DressCode dressCode) {
		
		getJdbcTemplate().update(createDressCodeSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, dressCode.getName());
				ps.setString(2, dressCode.getDescription());
				ps.setInt(3, dressCode.getRegion().getId());
				ps.setInt(4, dressCode.getTenant().getId());
				
			}
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	public void update(final DressCode dressCode) {
		getJdbcTemplate().update("update dresscode set name = ?, description = ? where region_id = ? and company_id = ? and id = ?", new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, dressCode.getName());
				ps.setString(2, dressCode.getDescription());
				ps.setInt(3, dressCode.getRegion().getId());
				ps.setInt(4, dressCode.getTenant().getId());
				ps.setInt(5, dressCode.getId());
			}
		});
		
	}
	
}
