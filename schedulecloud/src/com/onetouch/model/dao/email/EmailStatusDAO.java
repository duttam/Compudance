package com.onetouch.model.dao.email;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.EmailStatus;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.mapper.auditlog.AuditLogRowMapper;
import com.onetouch.model.mapper.emailstatus.EmailStatusRowMapper;

@Repository
@Qualifier("emailStatusDAO")
public class EmailStatusDAO extends BaseDAO{

	private final String createEmployeeEmailStatusSql = "insert into emailstatus(from_email,to_email,subject,operation,content,create_time,employee_id,region_id,tenant_id,status) values (?,?,?,?,?,?,?,?,?,?)";
	private final String createSendNotesSql = "insert into emailstatus(from_email,to_email,subject,operation,content,create_time,region_id,tenant_id,status) values (?,?,?,?,?,?,?,?,?)";
	private final String createScheduleEmailStatusSql = "insert into emailstatus(from_email,to_email,subject,operation,content,create_time,employee_id,schedule_id,region_id,tenant_id,status) values (?,?,?,?,?,?,?,?,?,?,?)";
	public int saveEmployeeEmail(final EmailStatus emailStatus){
		getJdbcTemplate().update(createEmployeeEmailStatusSql, new PreparedStatementSetter() {
		
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, emailStatus.getFromEmail());
				ps.setString(2,emailStatus.getToEmail());
				ps.setString(3,emailStatus.getSubject());
				ps.setString(4,emailStatus.getOperation());
				ps.setString(5,emailStatus.getContent());
				SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				ps.setString(6,timeformat.format(emailStatus.getCreateTime()));
				ps.setInt(7,emailStatus.getEmployeeId());
				ps.setInt(8,emailStatus.getRegionId());
				ps.setInt(9,emailStatus.getCompanyId());
				ps.setString(10, emailStatus.getStatus());
			}
		});
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}

	public void updateStatus(final Integer recordId,final  String status) {
		getJdbcTemplate().update("update emailstatus set update_time = ?, status = ? where id = ?", new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				ps.setString(1,timeformat.format(new Date()));
				ps.setString(2,status);
				ps.setInt(3,recordId);
				
			}
		});
	}

	public Integer saveScheduleEmail(final EmailStatus emailStatus) {
		getJdbcTemplate().update(createScheduleEmailStatusSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, emailStatus.getFromEmail());
				ps.setString(2,emailStatus.getToEmail());
				ps.setString(3,emailStatus.getSubject());
				ps.setString(4,emailStatus.getOperation());
				ps.setString(5,emailStatus.getContent());
				SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				ps.setString(6,timeformat.format(emailStatus.getCreateTime()));
				ps.setInt(7,emailStatus.getEmployeeId());
				ps.setInt(8,emailStatus.getScheduleId());
				ps.setInt(9,emailStatus.getRegionId());
				ps.setInt(10,emailStatus.getCompanyId());
				ps.setString(11, emailStatus.getStatus());
			}
		});
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public int saveScheduleNoteEmail(final EmailStatus emailStatus){
		getJdbcTemplate().update(createSendNotesSql, new PreparedStatementSetter() {
		
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, emailStatus.getFromEmail());
				ps.setString(2,emailStatus.getToEmail());
				ps.setString(3,emailStatus.getSubject());
				ps.setString(4,emailStatus.getOperation());
				ps.setString(5,emailStatus.getContent());
				SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				ps.setString(6,timeformat.format(emailStatus.getCreateTime()));
				
				ps.setInt(7,emailStatus.getRegionId());
				ps.setInt(8,emailStatus.getCompanyId());
				ps.setString(9, emailStatus.getStatus());
			}
		});
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public List<EmailStatus> findAllPendingEmails() {
		return getJdbcTemplate().query("select * from emailstatus where status = 'resend' or status = 'pending' ",new EmailStatusRowMapper());
		
		
	}

	public List<EmailStatus> findAllEmails(Tenant tenant, Region region) {
		return getJdbcTemplate().query("select * from emailstatus where tenant_id = ? and region_id = ?",new Object[]{tenant.getId(),region.getId()},new EmailStatusRowMapper());
	}

	public void updateToEmail(final Integer id,final String toEmail,final Integer companyId) {
		getJdbcTemplate().update("update emailstatus set to_email = ? where id = ? and tenant_id = ?", new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1,toEmail);
				ps.setInt(2,id);
				ps.setInt(3,companyId);
				
			}
		});
	}

}
