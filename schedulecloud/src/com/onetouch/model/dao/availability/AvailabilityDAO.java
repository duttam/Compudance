package com.onetouch.model.dao.availability;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.mapper.availability.AvailabilityRowMapper;
import com.onetouch.model.mapper.employee.EmployeeRowMapper;
import com.onetouch.view.util.DateUtil;

@Repository
public class AvailabilityDAO extends BaseDAO{
	static final Logger logger = Logger.getLogger("AvailabilityDAO.class");
	private final String createAvailabilitySql = "insert into availability(title,avail_date,starttime,endtime,employee_id,company_id) values (?,?,?,?,?,?)";
	private final String updateAvailabilitySql = "update availability set title=?,starttime=?,endtime=? where id=? and company_id = ?";
	public List<Availability> findAll(Integer companyId, Integer employeeId,Date start, Date end) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		String s = dateformat.format(start);
		String e = dateformat.format(end);
		return getJdbcTemplate().query("select * from availability where (avail_date BETWEEN DATE(?) and DATE(?)) and availability.employee_Id = ? and availability.company_id = ? order by avail_date",new Object[]{s,e,employeeId,companyId}, new AvailabilityRowMapper());
		//return null;
	}
	public List<Availability> findAll(Integer companyId, Integer employeeId) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		return getJdbcTemplate().query("select * from availability where availability.employee_Id = ? and availability.company_id = ? order by avail_date ",new Object[]{employeeId,companyId}, new AvailabilityRowMapper());
		//return null;
	}
	public Integer save(final Availability availability) {
		
		getJdbcTemplate().update(createAvailabilitySql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				ps.setString(1, availability.getTitle());
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
				ps.setString(2,dateformat.format(availability.getAvailDate()));
				//ps.setDate(3,new Date(availability.getStartTime().getTime()));
				//ps.setDate(4,new Date(availability.getEndTime().getTime()));
				if(availability.isAllday()){
					//String title = availability.getTitle()+" All Day";
					//availability.setTitle(title);
					ps.setString(3,timeformat.format(DateUtil.getDate("06:00 AM","hh:mm a")));
				}else 
					ps.setString(3,timeformat.format(availability.getStartTime()));
				ps.setString(4,timeformat.format(availability.getEndTime()));
				ps.setInt(5, availability.getEmployee().getId());
				ps.setInt(6, availability.getTenant().getId());
				
			}
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	public void saveAvailabilityBatch(final List<Availability> availabilityList) {
		getJdbcTemplate().batchUpdate(createAvailabilitySql, new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Availability availability = availabilityList.get(i);
				ps.setString(1, availability.getTitle());
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
				ps.setString(2,dateformat.format(availability.getAvailDate()));
				ps.setString(3,timeformat.format(availability.getStartTime()));
				ps.setString(4,timeformat.format(availability.getEndTime()));
				ps.setInt(5, availability.getEmployee().getId());
				ps.setInt(6, availability.getTenant().getId());
			}
		 
			@Override
			public int getBatchSize() {
				return availabilityList.size();
			}
		  });
	}
	public void update(final Availability availability) {
		getJdbcTemplate().update(updateAvailabilitySql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, availability.getTitle());
				SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
				//ps.setString(2,timeformat.format(availability.getStartTime()));
				if(availability.isAllday()){
					//String title = availability.getTitle()+" All Day";
					//availability.setTitle(title);
					ps.setString(2,timeformat.format(DateUtil.getDate("06:00 AM","hh:mm a")));
				}else 
					ps.setString(2,timeformat.format(availability.getStartTime()));
				ps.setString(3,timeformat.format(availability.getEndTime()));
				ps.setInt(4, availability.getId());
				ps.setInt(5, availability.getTenant().getId());
			}
		});
	}

	public Availability find(Integer availId, Integer employeeId, Integer companyId) {
		List<Availability> allAvailabilities = getJdbcTemplate().query("select * from availability where availability.id = ? and availability.employee_Id = ? and availability.company_id = ? ",new Object[]{availId,employeeId,companyId}, new AvailabilityRowMapper());
		
		return allAvailabilities.get(0);
		
	}

	public void delete(final Integer availId,final Integer employeeId,final Integer tenantId) {
		getJdbcTemplate().update("delete from availability where id=? and employee_id=? and company_id=?",new Object[]{availId,employeeId,tenantId});
		
	}

	/*public List<Employee> findAvailableEmployees(Event event,
			EventPosition eventPosition) {
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String eventDate = dateformat.format(event.getEventDate());
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		
		final String startTime = timeformat.format(eventPosition.getStartTime());
		final String endTime = timeformat.format(eventPosition.getEndTime());
		final Integer companyId = event.getTenant().getId();
		
		return getJdbcTemplate().query("SELECT * FROM (SELECT EMPLOYEE_ID  FROM AVAILABILITY A WHERE A.AVAIL_date = DATE(?) and TIMEDIFF(TIME(?),a.starttime)>0 and TIMEDIFF(a.endtime,TIME(?))>0 and A.COMPANY_ID=?) TEMP1, EMPLOYEE E WHERE TEMP1.EMPLOYEE_ID = E.ID",new Object[]{eventDate,startTime,endTime,companyId}, new EmployeeRowMapper());
	}*/

	public boolean IsEmployeeAvailable(Integer id, java.util.Date startTime,
			java.util.Date endTime, java.util.Date startDate,
			java.util.Date endDate, Integer id2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Integer findTotalAvailableTime(Date startDate,Date endDate,Integer empId,Integer companyId){
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String epStartDate = dateformat.format(startDate);
		final String epEndDate = dateformat.format(endDate);
		
		try{
			return getJdbcTemplate().queryForInt("select sum(TIME_TO_SEC(temp1.timediff)) as availabletime from( select temp.employee_id,TIMEDIFF(temp.endtime,temp.starttime) as timediff from ( SELECT * FROM availability a where a.avail_date = DATE(?) and employee_id = ? and company_id = ? union SELECT * FROM availability a where a.avail_date = DATE(?) and employee_id = ? and company_id = ? ) as temp ) as temp1 group by temp1.employee_id", new Object[]{epStartDate,empId,companyId,epEndDate,empId,companyId});
		}catch(EmptyResultDataAccessException e){
			
		}
		
		return null;
	}

	public Date findMinStartTime(Date startDate, Integer empId, Integer companyId) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String epStartDate = dateformat.format(startDate);
		String stime= (String)getJdbcTemplate().queryForObject("SELECT min(starttime) as minstart FROM availability a where a.avail_date = DATE(?) and employee_id = ? and company_id = ?", String.class,new Object[]{epStartDate,empId,companyId} );
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		try {
			if(stime!=null)
				return timeformat.parse(stime);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Date findMaxEndTime(Date endDate,Integer empId, Integer companyId) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String epEndDate = dateformat.format(endDate);
		
		String etime = (String)getJdbcTemplate().queryForObject("SELECT max(endtime) as endstart FROM availability a where a.avail_date = DATE(?) and employee_id = ? and company_id = ? ", String.class,new Object[]{epEndDate,empId,companyId} );
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		try {
			if(etime!=null)
				return timeformat.parse(etime);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	// Availabuility for only start date
	public List<Availability> findAllAvailableTime(Date startDate,Date endDate,Integer empId,Integer companyId) {
		//String selectAvailabilityByEmployee = "SELECT * FROM availability a where a.avail_date = DATE(?) and employee_id = ? and company_id = ? union SELECT * FROM availability a where a.avail_date = DATE(?) and employee_id = ? and company_id = ?";
		String selectAvailabilityByEmployee = "SELECT * FROM availability a where a.avail_date = DATE(?) and employee_id = ? and company_id = ? ";
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		final String epStartDate = dateformat.format(startDate);
		final String epEndDate = dateformat.format(endDate);
		
		
		return getJdbcTemplate().query(selectAvailabilityByEmployee, new Object[]{epStartDate,empId,companyId}, new AvailabilityRowMapper());
		
	}

	

}
