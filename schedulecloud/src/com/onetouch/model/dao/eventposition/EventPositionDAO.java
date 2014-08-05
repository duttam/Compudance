package com.onetouch.model.dao.eventposition;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.dbcp.BasicDataSource;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.State;

import com.onetouch.model.domainobject.Tenant;

import com.onetouch.model.mapper.employee.AvailableEmployeeExtractor;
import com.onetouch.model.mapper.employee.EmployeeRowMapper;
import com.onetouch.model.mapper.event.EventDetailExtractor;
import com.onetouch.model.mapper.event.EventPositionExtractor;
import com.onetouch.model.mapper.event.EventPositionWithEmployeeExtractor;
import com.onetouch.model.mapper.event.EventRowMapper;

import com.onetouch.model.mapper.schedule.EventPositionWithScheduleExtractor;
import com.onetouch.view.util.DateUtil;


@Repository
@Qualifier("eventPositionDAO")
public class EventPositionDAO extends BaseDAO{

	private final String createEventPositionSql = "insert into event_position (event_id,position_id,reqd_number,start_time,end_time,start_date,end_date,company_id,displayOrder,active,region_id,notes) values (?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private final String selectAvailableEmployeePositionByEvent = "select temp4.eposId, temp4.displayOrder, temp4.position_id,p.name as posName,date_format(temp4.start_time,'%l:%i %p') as start_time,date_format(temp4.end_time,'%l:%i %p') as end_time,temp4.reqd_number,temp4.emp_id, temp4.firstname,temp4.lastname,temp4.image_bytes,temp4.image_name,temp4.image_type, temp4.rating " +
				"from ( select temp3.eposId, temp3.displayOrder, temp3.position_id,temp3.employee_id,temp3.start_time,temp3.end_time,temp3.reqd_number,e.id as emp_id, e.firstname,e.lastname,e.image_bytes,e.image_name,e.image_type,e.rating " +
				"from ( select temp2.eposId, temp2.displayOrder, temp2.position_id,temp2.employee_id,temp2.start_time,temp2.end_time,temp2.reqd_number " +
				"from ( SELECT distinct temp1.eposId, temp1.displayOrder, temp1.position_id,empos.employee_id,temp1.start_time,temp1.end_time,temp1.reqd_number,temp1.event_date " +
				"from (select distinct epos.id as eposId,epos.displayOrder,epos.position_id,epos.start_time,epos.end_time,epos.reqd_number,e.event_date " +
				"from event_position epos, event e " +
				"where  epos.event_id = e.id and e.id=? and e.company_id = ? and epos.active = true) as temp1 " +
				"left join employee_position empos on temp1.position_id = empos.position_id ) as temp2 " +
				"left join availability a on temp2.employee_id = a.employee_id and temp2.event_date = a.avail_date and TIMEDIFF(temp2.start_time,a.starttime)>0 and TIMEDIFF(a.endtime,temp2.end_time)>0 " +
				"order by temp2.position_id, temp2.employee_id) as temp3 left join employee e on temp3.employee_id = e.id ) as temp4, position p " +
				"where temp4.position_id = p.id order by displayOrder desc";
	
	private final String selectScheduleByEventSql = " select * from( " +
			"select * from ( " +
			"select temp1.event_posId,temp1.event_id,temp1.position_id,temp1.reqd_number,temp1.start_date,temp1.end_date,temp1.start_time,temp1.end_time," +
			"temp1.displayOrder as PosDisplayOrder, temp1.active as eposActive, temp1.notes as eposNotes,temp1.offer_notes,temp1.offer_count, " +
			"s.id as scheduleId, s.status, s.employee_id,s.active,s.notify_expiretime,s.notify_updatetime,s.override_time " +
			"from ( " +
			"select epos.id as event_posId, epos.event_id,epos.position_id,epos.reqd_number,epos.start_date,epos.end_date,epos.start_time,epos.end_time,epos.displayOrder, epos.active, " +
			"epos.notes,epos.offer_notes,epos.offer_count " +
			"from event_position epos " +
			"where epos.event_id = ? and company_id = ? and epos.active = true " +
			"order by displayOrder asc) as temp1 " +
			"left join schedule s on temp1.event_posId = s.event_pos_id and s.active=true) as temp2 " +
			"left join position p on temp2.position_id = p.id) as temp3 " +
			"left join employee e on temp3.employee_id = e.id " +
			"order by PosDisplayOrder asc"; 
	
	private final String findEmployeesForPosition = "SELECT * FROM (SELECT EMPLOYEE_ID  FROM AVAILABILITY A WHERE A.AVAIL_DATE = DATE(?) and TIMEDIFF(TIME(?),a.starttime)>0 and TIMEDIFF(a.endtime,TIME(?))>0 and A.COMPANY_ID=?) TEMP1, (SELECT EMPLOYEE_ID FROM EMPLOYEE_POSITION,POSITION WHERE POSITION.ID = EMPLOYEE_POSITION.POSITION_ID AND POSITION.ID = ? AND POSITION.COMPANY_ID = ?) TEMP2,EMPLOYEE E WHERE TEMP1.EMPLOYEE_ID = TEMP2.EMPLOYEE_ID AND TEMP1.EMPLOYEE_ID = E.ID";
	
