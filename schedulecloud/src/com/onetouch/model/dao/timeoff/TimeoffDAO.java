package com.onetouch.model.dao.timeoff;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TimeOffRequest;

import com.onetouch.model.mapper.employee.EmployeeRowMapper;
import com.onetouch.model.mapper.timeoff.EmployeeTimeoffMapper;
import com.onetouch.model.mapper.timeoff.TimeoffMapper;


@Repository
@Qualifier("timeoffDAO")
public class TimeoffDAO extends BaseDAO{

        //                                                                          1            2             3
	private final String createTimeOffSql = "insert into employee_time_off (employee_id, company_id, time_off_type, " +
        //            4           5         6           7 
                " start_date, end_date, num_days, create_date,reason,event_id) values (?,?,?,?,?,?,?,?,?)";
        //                                                                            1             2            3          4                5
	private final String updateTimeOffSql = "update employee_time_off set time_off_type=?, start_date=?, end_date=?, num_days=? where id=?";
        //                                                                         1
	private final String deleteTimeOffSql = "delete employee_time_off where id = ?";
	
    public List<TimeOffRequest> findAll(final Integer employeeId){
		return getJdbcTemplate().query("select * from employee_time_off where employee_id = ? ",
                        new Object[]{employeeId}, new TimeoffMapper());
	}
    public List<TimeOffRequest> findAllCallOut(final Integer employeeId){
		return getJdbcTemplate().query("SELECT eto.*,e.name  FROM employee_time_off eto left join event e on eto.event_id =e.id where eto.time_off_type = 'Call Out' and eto.employee_id = ?",
                        new Object[]{employeeId}, new TimeoffMapper());
	}
	public Integer save(final TimeOffRequest timeOff){
		getJdbcTemplate().update(createTimeOffSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {			
                                ps.setInt(1, timeOff.getEmployeeId());
				ps.setInt(2, timeOff.getCompanyId());
				ps.setString(3, timeOff.getRequestType());
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				ps.setString(4,dateformat.format(timeOff.getBeginDate()));
				ps.setString(5,dateformat.format(timeOff.getEndDate()));
                ps.setInt(6, timeOff.getNumDays());
                SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                ps.setString(7, dateformat1.format(timeOff.getCreateDate()));
                ps.setString(8,timeOff.getReason());
                if(timeOff.getSickEvent()!=null)
                	ps.setInt(9, timeOff.getSickEvent().getId());
                else
                	ps.setNull(9,Types.INTEGER);
			}
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public void update(final TimeOffRequest timeOff){
		getJdbcTemplate().update(updateTimeOffSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {                           
				ps.setString(1, timeOff.getRequestType());
				ps.setDate(2, new java.sql.Date(timeOff.getBeginDate().getTime()));
				ps.setDate(3, new java.sql.Date(timeOff.getEndDate().getTime()));
				ps.setInt(4, timeOff.getNumDays());
				ps.setInt(5, timeOff.getId());			
			}
		});
	}

	public List<TimeOffRequest> findByCompany(final Integer companyId){
		return getJdbcTemplate().query("select * from employee_timr_off where company_id = ? ",
                        new Object[]{companyId}, new TimeoffMapper());
	}



	public TimeOffRequest findById(Integer id) {
		List<TimeOffRequest> timeOffs = getJdbcTemplate().query("select * from employee_time_off where id = ? ",new Object[]{id}, new TimeoffMapper());

		return (timeOffs.size()>0) ? timeOffs.get(0) : null;
	}
	
	
	public List<TimeOffRequest> findAllVacationByEmployee(final Integer month, final Integer employeeId, final Integer companyId){
		String vacationTimeOffSql = "select * from employee_time_off where employee_id = ? and company_id = ? and time_off_type = 'Vacation' and MONTH(start_date)=?";
		List<TimeOffRequest> timeOffList = getJdbcTemplate().query(vacationTimeOffSql,new Object[]{employeeId,companyId,month}, new EmployeeTimeoffMapper());
		return timeOffList;
	}

	public List<TimeOffRequest> findAllSickByEmployee(final Integer employeeId, final Integer companyId,Integer month) {
		String sickTimeoffSql = "select * from employee_time_off eto,(select id as eventId, name as eventName from event where company_id = ? and MONTH(event_startdate)=?) e where eto.employee_id = ? and eto.company_id = ? and eto.time_off_type = 'Sick' and eto.event_id = e.eventId ";
		List<TimeOffRequest> timeOffList = getJdbcTemplate().query(sickTimeoffSql,new Object[]{companyId,month,employeeId,companyId}, new EmployeeTimeoffMapper());
		return timeOffList;
	}

	public List<Employee> findSickEmployeesByEvent(Integer eventId, Integer companyId) {
		
		return getJdbcTemplate().query("SELECT * FROM employee_time_off e,employee emp where e.employee_id = emp.id and e.event_id = ? and e.company_id = ? ",new Object[]{eventId,companyId}, new RowMapper<Employee>() {

			@Override
			public Employee mapRow(ResultSet rs, int row) throws SQLException {
				Employee employee = new Employee();
				employee.setId(rs.getInt("employee_id"));
				return employee;
			}
		});
	}
	public void deleteSickTimeOff(TimeOffRequest timeOff) {
		final Integer tid = timeOff.getId();
		final Integer eid = timeOff.getEmployeeId();
		final Integer cid = timeOff.getCompanyId();
		getJdbcTemplate().update("delete from employee_time_off where id = ? and employee_id = ? and company_id = ?", new Object[]{tid,eid,cid});
	}
	public void updateCallOutRequest(final TimeOffRequest timeOff) {
		getJdbcTemplate().update("update employee_time_off set reason=? where id=? and employee_id = ? and company_id = ?", new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {                           
				ps.setString(1, timeOff.getReason());
				ps.setInt(2, timeOff.getId());	
				ps.setInt(3, timeOff.getEmployeeId());
				ps.setInt(4, timeOff.getCompanyId());
			}
		});
	}
	public List<Employee> findAllCallOutByDate(Date eventDate, final Integer companyId){
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		String timeoffDate = dateformat.format(eventDate);
		return getJdbcTemplate().query("SELECT eto.*,e.name FROM employee_time_off eto left join event e on eto.event_id =e.id where eto.time_off_type = 'Call Out' and eto.start_date = Date(?) and e.company_id = ? ",
                new Object[]{timeoffDate,companyId}, new RowMapper<Employee>() {

			@Override
			public Employee mapRow(ResultSet rs, int row) throws SQLException {
				Employee employee = new Employee();
				employee.setId(rs.getInt("employee_id"));
				employee.setTenant(new Tenant(companyId));
				
				return employee;
			}
		});
	}
	
}
