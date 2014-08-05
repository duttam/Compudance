package com.onetouch.model.dao.schedule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.stereotype.Repository;

import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.mapper.position.PositionRowMapper;
import com.onetouch.model.mapper.schedule.EmployeeScheduleReportRowMapper;
import com.onetouch.model.mapper.schedule.EmployeeScheduleRowMapper;
import com.onetouch.model.mapper.schedule.EventPositionWithScheduleExtractor;
import com.onetouch.model.mapper.schedule.NotifiedScheduleRowMapper;
import com.onetouch.model.mapper.schedule.ScheduleRowMapper;
import com.onetouch.model.mapper.schedule.TotalActualRowMapper;
import com.onetouch.model.mapper.schedule.TotalForecastRowMapper;

import com.onetouch.model.mapper.schedule.ScheduleWithPositionExtractor;
import com.onetouch.model.mapper.employee.EmployeeRowMapper;
import com.onetouch.model.mapper.event.EventRowMapper;
import com.onetouch.view.util.DateUtil;


@Repository
@Qualifier("scheduleDAO")
public class ScheduleDAO extends BaseDAO{

	private final String createScheduleSql = "insert into Schedule(event_pos_id,employee_id,active,company_id,status,notify_senttime,notify_expiretime) values (?,?,?,?,?,?,?)"; 
	private final String scheduleByEmployeeSql =" SELECT sch.id as scheduleId,e.id as eventId,e.name eventName,e.event_date as eventDate,e.dress_code as eventDressCode, e.notes as eventNotes, " +
												" e.host as eventHost,e.description as eventDesc, s.id as shiftId,s.name as shiftName, date_format(s.start_time,'%l:%i %p') as shiftStartTime," +
												" date_format(s.end_time,'%l:%i %p') as shiftEndTime, p.id as positionId, p.name as positionName, l.id as locId, l.state as locStateName,l.statecode as locStateCode , " +
												"l.address1 as locAddr1, l.address2 as locAddr2, l.city as locCity,l.state as locState, l.zipcode as locZipcode, l.contact_name as locContactName, " +
												"l.primary_phone as locPhone, l.email as locEmail " +
												"from event as e, shift as s, schedule as sch, position as p, location as l " +
												"where e.id =  sch.event_id and s.id = sch.shift_id and p.id = sch.position_id and e.location_id = l.id and sch.employee_id = ?";
	private final String updateScheduleSql = "update schedule set status = ?, notify_senttime = ?, notify_expiretime = ?, notify_updatetime = ?  where id = ? and active = ? and event_pos_id = ? and employee_id = ? and company_id = ? ";
	
	private final String findScheduleByEventPositionSql = " select temp2.scheduleId,temp2.eventId,temp2.eventName,temp2.startDate,temp2.endDate, temp2.startTime,temp2.endTime,temp2.eventNotes, " +
			"temp2.eventDressCode,temp2.eventHost, temp2.eventDesc, temp2.positionName, temp2.positionStartTime,temp2.view_report,temp2.positionNotes, temp2.positionEndTime, " +
			"temp2.locAddr1, temp2.locAddr2, temp2.locCity,temp2.locState, temp2.locZipcode, temp2.locContactName, temp2.locPhone, temp2.locEmail, temp2.parking_direction, " +
			"date_format(sio.start_time,'%l:%i %p') as start_time , " +
			"date_format(sio.end_time,'%l:%i %p') as end_time, date_format(sio.break_start,'%l:%i %p') as break_start, date_format(sio.break_end,'%l:%i %p') as break_end, sio.remarks " +
			"from ( select temp1.id as scheduleId, e.id as eventId, e.name as eventName, e.event_startdate as startDate, e.event_enddate as endDate, date_format(e.start_time,'%l:%i %p') as startTime , " +
			"date_format(e.end_time,'%l:%i %p') as endTime, e.notes as eventNotes, e.dress_code as eventDressCode, e.host as eventHost, e.description as eventDesc, p.name as positionName," +
			"p.view_report, date_format(temp1.start_time,'%l:%i %p') as positionStartTime, date_format(temp1.end_time,'%l:%i %p') as positionEndTime, temp1.positionNotes," +
			"l.address1 as locAddr1, l.address2 as locAddr2, l.city as locCity,l.state as locState, l.zipcode as locZipcode, l.contact_name as locContactName,l.primary_phone as locPhone, " +
			"l.email as locEmail, l.parking_direction " +
			"from ( select epos.event_id, epos.position_id,epos.start_time,epos.end_time,epos.notes as positionNotes,s.* " +
			"from event_position epos, schedule s " +
			"where epos.id = s.event_pos_id and s.employee_id = ? and s.company_id = ?  and s.status=2 and s.active=true and epos.active=true) as temp1, event e, position p,location l " +
			"where temp1.event_id = e.id and temp1.position_id = p.id and e.location_id = l.id "; 
	