	public List<EventPosition> findPositionEmployeeByEvent(final Integer companyId,final Integer eventId){
		return getJdbcTemplate().query(selectAvailableEmployeePositionByEvent,new Object[]{eventId,companyId}, new EventPositionWithEmployeeExtractor());
	}
	
	public int save(final EventPosition eventPosition){
			getJdbcTemplate().update(createEventPositionSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				ps.setInt(1,eventPosition.getEvent().getId());
				ps.setInt(2,eventPosition.getPosition().getId());
				ps.setInt(3,eventPosition.getReqdNumber());
				SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
				ps.setString(4,timeformat.format(eventPosition.getStartTime()));
				ps.setString(5,timeformat.format(eventPosition.getEndTime()));
				ps.setString(6,dateformat.format(eventPosition.getStartTime()));
				ps.setString(7,dateformat.format(eventPosition.getEndTime()));
				ps.setInt(8,eventPosition.getCompanyId());
				ps.setInt(9,eventPosition.getDisplayOrder());
				ps.setBoolean(10, eventPosition.isActive());
				ps.setInt(11,eventPosition.getRegion().getId());
				ps.setString(12,eventPosition.getNotes());
				
			}
		});
			return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public void updateEventPosition(final EventPosition eventPosition) {
	getJdbcTemplate().update("update event_position set position_id=?,reqd_number = ?,start_time =?,end_time =?,active=?,displayOrder=?,notes =?, start_date=?, end_date=? " +
			"where id = ? and company_id=? and region_id=?",	new PreparedStatementSetter() {
		
		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			
			ps.setInt(1,eventPosition.getPosition().getId());
			ps.setInt(2,eventPosition.getReqdNumber());
			SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			ps.setString(3,timeformat.format(eventPosition.getStartTime()));
			ps.setString(4,timeformat.format(eventPosition.getEndTime()));
			ps.setBoolean(5, eventPosition.isActive());
			ps.setInt(6,eventPosition.getDisplayOrder());
			ps.setString(7,eventPosition.getNotes());
			
			ps.setString(8,dateformat.format(eventPosition.getStartTime()));
			ps.setString(9,dateformat.format(eventPosition.getEndTime()));
			
			ps.setInt(10,eventPosition.getId());
			ps.setInt(11,eventPosition.getCompanyId());
			ps.setInt(12,eventPosition.getRegion().getId());
		}
	});
		
	}
	
	public List<EventPosition> findSchedulesByEvent(final Integer eventId,final Integer companyId){
		return getJdbcTemplate().query(selectScheduleByEventSql,new Object[]{eventId,companyId}, new EventPositionWithScheduleExtractor());
	}
	public List<EventPosition> findTransferredEventPositionByEmployee(final Integer employeeId,final Integer eventId,final Integer companyId) {
		return getJdbcTemplate().query("select * from( select * from ( " +
				"select temp1.event_posId,temp1.event_id,temp1.position_id,temp1.reqd_number,temp1.start_date,temp1.end_date,temp1.start_time,temp1.end_time, " +
				"temp1.displayOrder as PosDisplayOrder, temp1.active as eposActive, temp1.notes as eposNotes,temp1.offer_notes,temp1.offer_count, " +
				"s.id as scheduleId, s.status, s.employee_id,s.active,s.notify_expiretime,s.notify_updatetime,s.override_time " +
				"from ( " +
				"select epos.id as event_posId, epos.event_id,epos.position_id,epos.reqd_number,epos.start_date,epos.end_date,epos.start_time,epos.end_time," +
				"epos.displayOrder, epos.active, epos.notes,epos.offer_notes,epos.offer_count " +
				"from event_position epos, employee_position ep " +
				"where epos.event_id = ? and ep.employee_id = ? and epos.position_id = ep.position_id and epos.company_id = ? and epos.active = true " +
				"order by displayOrder asc) as temp1 " +
				"left join schedule s on temp1.event_posId = s.event_pos_id and s.active=true) as temp2 " +
				"left join position p on temp2.position_id = p.id) as temp3 " +
				"left join employee e on temp3.employee_id = e.id order by PosDisplayOrder asc",new Object[]{eventId,employeeId,companyId}, new EventPositionWithScheduleExtractor());
	}
	
	public void updateTimeAndReqdPositionCount(final EventPosition eventPosition, final Integer companyId){
		
		getJdbcTemplate().update("update event_position set start_time =?,end_time =?, reqd_number=?, start_date=?, end_date=? where id = ? and company_id=?",	new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				ps.setString(1,timeformat.format(eventPosition.getStartTime()));
				ps.setString(2,timeformat.format(eventPosition.getEndTime()));
				ps.setInt(3,eventPosition.getReqdNumber());
				ps.setString(4,dateformat.format(eventPosition.getStartTime()));
				ps.setString(5,dateformat.format(eventPosition.getEndTime()));
				ps.setInt(6,eventPosition.getId());
				ps.setInt(7,companyId);
				
			}
		});
		
	}

	public void updateEventPositionStatus(final EventPosition scheduledEventPosition,final Integer companyId) {
		getJdbcTemplate().update("update event_position set active=? where id = ? and company_id=?",	new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				
				ps.setBoolean(1,scheduledEventPosition.isActive());
				ps.setInt(2,scheduledEventPosition.getId());
				ps.setInt(3,companyId);
				
			}
		});
	}
	public void deleteEventPosition(final EventPosition scheduledEventPosition,final Integer companyId) {
		getJdbcTemplate().update("delete from event_position where id = ? and company_id=?",	new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1,scheduledEventPosition.getId());
				ps.setInt(2,companyId);				
				
				
			}
		});
	}
