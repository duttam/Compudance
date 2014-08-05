package com.onetouch.model.dao.schedule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.mapper.schedule.ScheduleRowMapper;

@Repository
@Qualifier("offerScheduleDAO")
public class OfferScheduleDAO extends ScheduleDAO{

	public Integer offerSchedules(final Schedule schedule) {

		getJdbcTemplate().update("insert into offerschedule(event_pos_id,employee_id,company_id,create_time,expire_time,status,active) values (?,?,?,?,?,?,?)", new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps)
			throws SQLException {
				ps.setInt(1,schedule.getEventPositionId());
				ps.setInt(2,schedule.getEmployee().getId());
				ps.setInt(3,schedule.getTenant().getId());
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				ps.setString(4,dateformat.format(new Date()));
				ps.setString(5,dateformat.format(schedule.getExpiretime()));
				ps.setInt(6,schedule.getSchedulestatus());
				ps.setBoolean(7, schedule.isActive());
			}


		});
		return getJdbcTemplate().queryForInt( "select last_insert_id()");


	}
	
	public List<Schedule> findAllExpiredOfferedSchedules() {
		return getJdbcTemplate().query("select * from offerschedule where TIMEDIFF(now(),expire_time)>0 and status = 1", new RowMapper<Schedule>() {

			public Schedule mapRow(ResultSet rs, int row)
					throws SQLException {
				Schedule schedule = new Schedule();
				schedule.setId(rs.getInt("id"));
				schedule.setSchedulestatus(rs.getInt("status"));
				schedule.setActive(rs.getBoolean("active"));
				return schedule;
			}
		});
	}
	public void expireOfferedSchedules(final List<Schedule> expiredSchedules) {
		getJdbcTemplate().batchUpdate("update offerschedule set status = 4 where TIMEDIFF(now(),expire_time)>0 and status = 1 and id = ?",new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Schedule schedule = expiredSchedules.get(i);
				ps.setInt(1,schedule.getId());
				
			}
			
			@Override
			public int getBatchSize() {
				
				return expiredSchedules.size();
			}
		});
		
		
	}
	public Schedule findOfferedSchedule(Integer scheduleId, final Integer eventPosId,final Integer employeeId, final Integer companyId) {
		String findScheduleDetailQuery = "select temp1.id as scheduleId,temp1.status as scheduleStatus, temp1.active ,temp1.create_time,temp1.expire_time,temp1.update_time,temp1.firstname,temp1.lastname," +
				"e.id as eventId, e.name as eventName, e.event_startdate as startDate, e.event_enddate as endDate, date_format(e.start_time,'%l:%i %p') as startTime , " +
				"date_format(e.end_time,'%l:%i %p') as endTime, e.notes as eventNotes, e.dress_code as eventDressCode, e.host as eventHost, e.description as eventDesc, " +
				"p.name as positionName,temp1.event_pos_id as eventPosId, date_format(temp1.start_time,'%l:%i %p') as positionStartTime, " +
				"date_format(temp1.end_time,'%l:%i %p') as positionEndTime,temp1.notes as eventPosNotes, " +
				"l.name as locName,l.code as locCode,l.address1 as locAddr1, l.address2 as locAddr2, l.city as locCity,l.state as locState, l.zipcode as locZipcode, " +
				"l.contact_name as locContactName,l.primary_phone as locPhone, l.email as locEmail " +
				"from ( select emp.firstname, emp.lastname, epos.event_id,epos.position_id,epos.start_time,epos.end_time,epos.notes,s.* " +
				"from event_position epos, offerschedule s,employee emp " +
				"where s.id = ? and s.event_pos_id = ? and s.employee_id = ? and s.company_id = ? and epos.id = s.event_pos_id and s.employee_id = emp.id) as temp1, " +
				"event e,position p,location l " +
				"where temp1.event_id = e.id and temp1.position_id = p.id and e.location_id = l.id";
		List<Schedule> schedules =  getJdbcTemplate().query(findScheduleDetailQuery,new Object[]{scheduleId,eventPosId,employeeId,companyId},new RowMapper<Schedule>() {

			public Schedule mapRow(ResultSet rs, int row)
					throws SQLException {
				Schedule schedule = new Schedule();
				schedule.setId(rs.getInt("scheduleId"));
				schedule.setSchedulestatus(rs.getInt("scheduleStatus"));
				schedule.setActive(rs.getBoolean("active"));
				EventPosition eventPosition = new EventPosition();
				eventPosition.setId(rs.getInt("eventPosId"));
				SimpleDateFormat scdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try {
					schedule.setNotifiedtime(scdateformat.parse(rs.getString("create_time")));
					schedule.setExpiretime(scdateformat.parse(rs.getString("expire_time")));
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
				try {
					eventPosition.setStartTime(format.parse(rs.getString("positionStartTime")));
					eventPosition.setEndTime(format.parse(rs.getString("positionEndTime")));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				eventPosition.setNotes(rs.getString("eventPosNotes"));
				schedule.setEventPositionId(eventPosId);
				schedule.setEventPosition(eventPosition);
				
				Event event = new Event();
				event.setId(rs.getInt("eventId"));
				event.setName(rs.getString("eventName"));
				event.setDressCode(rs.getString("eventDressCode"));
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					event.setStartDate(dateformat.parse(rs.getString("startDate")));
					event.setEndDate(dateformat.parse(rs.getString("endDate")));
					String startTime = rs.getString("startTime");
					if(startTime!=null)
						event.setStartTime(format.parse(startTime));
					String endTime = rs.getString("endTime");
					if(endTime!=null)
						event.setEndTime(format.parse(endTime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				schedule.setEvent(event);
				
				Location location = new Location();
				location.setName(rs.getString("locName"));
				location.setCode(rs.getString("locCode"));
				location.setAddress1(rs.getString("locAddr1"));
				location.setAddress2(rs.getString("locAddr2"));
				location.setCity(rs.getString("locCity"));
				//location.setState(new State(rs.getString("locStateCode"), rs.getString("locStateName")));
				location.setZipcode(rs.getString("locZipCode"));
				location.setContactName(rs.getString("locContactName"));
				location.setContactPhone(rs.getString("locPhone"));
				location.setContactEmail(rs.getString("locEmail"));
				schedule.getEvent().setLocation(location);
				
				Position position = new Position();
				//position.setId(rs.getInt("positionId"));
				position.setName(rs.getString("positionName"));
				
				schedule.setPosition(position);
				Employee employee = new Employee();
				employee.setId(employeeId);
				if(rs.getString("firstname")!=null)
					employee.setFirstname(rs.getString("firstname"));
				if(rs.getString("lastname")!=null)
					employee.setLastname(rs.getString("lastname"));
				schedule.setEmployee(employee);
				schedule.setTenant(new Tenant(companyId));
				return schedule;
			}
		});
				
		
		if(schedules.size()>0)
			return	schedules.get(0);
		else
			return null;
		
		
		
	}
	public List<Employee> findOfferedEmployeesByEventPosition(EventPosition offerEventPosition,Tenant tenant){
		String selectEmployeebyEventPosition = "select emp.id as employee_id,emp.firstname,emp.lastname,emp.homephone,emp.rating,emp.email,emp.hiredate," +
		"o.id as offerId, o.status, o.active " +
		"from employee emp, offerschedule o " +
		"where emp.id = o.employee_id and o.event_pos_id = ? and o.company_id = ? and o.status = 1 and o.active= true and emp.status = 'active'"; 
		return getJdbcTemplate().query(selectEmployeebyEventPosition,new Object[]{offerEventPosition.getId(),tenant.getId()}, new RowMapper<Employee>() {
		@Override
		public Employee mapRow(ResultSet rs, int row)
		throws SQLException {
		Integer employeeId = rs.getInt("employee_id");
		
		Employee employee = new Employee(employeeId);
		
		employee.setId(employeeId);
		Schedule schedule = new Schedule();
		schedule.setId(rs.getInt("offerId"));
		Integer status = rs.getInt("status");
		schedule.setSchedulestatus(rs.getInt("status"));
		
		
		employee.setFirstname(rs.getString("firstname"));
		employee.setLastname(rs.getString("lastname"));
		employee.setHomephone(rs.getString("homephone"));
		employee.setEmail(rs.getString("email"));
		employee.setRating(rs.getInt("rating"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {

			if(rs.getString("hiredate")!=null)
				employee.setHireDate(format.parse(rs.getString("hiredate")));
			
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		employee.setSchedule(schedule);
		
		return employee;
		}
		});
	}
	public void saveOfferedSchedule(final Schedule schedule, final int status) {
		getJdbcTemplate().update("insert into Schedule(event_pos_id,employee_id,active,company_id,status,notify_senttime,notify_expiretime,notify_updatetime) values (?,?,?,?,?,?,?,?)", new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps)
					throws SQLException {
				ps.setInt(1,schedule.getEventPositionId());
                ps.setInt(2,schedule.getEmployee().getId());
                ps.setBoolean(3, schedule.isActive());
                ps.setInt(4,schedule.getTenant().getId());
                ps.setInt(5,status);
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                
                ps.setString(6,dateformat.format(schedule.getNotifiedtime()));
                ps.setString(7,dateformat.format(schedule.getExpiretime()));
                ps.setString(8,dateformat.format(new Date()));
			}

                
          });
       
        	
	}

	public void updateOfferedSchedule(final Schedule schedule,final int status) {
		getJdbcTemplate().update("update offerschedule set status = ? where id = ? and event_pos_id = ? and employee_id = ? and company_id = ? and active = true", new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps)
					throws SQLException {
				ps.setInt(1,status);
                ps.setInt(2,schedule.getId());
				ps.setInt(3,schedule.getEventPositionId());
                ps.setInt(4,schedule.getEmployee().getId());
                ps.setInt(5,schedule.getTenant().getId());
                
			}

                
          });
       
	}

	public int findOfferCountByEventPosition(Integer eventPositionId) {
		Integer offerCount = getJdbcTemplate().queryForObject("select offer_count from event_position where id = ?",new Object[]{eventPositionId},Integer.class);
		if(offerCount==null)
			return 0;
		
		return offerCount;
			
	}

	public void updateOfferCountByEventPosition(final Integer newOfferCount,final Integer eventPositionId) {
		getJdbcTemplate().update("update event_position set offer_count = ? where id = ? and active = true", new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps)
					throws SQLException {
				ps.setInt(1,newOfferCount);
                ps.setInt(2,eventPositionId);
				
                
			}

                
          });
	}
}