	private final String findNotifiedSchedules = " select temp1.id as scheduleId,temp1.status as scheduleStatus, temp1.active ,temp1.notify_expiretime, temp1.firstname, temp1.lastname, " +
			"e.id as eventId, e.name as eventName, e.event_startdate as startDate, e.event_enddate as endDate, " +
			"date_format(e.start_time,'%l:%i %p') as startTime , date_format(e.end_time,'%l:%i %p') as endTime, e.notes as eventNotes, e.dress_code as eventDressCode, e.host as eventHost, " +
			"e.description as eventDesc, " +
			"p.name as positionName,temp1.event_pos_id as eventPosId, date_format(temp1.start_time,'%l:%i %p') as positionStartTime, " +
			"date_format(temp1.end_time,'%l:%i %p') as positionEndTime, " +
			"temp1.notes as eventPosNotes," +
			"l.name as locName, l.code as locCode,l.address1 as locAddr1, l.address2 as locAddr2, l. city as locCity,l.state as locState, l.zipcode as locZipcode, " +
			"l.contact_name as locContactName,l.primary_phone as locPhone, l.email as locEmail " +
			"from ( select emp.firstname,emp.lastname,  epos.event_id,epos.position_id,epos.start_time,epos.end_time,epos.notes,s.* from event_position epos, schedule s, EMPLOYEE emp " +
			"where epos.id = s.event_pos_id and s.employee_id = ? and s.company_id = ? and s.status = 1 and s.active=true and epos.active=true and epos.start_date >= Date(?) and " +
			"s.employee_id = emp.id) as temp1, event e,position p,location l where temp1.event_id = e.id and temp1.position_id = p.id and e.location_id = l.id ";   
	
	
	private final String filterTodaySchedules = " and (e.event_startdate BETWEEN DATE(?) and DATE(?))" ;
	private final String sql = " ) temp2 left join signinout sio on temp2.scheduleId = sio.schedule_id ";
	private final String filterYesterdaySchedules = " and e.event_startdate < Date(?) ";
	private final String filterTomorrowSchedules = " and e.event_startdate > Date(?) ";
	private final String selectSignInOutQuery = "select temp.id as schId, temp.employee_id, temp.firstname, temp.lastname, temp.position_id, temp.name as posname,sio.id as signinid,date_format(sio.start_time,'%l:%i %p') as start_time , date_format(sio.end_time,'%l:%i %p') as end_time, date_format(sio.break_start,'%l:%i %p') as break_start, date_format(sio.break_end,'%l:%i %p') as break_end, sio.remarks from( SELECT s.id, s.employee_id,epos.position_id, p.name, e.firstname, e.lastname FROM event_position epos, schedule s, employee e, position p where s.event_pos_id = epos.id and s.employee_id = e.id and epos.position_id = p.id and epos.event_id = ? and s.active = true and s.company_id = ? and s.status = 2) as temp left join signinout sio on temp.id = sio.schedule_id";  
	public void save(final List<Schedule> schedules){
            getJdbcTemplate().batchUpdate(createScheduleSql, new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Schedule schedule = schedules.get(i);
                            ps.setInt(1,schedule.getEventPositionId());
                            ps.setInt(2,schedule.getEmployee().getId());
                            ps.setBoolean(3, schedule.isActive());
                            ps.setInt(4,schedule.getTenant().getId());
                            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            ps.setInt(5,schedule.getSchedulestatus());
                            ps.setString(6,dateformat.format(schedule.getNotifiedtime()));
                            ps.setString(7,dateformat.format(schedule.getExpiretime()));
                    }

                    @Override
                    public int getBatchSize() {
                            return schedules.size();
                    }
              });
        }
        
        public int save(final Schedule schedule){
            getJdbcTemplate().update(createScheduleSql, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps)
						throws SQLException {
					ps.setInt(1,schedule.getEventPositionId());
                    ps.setInt(2,schedule.getEmployee().getId());
                    ps.setBoolean(3, schedule.isActive());
                    ps.setInt(4,schedule.getTenant().getId());
                    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    ps.setInt(5,schedule.getSchedulestatus());
                    ps.setString(6,dateformat.format(schedule.getNotifiedtime()));
                    ps.setString(7,dateformat.format(schedule.getExpiretime()));
				}

                    
              });
            return getJdbcTemplate().queryForInt( "select last_insert_id()");
        }
        
        public void update(final List<Schedule> updateSchedules) {
        	getJdbcTemplate().batchUpdate(updateScheduleSql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Schedule schedule = updateSchedules.get(i);
                        ps.setInt(1,schedule.getSchedulestatus());
                        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        ps.setString(2,dateformat.format(schedule.getNotifiedtime()));
                        ps.setString(3,dateformat.format(schedule.getExpiretime()));
                        ps.setNull(4,java.sql.Types.DATE);
                        
                        ps.setInt(5,schedule.getId());
                        ps.setBoolean(6, schedule.isActive());
                        ps.setInt(7,schedule.getEventPositionId());
                        ps.setInt(8,schedule.getEmployee().getId());
                        ps.setInt(9,schedule.getTenant().getId());
                        
                }

                @Override
                public int getBatchSize() {
                        return updateSchedules.size();
                }
          });
		}
        
        public List<Position> findByEvent(final Integer eventId, final Integer tenantId) {
                //List<Schedule> schedules = getJdbcTemplate().query("select * from schedule where schedule.event_id = ? and schedule.company_id = ? and schedule.active= true",new Object[]{eventId,tenantId}, new ScheduleRowMapper());
                //List<Schedule> schedules = getJdbcTemplate().query("SELECT s.*, sio.id as signinid,date_format(sio.start_time,'%l:%i %p') as start_time , date_format(sio.end_time,'%l:%i %p') as end_time, date_format(sio.break_start,'%l:%i %p') as break_start, date_format(sio.break_end,'%l:%i %p') as break_end, sio.remarks FROM (select * from schedule where schedule.event_id = ? and schedule.company_id = ? and schedule.active= true) as s left join signinout sio on s.id = sio.schedule_id",new Object[]{eventId,tenantId}, new ScheduleRowMapper());
        		List<Position> scheduledPositions = getJdbcTemplate().query(selectSignInOutQuery, new Object[]{eventId,tenantId}, new ScheduleWithPositionExtractor(tenantId) );
                return scheduledPositions;
        }

		public List<Employee> findEmployeeBySchedule(final Integer eventId,final Integer shiftId,final Integer positionId,final Integer tenantId) {
			return getJdbcTemplate().query("SELECT schedule.id as scheduleId, employee.* FROM schedule,employee where schedule.employee_id = Employee.id and event_id = ? and shift_id = ? and position_id = ? and active = 1 and schedule.company_id = ?",new Object[]{eventId,shiftId,positionId,tenantId}, new EmployeeRowMapper());
		}

		/*public List<Schedule> findEmployeeSchedules(final Integer employeeId, final Integer tenantId) {
			return getJdbcTemplate().query(scheduleByEmployeeSql,new Object[]{employeeId} ,new EmployeeScheduleRowMapper());
			
		}*/

		public List<Employee> findEmployeeByScheduleAndShift(Integer eventId, Integer shiftId, Integer tenantId) {
			
			return getJdbcTemplate().query("SELECT schedule.id as scheduleId, employee.* FROM schedule,employee where schedule.employee_id = Employee.id and event_id = ? and shift_id = ? and active = 1 and schedule.company_id = ?",new Object[]{eventId,shiftId,tenantId}, new EmployeeRowMapper());
		}
		public Integer findSchedule(final Integer eventPosId, final Integer employeeId, final Integer companyId){
			List<Integer> scheduleIds =  getJdbcTemplate().queryForList("select id from schedule where event_pos_id = ? and employee_id = ? and company_id = ?", Integer.class,new Object[]{eventPosId,employeeId,companyId});
			return (scheduleIds.size()>0) ? scheduleIds.get(0) : null ;
		}
		
		public void delete(final List<Schedule> deleteSchedules) {
			getJdbcTemplate().batchUpdate(updateScheduleSql, new BatchPreparedStatementSetter() {
			
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Schedule schedule = deleteSchedules.get(i);
                        ps.setBoolean(1, false);
                        ps.setInt(2,schedule.getEventPositionId());
                        ps.setInt(3,schedule.getEmployee().getId());
                        ps.setInt(4,schedule.getTenant().getId());
                }

                @Override
                public int getBatchSize() {
                        return deleteSchedules.size();
                }
          });
			
		}
		public void deleteSchedule(final List<Schedule> deleteSchedules) {
			getJdbcTemplate().batchUpdate("delete from schedule where id = ? and event_pos_id = ? and employee_id = ? and company_id = ? ", new BatchPreparedStatementSetter() {
			
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Schedule schedule = deleteSchedules.get(i);
                        ps.setInt(1, schedule.getId());
                        ps.setInt(2,schedule.getEventPositionId());
                        ps.setInt(3,schedule.getEmployee().getId());
                        ps.setInt(4,schedule.getTenant().getId());
                }

                @Override
                public int getBatchSize() {
                        return deleteSchedules.size();
                }
          });
			
		}
		
		
		public List<Position> findPositionByShift(Integer eventId, Integer shiftId, Integer companyId) {
			return getJdbcTemplate().query("SELECT distinct p.* FROM schedule s, position p where  s.position_id = p.id and event_id=? and shift_id=? and s.company_id = ?",new Object[]{eventId,shiftId,companyId}, new PositionRowMapper());
		}

		public List<Schedule> findAllScheduledEventPosition(final Integer employeeId, final Integer companyId) {
			return getJdbcTemplate().query(findScheduleByEventPositionSql+sql,new Object[]{employeeId,companyId} ,new EmployeeScheduleRowMapper());
		}

		public Integer findPendingScheduleForEvent(final Integer eventId, final Integer companyId){
			return getJdbcTemplate().queryForInt("SELECT temp1.reqdNum - temp2.schNum from (select sum(epos.reqd_number) as reqdNum from event_position epos where epos.event_id = ? and epos.active = true and epos.company_id = ?) as temp1, (select count(*) as schNum from event_position epos1, schedule s where s.event_pos_id = epos1.id and epos1.event_id = ? and epos1.active = true and s.active = true and s.company_id=? and s.status=2) as temp2", new Object[]{eventId,companyId,eventId,companyId});
		}
		
		
		
		public List<Schedule> findAllScheduleByEmployee(final Integer month, final Integer employeeId, final Integer companyId) {
			String allScheduleByEmployee = "select temp1.id as scheduleId, e.id as eventId, e.name as eventName, e.event_startdate as startDate,date_format(sio.start_time,'%l:%i %p') as startTime , date_format(sio.end_time,'%l:%i %p') as endTime, date_format(sio.break_start,'%l:%i %p') as breakStartTime , date_format(sio.break_end,'%l:%i %p') as breakEndTime from ( select epos.event_id, epos.position_id,epos.start_time,epos.end_time,s.* from event_position epos, schedule s where epos.id = s.event_pos_id and s.employee_id = ? and s.company_id = ?) as temp1, (select * from event  where company_id = ? and status = 'PUBLISHED' and MONTH(event_startdate)=? order by event_startdate) as e, signinout sio where temp1.event_id = e.id and temp1.id = sio.schedule_id";
			//String allScheduleByEmployee = "select temp1.id as scheduleId, e.id as eventId, e.name as eventName, e.event_startdate as startDate,date_format(sio.start_time,'%l:%i %p') as startTime , date_format(sio.end_time,'%l:%i %p') as endTime, date_format(sio.break_start,'%l:%i %p') as breakStartTime , date_format(sio.break_end,'%l:%i %p') as breakEndTime  from ( select epos.event_id, epos.position_id,epos.start_time,epos.end_time,s.* from event_position epos, schedule s  where epos.id = s.event_pos_id and s.employee_id = ? and s.company_id = ?) as temp1, event e, signinout sio  where temp1.event_id = e.id and temp1.id = sio.schedule_id";
			return getJdbcTemplate().query(allScheduleByEmployee, new Object[]{employeeId,companyId,companyId,month},new EmployeeScheduleReportRowMapper());
			
		}
		
		
		public List<Employee> findAllScheduledEmpByEvent(final Integer eventId, final Integer companyId){
			String allSchEmpByEvent = "select s.employee_id as emp_id, date_format(epos.start_time,'%l:%i %p') as startTime , date_format(epos.end_time,'%l:%i %p') as endTime,epos.displayOrder from event_position epos, schedule s where epos.id = s.event_pos_id and epos.event_id = ? and epos.company_id = ? and epos.active = true and s.active=true order by displayOrder asc";
			return getJdbcTemplate().query(allSchEmpByEvent, new Object[]{eventId,companyId},new TotalForecastRowMapper());
			
		}
		public List<Employee> findAllSignInOutEmpByEvent(final Integer eventId, final Integer companyId){
			String allSchEmpByEvent = "select emp_id, date_format(sio.start_time,'%l:%i %p') as startTime , date_format(sio.end_time,'%l:%i %p') as endTime,date_format(sio.break_start,'%l:%i %p') as breakStartTime , date_format(sio.break_end,'%l:%i %p') as breakEndTime from( select s.employee_id as emp_id, s.id as schId from event_position epos, schedule s where epos.id = s.event_pos_id and epos.event_id = ? and epos.company_id = ? and epos.active = true and s.active=true) as sch left join signinout sio on sch.schId = sio.id";
			return getJdbcTemplate().query(allSchEmpByEvent, new Object[]{eventId,companyId},new TotalActualRowMapper());
			
		}

		public List<Schedule> findAllScheduledEventPositionByDate(final Integer employeeId, final Integer companyId, String mode) {
			List<Schedule> allSchedules = new ArrayList<Schedule>();
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			String today = dateformat.format(new Date());
			if(mode.equalsIgnoreCase("past")){
				allSchedules = getJdbcTemplate().query(findScheduleByEventPositionSql+filterYesterdaySchedules+sql,new Object[]{employeeId,companyId,today} ,new EmployeeScheduleRowMapper());
				
			}else if(mode.equalsIgnoreCase("future")){
				allSchedules = getJdbcTemplate().query(findScheduleByEventPositionSql+filterTomorrowSchedules+sql,new Object[]{employeeId,companyId,today} ,new EmployeeScheduleRowMapper());
				
			}else{
				allSchedules = getJdbcTemplate().query(findScheduleByEventPositionSql+filterTodaySchedules+sql,new Object[]{employeeId,companyId,today,today} ,new EmployeeScheduleRowMapper());
				
			}
			return allSchedules;
		}
		// all notified and accepted employees
		public List<Employee> findAllScheduledEmployeesByDate(Date date,
				Integer companyId) {
			String selectEmployeeByPosition = "select e.*,s.status,s.event_pos_id from employee as e,schedule as s,event_position as ep where s.status <=2 and s.event_pos_id = ep.id and s.employee_id = e.id and ep.start_date = DATE(?) and s.company_id = ?";
			return getJdbcTemplate().query(selectEmployeeByPosition,new Object[]{date,companyId}, new EmployeeRowMapper()) ;
			
		}
		//	all notified and accepted employees
		public List<Employee> findAllScheduledEmployeesByEvent(Integer eventId,
				Integer companyId) {
			String selectEmployeeByPosition = "select e.*,s.status from employee as e,schedule as s,event_position as ep where s.status <=2 and s.event_pos_id = ep.id and s.employee_id = e.id and ep.start_date = ? and s.company_id = ?";
			return getJdbcTemplate().query(selectEmployeeByPosition,new Object[]{eventId,companyId}, new EmployeeRowMapper()) ;
			
		}
		public Date findScheduleExpireTime(Integer employeeId,
				Integer companyId, Integer eventPosId) {
			// TODO Auto-generated method stub
			Date date = null;
			String expireDate = getJdbcTemplate().queryForObject("SELECT notify_expiretime FROM schedule where employee_id = ? and event_pos_id = ? and company_id = ? and active  = true;",new Object[]{employeeId,eventPosId,companyId}, String.class);
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				date = dateformat.parse(expireDate);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return date;
		}

		public void updateStatus(final Integer scheduleId,final Integer employeeId,final Integer companyId, final Integer eventPosId,final Integer status) {
			getJdbcTemplate().update("update schedule set status = ?, notify_updatetime = ? where id = ? and employee_id = ? and event_pos_id = ? and company_id = ? and active  = true;", new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps)
						throws SQLException {
					 ps.setInt(1,status);
					 SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                     ps.setString(2,dateformat.format(new Date()));
                     ps.setInt(3,scheduleId);
                     ps.setInt(4,employeeId);
                     ps.setInt(5,eventPosId);
                     ps.setInt(6, companyId);
                     
				}

                    
              });
		}

		public List<Schedule> findAllNotifiedSchedules(Integer employeeId,Integer companyId) {
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			String today = dateformat.format(new Date());
			return getJdbcTemplate().query(findNotifiedSchedules,new Object[]{employeeId,companyId,today} ,new NotifiedScheduleRowMapper());
		}

		public Schedule getSchedule(Integer scheduleId, Integer eventPositionId,
				Integer employeeId, Integer companyId) {
			String findScheduleDetailQuery = "select temp1.id as scheduleId,temp1.status as scheduleStatus, temp1.active ,temp1.notify_expiretime,temp1.firstname,temp1.lastname,e.id as eventId, e.name as eventName, e.event_startdate as startDate, e.event_enddate as endDate, date_format(e.start_time,'%l:%i %p') as startTime , date_format(e.end_time,'%l:%i %p') as endTime, e.notes as eventNotes, e.dress_code as eventDressCode, e.host as eventHost, e.description as eventDesc, p.name as positionName,temp1.event_pos_id as eventPosId, date_format(temp1.start_time,'%l:%i %p') as positionStartTime, date_format(temp1.end_time,'%l:%i %p') as positionEndTime,temp1.notes as eventPosNotes, l.name as locName,l.code as locCode,l.address1 as locAddr1, l.address2 as locAddr2, l.city as locCity,l.state as locState, l.zipcode as locZipcode, l.contact_name as locContactName,l.primary_phone as locPhone, l.email as locEmail from ( select emp.firstname, emp.lastname, epos.event_id,epos.position_id,epos.start_time,epos.end_time,epos.notes,s.* from event_position epos, schedule s,employee emp where s.id = ? and s.event_pos_id = ? and s.employee_id = ? and s.company_id = ? and epos.id = s.event_pos_id and s.employee_id = emp.id) as temp1, event e,position p,location l where temp1.event_id = e.id and temp1.position_id = p.id and e.location_id = l.id";
			List<Schedule> schedules =  getJdbcTemplate().query(findScheduleDetailQuery,new Object[]{scheduleId,eventPositionId,employeeId,companyId},new NotifiedScheduleRowMapper());
			if(schedules.size()>0)
				return	schedules.get(0);
			else
				return null;
			
			
			
		}

		public List<EventPosition> findNotifiedSchedulesByEvent(Integer eventId,Integer companyId) {
			String selectScheduleByEventSql = " select * from( " +
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
			"left join schedule s on temp1.event_posId = s.event_pos_id and s.active=true and s.status<=2) as temp2 " +
			"left join position p on temp2.position_id = p.id) as temp3 " +
			"left join employee e on temp3.employee_id = e.id " +
			"order by PosDisplayOrder asc";
			return getJdbcTemplate().query(selectScheduleByEventSql,new Object[]{eventId,companyId}, new EventPositionWithScheduleExtractor());
		}
		
		// delete notified schedules which are expired
		public void updateExpiredSchedules(final List<Schedule> expiredSchedules) {
			getJdbcTemplate().batchUpdate("update schedule set status = 4 where TIMEDIFF(now(),notify_expiretime)>0 and status = 1 and id = ?",new BatchPreparedStatementSetter() {
				
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

		public Integer findIfEmployeeDeclinedBefore(Date eventPositionStartDate,final  Integer employeeId,
				final Integer companyId) {
			//return getJdbcTemplate().queryForObject("SELECT count(*) from schedule where event_pos_id = ? and employee_id = ? and company_id = ?", new Object[]{eventPositionId,employeeId,companyId}, Integer.class);
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			final String startDate = dateformat.format(eventPositionStartDate);
			return getJdbcTemplate().queryForObject("SELECT count(*) FROM schedule s, event_position ep where s.event_pos_id = ep.id and  s.employee_id = ? " +
					"and s.company_id = ? and s.status = 3 and ep.start_date = Date(?)", new Object[]{employeeId,companyId,startDate},Integer.class);
		}
		
		public List<Schedule> findAllExpiredSchedules() {
			return getJdbcTemplate().query("select * from schedule where TIMEDIFF(now(),notify_expiretime)>0 and status = 1", new ScheduleRowMapper());
		}
		
		public void overrideSchedules(final List<Schedule> overrideSchedules) {
			getJdbcTemplate().batchUpdate("insert into Schedule(event_pos_id,employee_id,active,company_id,status,override_time) values (?,?,?,?,?,?)", new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Schedule schedule = overrideSchedules.get(i);
                        ps.setInt(1,schedule.getEventPositionId());
                        ps.setInt(2,schedule.getEmployee().getId());
                        ps.setBoolean(3, schedule.isActive());
                        ps.setInt(4,schedule.getTenant().getId());
                        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        ps.setInt(5,schedule.getSchedulestatus());
                        ps.setString(6,dateformat.format(schedule.getOverrideTime()));
                        
                }

                @Override
                public int getBatchSize() {
                        return overrideSchedules.size();
                }
          });
			
		}

		public List<Employee> findScheduledEmployeeByEventPosition(Integer eventPositionId, Integer regionId, Integer tenantId) {
			String selectEmployeeByPosition = "select e.*,s.status,s.event_pos_id from employee as e,schedule as s,event_position as ep where s.status <=2 and s.event_pos_id = ep.id and s.employee_id = e.id and ep.id = ? and ep.region_id = ? and s.company_id = ?";
			return getJdbcTemplate().query(selectEmployeeByPosition,new Object[]{eventPositionId,regionId, tenantId}, new EmployeeRowMapper()) ;
			
		}

		public List<Schedule> findAllCallOutSchedules(Integer empid,
				Integer eventid, Integer companyid) {
			return getJdbcTemplate().query("SELECT s.*, date_format(ep.start_time,'%l:%i %p') as positionStartTime, date_format(ep.end_time,'%l:%i %p') as positionEndTime,p.name as posname " +
					"FROM event_position ep,position p, schedule s " +
					"where ep.id = s.event_pos_id and p.id = ep.position_id and ep.event_id = ? and s.employee_id = ? and s.status<=2 and s.company_id = ?", new Object[]{eventid,empid,companyid},new RowMapper<Schedule>() {

				@Override
				public Schedule mapRow(ResultSet rs, int row)
						throws SQLException {
					Schedule schedule = new Schedule();
					schedule.setId(rs.getInt("id"));
					schedule.setSchedulestatus(rs.getInt("status"));
					schedule.setActive(rs.getBoolean("active"));
					
					
					if(rs.getObject("employee_id")!=null)
						schedule.setEmployee(new Employee(rs.getInt("employee_id")));
					if(rs.getObject("company_id")!=null)
						schedule.setTenant(new Tenant(rs.getInt("company_id")));
					
					EventPosition eventPosition = new EventPosition();
					if(rs.getObject("event_pos_id")!=null)
						schedule.setEventPositionId(rs.getInt("event_pos_id"));
					SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
					
					try {
						Date shiftStartTime = timeformat.parse(rs.getString("positionStartTime"));
						Date shiftEndTime = timeformat.parse(rs.getString("positionEndTime"));
						eventPosition.setStartTime(shiftStartTime);
						eventPosition.setEndTime(shiftEndTime);
					}catch (ParseException e) {
						e.printStackTrace();
					}
					Position position = new Position();
					position.setName(rs.getString("posname"));
					schedule.setEventPosition(eventPosition);
					schedule.setPosition(position);
					return schedule;
				}
			});
		}

		public List<Schedule> findAllNotifiedEventPosition(Integer employeeId,Integer companyId, Date eventDate) {
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			//String today = dateformat.format(new Date());
			 String findNotifiedByEventPositionSql = " select temp2.scheduleId,temp2.eventId,temp2.eventName,temp2.startDate,temp2.endDate, temp2.startTime,temp2.endTime,temp2.eventNotes, " +
			"temp2.eventDressCode,temp2.eventHost, temp2.eventDesc, temp2.positionName, temp2.positionStartTime,temp2.view_report,temp2.positionNotes, temp2.positionEndTime, " +
			"temp2.locAddr1, temp2.locAddr2, temp2.locCity,temp2.locState, temp2.locZipcode, temp2.locContactName, temp2.locPhone, temp2.locEmail, temp2.parking_direction, " +
			"date_format(sio.start_time,'%l:%i %p') as start_time , " +
			"date_format(sio.end_time,'%l:%i %p') as end_time, date_format(sio.break_start,'%l:%i %p') as break_start, date_format(sio.break_end,'%l:%i %p') as break_end, sio.remarks " +
			"from ( select temp1.id as scheduleId, e.id as eventId, e.name as eventName, e.event_startdate as startDate, e.event_enddate as endDate, date_format(e.start_time,'%l:%i %p') as startTime , " +
			"date_format(e.end_time,'%l:%i %p') as endTime, e.notes as eventNotes, e.dress_code as eventDressCode, e.host as eventHost, e.description as eventDesc, p.name as positionName," +
			"p.view_report, date_format(temp1.start_time,'%l:%i %p') as positionStartTime, date_format(temp1.end_time,'%l:%i %p') as positionEndTime, temp1.positionNotes," +
			"l.address1 as locAddr1, l.address2 as locAddr2, l.city as locCity,l.state as locState, l.zipcode as locZipcode, l.contact_name as locContactName,l.primary_phone as locPhone, " +
			"l.email as locEmail, l.parking_direction " +
			"from ( select epos.event_id, epos.position_id,epos.start_time,epos.end_time,epos.notes as positionNotes,s.* " +
			"from event_position epos, schedule s " +
			"where epos.id = s.event_pos_id and s.employee_id = ? and s.company_id = ?  and (s.status=2 or s.status=1) and s.active=true and epos.active=true) as temp1, event e, position p,location l " +
			"where temp1.event_id = e.id and temp1.position_id = p.id and e.location_id = l.id "; 
			return getJdbcTemplate().query(findNotifiedByEventPositionSql+filterTodaySchedules+sql,new Object[]{employeeId,companyId,eventDate,eventDate} ,new EmployeeScheduleRowMapper());
		}
		
		public void expireOldNotifiedSchedulesByEventPosition(Integer scheduleId,Integer eventPosId, Integer companyId) {
			 getJdbcTemplate().update("update schedule s set s.status = 4 where s.id = ? and s.event_pos_id = ? and  s.company_id = ? and s.status <2 ", new Object[]{scheduleId,eventPosId,companyId});			
		}

		

}