/*	public List<Employee> findEmployeeByPosition(final Event event, final EventPosition eventPosition){
		Position position = eventPosition.getPosition();
		final Integer positionId = position.getId();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String eventDate = dateformat.format(event.getEventDate());
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		
		final String startTime = timeformat.format(eventPosition.getStartTime());
		final String endTime = timeformat.format(eventPosition.getEndTime());
		final Integer companyId = event.getTenant().getId();
		
		return getJdbcTemplate().query(findEmployeesForPosition,new Object[]{eventDate,startTime,endTime,companyId,positionId,companyId}, new EmployeeRowMapper());
	}
*/
	public List<Integer> findOverlappingEventPosition(final Event event, final EventPosition eventPosition){
		//String overlapeventpositionsql = "select ep.id from event_position ep where ep.company_id = ? and ep.start_date = DATE(?) and (TIMEDIFF(TIME(?),ep.start_time)>0 and TIMEDIFF(ep.end_time,TIME(?))>0 or TIMEDIFF(TIME(?),ep.start_time)>0 and TIMEDIFF(ep.end_time,TIME(?))>0)";
		// date and time sql
		String overlappingeventpositionsql1 = "select ep.id from event_position ep where ep.company_id = ? and ep.start_date = DATE(?) and ( TIMEDIFF(?,CONCAT(ep.start_date,' ',ep.start_time))>=0 and TIMEDIFF(CONCAT(ep.end_date,' ',ep.end_time),?)>=0 or TIMEDIFF(?,CONCAT(ep.start_date,' ',ep.start_time))>=0 and TIMEDIFF(CONCAT(ep.end_date,' ',ep.end_time),?)>=0 or TIMEDIFF(?,CONCAT(ep.start_date,' ',ep.start_time))<=0 and TIMEDIFF(CONCAT(ep.end_date,' ',ep.end_time),?)<=0)"; 
		//only time sql, breaks for midnight shifts
		String overlappingeventpositionsql = "select ep.id from event_position ep where ep.company_id = ? and ep.start_date = DATE(?) and ( TIMEDIFF(TIME(?),ep.start_time)>=0 and TIMEDIFF(ep.end_time,TIME(?))>=0 or TIMEDIFF(TIME(?),ep.start_time)>=0 and TIMEDIFF(ep.end_time,TIME(?))>=0 or TIMEDIFF(TIME(?),ep.start_time)<=0 and TIMEDIFF(ep.end_time,TIME(?))<=0)";
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String eventDate = dateformat.format(event.getStartDate());
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateAndtimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		final String startTime = timeformat.format(eventPosition.getStartTime());
		final String endTime = timeformat.format(eventPosition.getEndTime());
		Date startDate = DateUtil.getDateAndTime(eventPosition.getStartDate(), eventPosition.getStartTime());
		Date endDate = DateUtil.getDateAndTime(eventPosition.getEndDate(), eventPosition.getEndTime());
		final String startDateAndTime = dateAndtimeformat.format(startDate);
		final String endDateAndTime = dateAndtimeformat.format(endDate);
		
		final Integer companyId = event.getTenant().getId();
		
		//return getJdbcTemplate().queryForList(overlappingeventpositionsql,new Object[]{companyId,eventDate,startTime,startTime,endTime,endTime,startTime,endTime},Integer.class);
		return getJdbcTemplate().queryForList(overlappingeventpositionsql1,new Object[]{companyId,eventDate,startDateAndTime,startDateAndTime,endDateAndTime,endDateAndTime,startDateAndTime,endDateAndTime},Integer.class);
	}
	
	public List<EventPosition> findAllEventPositionByEvent(final Integer eventId,final Integer companyId, final Integer regionId) {
		String sql = "select evp.id as eposId, evp.position_id as position_id, date_format(evp.start_time,'%l:%i %p') as start_time,date_format(evp.end_time,'%l:%i %p') as end_time, " +
				"evp.reqd_number,evp.displayOrder, evp.start_date, evp.end_date, evp.notes,e.id as emp_id,e.firstname,e.lastname,e.image_bytes,e.image_name,  e.image_type,e.rating,e.homephone  ,p.id as posId,p.name as posName " +
				"from event_position evp " +
				"left join employee_position emp on evp.position_id = emp.position_id " +
				"left join (select * from employee where employee.company_id = ? and employee.region_id = ? and employee.status = 'active') as e on e.id = emp.employee_id " +
				"left join position p on p.id = evp.position_id left join region r on r.id = evp.region_id " +
				"where evp.event_id = ? and evp.company_id = ? and evp.region_id = ? " +
				"order by evp.displayOrder asc";
		//String eventPosByEmpSql = "select temp1.eposId, temp1.position_id, temp1.start_time,temp1.end_time,temp1.reqd_number,temp1.displayOrder, temp1.start_date, temp1.end_date,temp1.posName, temp1.emp_id,e.firstname,e.lastname,e.image_bytes,e.image_name,  e.image_type,e.rating,e.homephone from( select temp.eposId,temp.position_id, temp.start_time,temp.end_time,temp.reqd_number,   temp.displayOrder, temp.start_date, temp.end_date, temp.emp_id,p.name as posName from( select evp.id as eposId, evp.position_id as position_id, date_format(evp.start_time,'%l:%i %p') as start_time,date_format(evp.end_time,'%l:%i %p') as end_time, evp.reqd_number,evp.displayOrder, evp.start_date, evp.end_date, emp.employee_id as emp_id from event_position evp left join employee_position emp on evp.position_id = emp.position_id where evp.event_id = ? and evp.company_id = ? and evp.region_id = ? order by evp.displayOrder asc) as temp, position p where temp.position_id = p.id) temp1 left join employee e on temp1.emp_id = e.id" ;
		//String modifiedEventPosBySql = "select temp1.eposId, temp1.position_id, temp1.start_time,temp1.end_time,temp1.reqd_number,temp1.displayOrder, temp1.start_date, temp1.end_date,temp1.posName, e.id,e.firstname,e.lastname,e.image_bytes,e.image_name,  e.image_type,e.rating,e.homephone from( select temp.eposId,temp.position_id, temp.start_time,temp.end_time,temp.reqd_number,   temp.displayOrder, temp.start_date, temp.end_date, temp.emp_id,p.name as posName from( select evp.id as eposId, evp.position_id as position_id, date_format(evp.start_time,'%l:%i %p') as start_time,date_format(evp.end_time,'%l:%i %p') as end_time, evp.reqd_number,evp.displayOrder, evp.start_date, evp.end_date, emp.employee_id as emp_id from event_position evp left join employee_position emp on evp.position_id = emp.position_id where evp.event_id = 348 and evp.company_id = 12 and evp.region_id = 5 order by evp.displayOrder asc) as temp, position p where temp.position_id = p.id) temp1 left join (select * from employee where employee.company_id = 12 and employee.region_id = 5) as e on temp1.emp_id = e.id";
		return getJdbcTemplate().query(sql,new Object[]{companyId,regionId,eventId,companyId,regionId}, new EventPositionExtractor());
		
	}
	public List<EventPosition> findTempAllEventPositionByEvent(final Integer eventId,final Integer companyId, final Integer regionId) {
		String sql = "select evp.id as eposId, evp.position_id as position_id, date_format(evp.start_time,'%l:%i %p') as start_time,date_format(evp.end_time,'%l:%i %p') as end_time, " +
				"evp.reqd_number,evp.displayOrder, evp.start_date, evp.end_date, evp.notes," +
				"p.id as posId,p.name as posName " +
				"from event_position evp " +
				"left join position p on p.id = evp.position_id left join region r on r.id = evp.region_id " +
				"where evp.event_id = ? and evp.company_id = ? and evp.region_id = ? " +
				"order by evp.displayOrder asc";
		//String eventPosByEmpSql = "select temp1.eposId, temp1.position_id, temp1.start_time,temp1.end_time,temp1.reqd_number,temp1.displayOrder, temp1.start_date, temp1.end_date,temp1.posName, temp1.emp_id,e.firstname,e.lastname,e.image_bytes,e.image_name,  e.image_type,e.rating,e.homephone from( select temp.eposId,temp.position_id, temp.start_time,temp.end_time,temp.reqd_number,   temp.displayOrder, temp.start_date, temp.end_date, temp.emp_id,p.name as posName from( select evp.id as eposId, evp.position_id as position_id, date_format(evp.start_time,'%l:%i %p') as start_time,date_format(evp.end_time,'%l:%i %p') as end_time, evp.reqd_number,evp.displayOrder, evp.start_date, evp.end_date, emp.employee_id as emp_id from event_position evp left join employee_position emp on evp.position_id = emp.position_id where evp.event_id = ? and evp.company_id = ? and evp.region_id = ? order by evp.displayOrder asc) as temp, position p where temp.position_id = p.id) temp1 left join employee e on temp1.emp_id = e.id" ;
		//String modifiedEventPosBySql = "select temp1.eposId, temp1.position_id, temp1.start_time,temp1.end_time,temp1.reqd_number,temp1.displayOrder, temp1.start_date, temp1.end_date,temp1.posName, e.id,e.firstname,e.lastname,e.image_bytes,e.image_name,  e.image_type,e.rating,e.homephone from( select temp.eposId,temp.position_id, temp.start_time,temp.end_time,temp.reqd_number,   temp.displayOrder, temp.start_date, temp.end_date, temp.emp_id,p.name as posName from( select evp.id as eposId, evp.position_id as position_id, date_format(evp.start_time,'%l:%i %p') as start_time,date_format(evp.end_time,'%l:%i %p') as end_time, evp.reqd_number,evp.displayOrder, evp.start_date, evp.end_date, emp.employee_id as emp_id from event_position evp left join employee_position emp on evp.position_id = emp.position_id where evp.event_id = 348 and evp.company_id = 12 and evp.region_id = 5 order by evp.displayOrder asc) as temp, position p where temp.position_id = p.id) temp1 left join (select * from employee where employee.company_id = 12 and employee.region_id = 5) as e on temp1.emp_id = e.id";
		return getJdbcTemplate().query(sql,new Object[]{eventId,companyId,regionId}, new EventPositionExtractor());
		
	}
	public List<EventPosition> findAllEventPositionByDate(final Date eventDate,final Integer companyId) {
		
		String eventPosByEmpSql = "select temp1.eposId, temp1.position_id, temp1.start_time,temp1.end_time,temp1.reqd_number,temp1.displayOrder, temp1.start_date, temp1.end_date,temp1.posName, temp1.emp_id,e.firstname,e.lastname,e.image_bytes,e.image_name,  e.image_type,e.rating,e.homephone from( select temp.eposId,temp.position_id, temp.start_time,temp.end_time,temp.reqd_number,   temp.displayOrder, temp.start_date, temp.end_date, temp.emp_id,p.name as posName from( select evp.id as eposId, evp.position_id as position_id, date_format(evp.start_time,'%l:%i %p') as start_time,date_format(evp.end_time,'%l:%i %p') as end_time, evp.reqd_number,evp.displayOrder, evp.start_date, evp.end_date, emp.employee_id as emp_id from event_position evp left join employee_position emp on evp.position_id = emp.position_id where evp.event_id = ? and evp.company_id = ? order by evp.displayOrder asc) as temp, position p where temp.position_id = p.id) temp1 left join employee e on temp1.emp_id = e.id" ;
		return getJdbcTemplate().query(eventPosByEmpSql,new Object[]{eventDate,companyId}, new EventPositionExtractor());
		
	}
	public void delete(Event event, final List<EventPosition> assignedEventPositions,
			Tenant tenant) {
		final Integer eventId = event.getId();
		final Integer companyId = tenant.getId();
		getJdbcTemplate().batchUpdate("DELETE from event_position where id = ? AND event_id =? and company_id = ?", new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				EventPosition eventPosition = assignedEventPositions.get(i);
				ps.setInt(1,eventPosition.getId());
				ps.setInt(2,eventId);
				ps.setInt(3,companyId);
				
			}
			
			@Override
			public int getBatchSize() {
				
				return assignedEventPositions.size();
			}
		});
		
	}
	
	public List<Map<String,Object>> findScheduleCountForEvent(final Integer eventId, final Integer companyId,final Integer regionId){
		return getJdbcTemplate().queryForList("SELECT temp1.reqdNum , temp2.schNum " +
				"from " +
				"(select sum(epos.reqd_number) as reqdNum  from event_position epos  where epos.event_id = ? and epos.active = true and epos.company_id = ? and epos.region_id =?) as temp1, " +
				"(select count(*) as schNum from event_position epos1, schedule s where s.event_pos_id = epos1.id and epos1.event_id = ? and epos1.active = true and s.active = true and s.company_id= ? and s.status=2) as temp2", new Object[]{eventId,companyId,regionId,eventId,companyId});
	}

	public List<Employee> findAllAvailabileEmployees(final Event event, final EventPosition eventPosition) {
		String sql = "select ep.employee_id, e.firstname, e.lastname,e.rating, e.status,e.homephone,avail.id,avail.avail_date, avail.starttime, avail.endtime " +
					"from  employee e, employee_position ep , availability avail " +
					"where e.id = ep.employee_id and e.region_id = ?  and e.id = avail.employee_id and avail.avail_date = DATE('2014-05-21')  and  " +
					"e.company_id = ? and e.status = 'active' and ep.position_id = ? " +
					"and " +
					//"ep.employee_id not in (select eto.employee_id from employee_time_off eto where eto.start_date = DATE(?) and eto.company_id = ? ) and " +
					"ep.employee_id not in (select re.emp_id from restrict_emploc re where re.loc_id = ? and re.company_id = ?) " +
					"and " +
					"ep.employee_id not in ( " +
					"select s.employee_id " +
					"from schedule s , event_position evp " +
					"where s.event_pos_id = evp.id and s.company_id = ? and evp.region_id = ? and s.status = 2 and evp.start_date = DATE(?) " +
					"and " +
					"( TIMEDIFF(?,CONCAT(evp.start_date,' ',evp.start_time))>=0 and TIMEDIFF(CONCAT(evp.end_date,' ',evp.end_time),?)>=0 " +
					"or " +
					"TIMEDIFF(?,CONCAT(evp.start_date,' ',evp.start_time))>=0 and TIMEDIFF(CONCAT(evp.end_date,' ',evp.end_time),?)>=0 " +
					"or " +
					"TIMEDIFF(?,CONCAT(evp.start_date,' ',evp.start_time))<=0 and TIMEDIFF(CONCAT(evp.end_date,' ',evp.end_time),?)<=0 )"+
					 " ) " +
					"and ep.employee_id not in ( " +
					"select a.employee_id from availability a " +
					"where a.company_id = ? and a.avail_date = DATE(?) " +
					"and " +
					"( TIMEDIFF(?,CONCAT(a.avail_date,' ',a.starttime))<=0 and TIMEDIFF(CONCAT(a.avail_date,' ',a.endtime),?)<=0 ) ) ";
		final Integer regionId = event.getEventRegion().getId();
		final Integer positionId = eventPosition.getPosition().getId();
		final Integer companyId = event.getTenant().getId();
		final Integer locationId = event.getLocation().getId(); 
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String eventDate = dateformat.format(event.getStartDate());
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateAndtimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		final String startTime = timeformat.format(eventPosition.getStartTime());
		final String endTime = timeformat.format(eventPosition.getEndTime());
		final Date startDate = DateUtil.getDateAndTime(eventPosition.getStartDate(), eventPosition.getStartTime());
		final Date endDate = DateUtil.getDateAndTime(eventPosition.getEndDate(), eventPosition.getEndTime());
		
		final String startDateAndTime = dateAndtimeformat.format(startDate);
		final String endDateAndTime = dateAndtimeformat.format(endDate);
		return getJdbcTemplate().query(sql,new Object[]{regionId,companyId,positionId,
														//eventDate,companyId,
														locationId,companyId,
														companyId,regionId,eventDate,
														companyId,eventDate,
														startDateAndTime,startDateAndTime,
														endDateAndTime,endDateAndTime,
														startDateAndTime,endDateAndTime,
														startDateAndTime,endDateAndTime}, new RowMapper<Employee>() {

			@Override
			public Employee mapRow(ResultSet rs, int row)
					throws SQLException {
				Integer employeeId = rs.getInt("employee_id");

				Employee employee = new Employee(employeeId);
				employee.setTenant(new Tenant(companyId));
				employee.setRegion(new Region(regionId));

				employee.setFirstname(rs.getString("firstname"));
				employee.setLastname(rs.getString("lastname"));
				employee.setRating(rs.getInt("rating"));
				employee.setHomephone(rs.getString("homephone"));


				return employee;
			}
		});
		
				
	}
	
	
	public List<Employee> findTempAllAvailabileEmployees(final Event event, final EventPosition eventPosition,Tenant tenant,Region region) {
		String sql = "select e.id as employee_id, e.firstname, e.lastname,e.rating, e.status,e.homephone,e.email,e.hiredate,avail.id,avail.avail_date," +
				"avail.starttime, avail.endtime, eto.id as timeoffId,s.status as notify, s.notifiedEvent  " +
				"from  employee e " +
				"inner join employee_position ep  on e.id = ep.employee_id and ep.position_id = ? " +
				"inner join availability avail on e.id = avail.employee_id and avail.avail_date = DATE(?) " +
				"left outer join (select sch.employee_id, sch.status, sch.event_pos_id as notifiedEvent from schedule sch,event_position ev_p " +
				"where sch.event_pos_id = ev_p.id and ev_p.start_date = date(?) " +
				"and ev_p.company_id = ? and ev_p.region_id = ? and sch.status=1) s on  e.id = s.employee_id "+
				"left outer join employee_time_off eto on e.id = eto.employee_id and eto.time_off_type = 'Call Out' and eto.start_date = Date(?) and e.company_id = ? " +
				"where e.region_id = ? and e.company_id = ? and e.status = 'active' " +
				"and ep.employee_id not in (select re.emp_id from restrict_emploc re where re.loc_id = ? and re.company_id = ?) " +
				"and ep.employee_id not in ( " +
				"select s.employee_id " +
				"from schedule s , event_position evp " +
				"where s.event_pos_id = evp.id and " +
				"s.company_id = ? and evp.region_id = ? and s.status = 2 and s.event_pos_id <> ? and " +
				"evp.start_date = DATE(?) " +
				"and " +
				"( " +
				"TIMEDIFF(?,CONCAT(evp.start_date,' ',evp.start_time))>=0 and TIMEDIFF(CONCAT(evp.end_date,' ',evp.end_time),?)>=0 " +
				"or " +
				"TIMEDIFF(?,CONCAT(evp.start_date,' ',evp.start_time))>=0 and TIMEDIFF(CONCAT(evp.end_date,' ',evp.end_time),?)>=0 " +
				"or " +
				"TIMEDIFF(?,CONCAT(evp.start_date,' ',evp.start_time))<=0 and TIMEDIFF(CONCAT(evp.end_date,' ',evp.end_time),?)<=0 " +
				") " +
				") " +
				"and ep.employee_id not in ( " +
				"select a.employee_id " +
				"from availability a " +
				"where a.company_id = ? " +
				"and a.avail_date = DATE(?) and ( " +
				"TIMEDIFF(?,CONCAT(a.avail_date,' ',a.starttime))<0 and TIMEDIFF(CONCAT(a.avail_date,' ',a.endtime),?)<0 ) )";
		
		final Integer regionId = region.getId();
		final Integer positionId = eventPosition.getPosition().getId();
		final Integer companyId = tenant.getId();
		final Integer locationId = event.getLocation().getId(); 
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String eventDate = dateformat.format(event.getStartDate());
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateAndtimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		
		
		Date startDate = DateUtil.getDateAndTime(eventPosition.getStartDate(), eventPosition.getStartTime());
		Date endDate = DateUtil.getDateAndTime(eventPosition.getEndDate(), eventPosition.getEndTime());
		if(DateUtil.dateRange(startDate, endDate)==1){
			if(DateUtil.afterMidnight(startDate,endDate))
			endDate = DateUtil.getMidnight(eventPosition.getStartDate()); // only time
		}
		//startDate = DateUtil.getDateAndTime(eventPosition.getStartDate(), eventPosition.getStartTime());
		//endDate = DateUtil.getDateAndTime(eventPosition.getStartDate(), endDate);
		final String startDateAndTime = dateAndtimeformat.format(startDate);
		final String endDateAndTime = dateAndtimeformat.format(endDate);
		final Integer eventPositionId = (eventPosition.getId()==null)?0:eventPosition.getId();
		return getJdbcTemplate().query(sql,new Object[]{positionId,
														eventDate,
														eventDate,companyId,regionId,
														eventDate,companyId,
														regionId,companyId,
														locationId,companyId,
														companyId,regionId,eventPositionId,
														eventDate,
														startDateAndTime,startDateAndTime,
														endDateAndTime,endDateAndTime,
														startDateAndTime,endDateAndTime,
														companyId,
														eventDate,
														startDateAndTime,endDateAndTime}, new AvailableEmployeeExtractor(tenant, region, eventPosition));
		

	}
	
	public List<Integer> findAllDeclinedEmployeesByEventPosition(final Event event,EventPosition eventPosition, Tenant tenant, Region region) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String eventDate = dateformat.format(event.getStartDate());
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateAndtimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		final String startTime = timeformat.format(eventPosition.getStartTime());
		final String endTime = timeformat.format(eventPosition.getEndTime());
		final Date startDate = DateUtil.getDateAndTime(eventPosition.getStartDate(), eventPosition.getStartTime());
		final Date endDate = DateUtil.getDateAndTime(eventPosition.getEndDate(), eventPosition.getEndTime());
		
		final String startDateAndTime = dateAndtimeformat.format(startDate);
		final String endDateAndTime = dateAndtimeformat.format(endDate);
		final Integer eventPositionId = (eventPosition.getId()==null)?0:eventPosition.getId();
		return getJdbcTemplate().queryForList("select  e.id from  employee e, schedule s, event_position evp where s.event_pos_id = evp.id " +
				"and evp.start_date = DATE(?) and  e.id = s.employee_id " +
				"and s.status=3 and e.company_id = ? and e.region_id = ? and e.id not in ( " +
				"select s.employee_id from schedule s , event_position evp where s.event_pos_id = evp.id and " +
				"s.company_id = ? and evp.region_id = ? and s.status = 2 and s.event_pos_id <> ? and evp.start_date = DATE(?) " +
				"and ( " +
				"TIMEDIFF(?,CONCAT(evp.start_date,' ',evp.start_time))>=0 and TIMEDIFF(CONCAT(evp.end_date,' ',evp.end_time),?)>=0 " +
				"or " +
				"TIMEDIFF(?,CONCAT(evp.start_date,' ',evp.start_time))>=0 and TIMEDIFF(CONCAT(evp.end_date,' ',evp.end_time),?)>=0 " +
				"or " +
				"TIMEDIFF(?,CONCAT(evp.start_date,' ',evp.start_time))<=0 and TIMEDIFF(CONCAT(evp.end_date,' ',evp.end_time),?)<=0 ) )", 
								new Object[]{eventDate,tenant.getId(),region.getId(),
								tenant.getId(),region.getId(), eventPositionId,
								eventDate,
								startDateAndTime,startDateAndTime,
								endDateAndTime,endDateAndTime,
								startDateAndTime,endDateAndTime},
									
				Integer.class);
	}

	public List<Map<String,Object>> findNotifiedCountForEvent(Integer eventId,Integer companyId, Integer regionId) {
		/*List<Integer> values =   getJdbcTemplate().queryForList("select count(*) as notifiedCnt " +
				"from schedule s, event_position epos " +
				"where s.event_pos_id = epos.id and epos.event_id = ? and epos.active = true and s.active = true and s.company_id= ? and s.status=1 and epos.region_id= ?", 
				new Object[]{eventId,companyId,regionId},Integer.class);
		if(values!=null && values.size()>0)
			return values.get(0);
		else 
			return 0;*/
		return getJdbcTemplate().queryForList("select temp.reqd_number,ss.schNum from " +
				"( select epos.id, epos.reqd_number from event_position epos where  epos.event_id = ? and epos.active = true and epos.company_id = ? and epos.region_id = ? ) temp " +
				"left join " +
				"( select s.event_pos_id, count(*) as schNum from event_position epos1, schedule s where s.event_pos_id = epos1.id and epos1.event_id = ? and epos1.active = true " +
				"and s.active = true and s.company_id= ? and s.status<=2 group by s.event_pos_id) ss " +
				" on temp.id = ss.event_pos_id", new Object[]{eventId,companyId,regionId,eventId,companyId});
	}

	public List<Employee> findAllScheduledEmployeeByEventPosition(EventPosition scheduledEventPosition, Tenant tenant) {
		String selectEmployeebyEventPosition = "select emp.id as employee_id,emp.firstname,emp.lastname,emp.homephone,emp.rating,emp.email," +
												"s.id as scheduleId, s.status, s.active,s.notify_expiretime,s.notify_updatetime,s.override_time " +
												"from employee emp, schedule s " +
												"where emp.id = s.employee_id and s.event_pos_id = ? and s.company_id = ? and emp.status = 'active'"; 
				return getJdbcTemplate().query(selectEmployeebyEventPosition,new Object[]{scheduledEventPosition.getId(),tenant.getId()}, new RowMapper<Employee>() {
		@Override
		public Employee mapRow(ResultSet rs, int row)
		throws SQLException {
			Integer employeeId = rs.getInt("employee_id");
		
			Employee employee = new Employee(employeeId);
			
			employee.setId(employeeId);
			Schedule schedule = new Schedule();
			schedule.setId(rs.getInt("scheduleId"));
			Integer status = rs.getInt("status");
			schedule.setSchedulestatus(rs.getInt("status"));
			
			SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				if(rs.getString("notify_expiretime")!=null)
					schedule.setExpiretime(datetimeformat.parse(rs.getString("notify_expiretime")));
				if(rs.getString("notify_updatetime")!=null)
					schedule.setUpdatetime(datetimeformat.parse(rs.getString("notify_updatetime")));
				if(rs.getString("override_time")!=null)
					schedule.setOverrideTime(datetimeformat.parse(rs.getString("override_time")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			employee.setFirstname(rs.getString("firstname"));
			employee.setLastname(rs.getString("lastname"));
			employee.setHomephone(rs.getString("homephone"));
			employee.setEmail(rs.getString("email"));
			employee.setRating(rs.getInt("rating"));
			employee.setSchedule(schedule);
		
			return employee;
		}
		});
	}

	public void updateOfferNotes(final EventPosition eventPosition,final Integer companyId) {
		getJdbcTemplate().update("update event_position set offer_notes =?, offer_count = ?, reqd_number = ? where id = ? and company_id=?",	new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1,eventPosition.getOfferNotes());
				ps.setInt(2,eventPosition.getOfferCount());
				ps.setInt(3,eventPosition.getReqdNumber());
				ps.setInt(4,eventPosition.getId());
				ps.setInt(5,companyId);
				
			}
		});
	}

	public Integer findEventPosCnt(Integer eventId, Integer companyId) {
		return getJdbcTemplate().queryForObject("select count(*) from event_position where event_id = ? and company_id = ?", new Object[]{eventId, companyId},Integer.class);
	}

	
}
