package com.onetouch.model.dao.signinout;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlTypeValue;

import org.springframework.stereotype.Repository;

import com.onetouch.model.dao.BaseDAO;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;



import com.onetouch.model.domainobject.SignInOut;

import com.onetouch.model.mapper.event.EventRowMapper;
import com.onetouch.model.mapper.schedule.ScheduleWithPositionExtractor;

import com.onetouch.model.mapper.signinout.EmployeeSignInOutMapper;
import com.onetouch.model.mapper.signinout.SignInOutMapper;


@Repository
@Qualifier("shiftDAO")
public class SignInOutDAO extends BaseDAO{

	private final String createSignInSql = "insert into signinout(start_time,schedule_id) values (?,?)";
	private final String updateSignOutSql = "update signinout set end_time =? where id = ?";
	private final String updateBreakSignInSql = "update signinout set break_start =? where id = ?";
	private final String updateBreakSignOutSql = "update signinout set break_end =? where id = ?";
	private final String selectSignInOutQuery = "select temp.id as schId, temp.employee_id, temp.firstname, temp.lastname, temp.position_id, temp.name as posname,sio.id as signinid,date_format(sio.start_time,'%l:%i %p') as start_time , date_format(sio.end_time,'%l:%i %p') as end_time, date_format(sio.break_start,'%l:%i %p') as break_start, date_format(sio.break_end,'%l:%i %p') as break_end, sio.remarks from( SELECT s.id, s.employee_id,epos.position_id, p.name, e.firstname, e.lastname FROM event_position epos, schedule s, employee e, position p where s.event_pos_id = epos.id and s.employee_id = e.id and epos.position_id = p.id and epos.event_id = ? and s.active = true and s.company_id = ?) as temp left join signinout sio on temp.id = sio.schedule_id";
	
	/*public List<SignInOut> findAll(final Integer companyId){
		return getJdbcTemplate().query("select id,name,date_format(start_time,'%l:%i %p') as starttime,date_format(end_time,'%l:%i %p') as endtime,shift_color,notes,company_id from shift where shift_type='fix' and company_id = ? ",new Object[]{companyId}, new ShiftRowMapper());
	}*/

	public Integer saveSignInTime(final SignInOut signInOut) {
	
		getJdbcTemplate().update(createSignInSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
				ps.setString(1,sdf.format(signInOut.getStartTime()));
				ps.setInt(2,signInOut.getSchedule_id());
				
			}
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
		
	}

	public void updateSignOutTime(final SignInOut signInOut) {
		
		getJdbcTemplate().update(updateSignOutSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
				ps.setString(1,sdf.format(signInOut.getEndTime()));
				ps.setInt(2,signInOut.getId());
			}
		});
		
	}
	
	public void updateBreakSignInTime(final SignInOut signInOut) {
			
			getJdbcTemplate().update(updateBreakSignInSql, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
					ps.setString(1,sdf.format(signInOut.getBreakStartTime()));
					ps.setInt(2,signInOut.getId());
				}
			});
			
	}

	public void UpdateBreakSignOutTime(final SignInOut signInOut) {
		
		getJdbcTemplate().update(updateBreakSignOutSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
				ps.setString(1,sdf.format(signInOut.getBreakEndTime()));
				ps.setInt(2,signInOut.getId());
			}
		});
		
	}

	public void updateRemark(final SignInOut signInOut) {
		getJdbcTemplate().update("update signinout set remarks =? where id = ?", new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				ps.setString(1,signInOut.getRemarks());
				ps.setInt(2,signInOut.getId());
			}
		});
	}

	public List<Event> findAllSignInOutEvents(final Integer companyId, final CustomUser customUser) {
		if(customUser.isAdmin())
			return getJdbcTemplate().query("select * from event where curdate()= event.event_startdate and event.company_id = ? and status='PUBLISHED' order by event.event_startdate, start_time asc ",new Object[]{companyId}, new EventRowMapper());
		else if(customUser.isManager())
			return getJdbcTemplate().query("select * from event where coordinator_id = ? and curdate()= event.event_startdate and event.company_id = ? and status='PUBLISHED' order by event.event_startdate, start_time asc ",new Object[]{customUser.getEmp_id(),companyId}, new EventRowMapper());
		else
			return new ArrayList<Event>();
	}

	public List<SignInOut> findByEvent(final Integer eventId, final Integer companyId) {
		return getJdbcTemplate().query(" select date_format(signinout.start_time,'%l:%i %p') as starttime,date_format(signinout.end_time,'%l:%i %p') as endtime, date_format(signinout.break_start,'%l:%i %p') as breakstart,date_format(signinout.break_end,'%l:%i %p') as breakend, schedule.employee_id, schedule.position_id from signinout,schedule where signinout.schedule_id = schedule.id and schedule.event_id = ? and schedule.company_id = ? ",new Object[]{eventId,companyId}, new SignInOutMapper());
	}

	public List<Employee> findAllByEmployee(final Integer eventId,final Integer tenantId) {
		List<Employee> scheduledPositions = getJdbcTemplate().query(selectSignInOutQuery, new Object[]{eventId,tenantId}, new EmployeeSignInOutMapper() );
        return scheduledPositions;
	}

	public Integer findIfExist(final Integer eventId, final Integer tenantId) {
		String findMaxId = "select max(id) from signinout sio, (select temp.id as schId, temp.employee_id, temp.firstname, temp.lastname, temp.position_id, temp.name as posname,sio.id as signinid,date_format(sio.start_time,'%l:%i %p') as start_time , date_format(sio.end_time,'%l:%i %p') as end_time, date_format(sio.break_start,'%l:%i %p') as break_start, date_format(sio.break_end,'%l:%i %p') as break_end, sio.remarks from( SELECT s.id, s.employee_id,epos.position_id, p.name, e.firstname, e.lastname FROM event_position epos, schedule s, employee e, position p where s.event_pos_id = epos.id and s.employee_id = e.id and epos.position_id = p.id and epos.event_id = ? and s.active = true and s.company_id = ?) as temp left join signinout sio on temp.id = sio.schedule_id) as temp2 where sio.schedule_id = temp2.schId ";
		Integer id = getJdbcTemplate().queryForObject(findMaxId, new Object[]{eventId,tenantId},Integer.class);
		return id;
	}
	
}
