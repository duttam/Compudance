package com.onetouch.model.dao.employee;



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
import com.onetouch.model.mapper.availability.EmployeeWithAvailabilityExtractor;
import com.onetouch.model.mapper.employee.EmployeePositionExtractor;
import com.onetouch.model.mapper.employee.EmployeeRateRowMapper;
import com.onetouch.model.mapper.employee.EmployeeRowMapper;
import com.onetouch.model.mapper.employee.EmployeeWithRateMapper;
import com.onetouch.model.mapper.location.LocationRowMapper;


@Repository
@Qualifier("employeeDAO")
public class EmployeeDAO extends BaseDAO{

	
	//private final String createEmployeeSql = "insert into employee(email,invitecode,firstname,lastname,adminnotes,status,company_id,createtime,image_bytes,image_name,image_type) values (?,?,?,?,?,?,?,?,?,?,?)";
	private final String createEmployeeSql = "insert into employee(email,invitecode,firstname,lastname,dob,address1,address2,city,state,statecode,zipcode,homephone,cellphone,fax,adminnotes,status,company_id,createtime,image_bytes,image_name,image_type,scAdmin,scManager,scSalesPerson,region_id, hiredate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String createEmployeeRateSql = "insert into employee_rate(emp_id,rate,start_date,end_date) values (?,?,?,?)";
	
	private final String updateEmployeeSql = "update employee set firstname=?, lastname=?,dob=?,address1=?,address2=?,city=?,state=?,statecode=?,zipcode=?,homephone=?,cellphone=?,fax=?,email = ?,getemail=?,getsms=? ";
	private final String selectEmployeePositionSql = "select distinct * from employee e,position p,employee_position pe where e.id = pe.employee_id and p.id = pe.position_id and e.company_id = ? and (e.status = 'invitesent' or e.status = 'active') ";
	/*private final String selectEmployeeWithAvailabilitySql = "select e.id,e.firstname,e.lastname,e.dob,e.address1,e.address2,e.city,e.state," +
                                                                                                         "e.statecode,e.zipcode,e.homephone,e.cellphone,e.fax,e.email, e.getemail,e.getsms," +
                                                                                                         "e.image_name,e.image_type,e.hourly_rate,e.adminnotes,a.id as availability_id,a.title,a.starttime," +
                                                                                                         "a.endtime,a.avail_allday,a.notes " +
                                                                                                         "FROM employee e,availability a where e.id=a.employee_id and e.status = 'active' and e.company_id = ?";*/
	private final String selectEmployeeWithAvailabilitySql = "select distinct e.* from employee e, availability a where e.id=a.employee_id and e.status = 'active' and e.company_id = ? AND avail_date = DATE(?) and e.region_id = ?";
	public final String selectAllEmployeeSql = "select distinct * from employee e";
	public final String whereAllEmployee =	" where e.company_id = ? and e.status = 'active'"; 
	public final String selectEmployeeByPosition = "select employee.* from employee,employee_position where employee.id = employee_position.employee_id and employee_position.position_id = ? and employee.region_id = ? and employee_position.company_id = ? and employee.status = 'active'"; 
	private final String whereByUser = "and e.id = ?";
	private final String whereByEmployeeId = "and e.company_id= ?";
	private final String orderby = " order by e.rating desc";
	public List<Employee> findAll(final Integer tenantId,final String[] employeeStatus,Integer regionId){
		//String where = "";
		/*String activeStatus = employeeStatus[0]=="active"?"active":null;
		String inviteStatus = employeeStatus[0]=="invitesent"?"invitesent":null;
		String inactiveStatus = employeeStatus[0]=="inactive"?"inactive":null;*/
		
		List<Employee> employees = null;
		if(employeeStatus.length==0 || employeeStatus.length>1){
			
			//employees = getJdbcTemplate().query(selectAllEmployeeSql+whereAllEmployee+orderby,new Object[]{tenantId} ,new EmployeeRowMapper());
			employees = getJdbcTemplate().query(selectAllEmployeeSql+whereAllEmployee+orderby,new Object[]{tenantId} ,new RowMapper<Employee>(){

				@Override
				public Employee mapRow(ResultSet rs, int col)
						throws SQLException {

					Employee employee = new Employee();
					Integer employeeId = rs.getInt("id"); 
					employee.setId(employeeId);
					employee.setFirstname(rs.getString("firstname"));
					employee.setLastname(rs.getString("lastname"));
					employee.setHomephone(rs.getString("homephone"));
					employee.setEmail(rs.getString("email"));
					employee.setEmployeeStatus(rs.getString("status"));
					employee.setRating(rs.getInt("rating"));
					
					return employee;
				}
				
			});
		}
		else{
			//employees = getJdbcTemplate().query(selectAllEmployeeSql+" where e.status = ? and e.company_id = ? and e.region_id = ?"+orderby,new Object[]{employeeStatus[0],tenantId,regionId}, new EmployeeRowMapper());
			employees = getJdbcTemplate().query(selectAllEmployeeSql+" where e.status = ? and e.company_id = ? and e.region_id = ?"+orderby,new Object[]{employeeStatus[0],tenantId,regionId} ,new RowMapper<Employee>(){

				@Override
				public Employee mapRow(ResultSet rs, int col)
						throws SQLException {

					Employee employee = new Employee();
					Integer employeeId = rs.getInt("id"); 
					employee.setId(employeeId);
					employee.setFirstname(rs.getString("firstname"));
					employee.setLastname(rs.getString("lastname"));
					employee.setHomephone(rs.getString("homephone"));
					employee.setEmail(rs.getString("email"));
					employee.setEmployeeStatus(rs.getString("status"));
					employee.setRating(rs.getInt("rating"));
					
					return employee;
				}
				
			});
		}
		return employees;
		
		
	}
	
	public Integer save(final Employee employee){
		LobHandler lobHandler = new DefaultLobHandler();
		
		getJdbcTemplate().execute(createEmployeeSql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
            public void setValues(PreparedStatement ps, LobCreator lc) throws SQLException {
            	ps.setString(1, employee.getEmail());
                ps.setString(2, employee.getInvitecode());
                ps.setString(3, employee.getFirstname());
                ps.setString(4, employee.getLastname());
               
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        		if(employee.getDob()!=null)
        			ps.setString(5,sdf.format(employee.getDob()));
                else
                	ps.setNull(5,Types.DATE);
        		
        		ps.setString(6, employee.getAddress1());
 				ps.setString(7, employee.getAddress2());
 				ps.setString(8, employee.getCity());
                
                
                ps.setString(9, employee.getCity());
                
                if(employee.getState()!=null){
                	ps.setString(10, employee.getState().getStateName());
                	ps.setString(10, employee.getState().getStateCode());
                }
                else{
                	ps.setNull(10,Types.VARCHAR);
                	ps.setNull(10,Types.VARCHAR);
                }
                ps.setString(11, employee.getZipcode());
                ps.setString(12, employee.getHomephone());
                ps.setString(13, employee.getCellphone());
                ps.setString(14, employee.getFax());
                ps.setString(15, employee.getAdminNotes());
                ps.setString(16, employee.getEmployeeStatus());
                ps.setInt(17, employee.getTenant().getId());
                ps.setTimestamp(18,new Timestamp(new Date().getTime()));
                lc.setBlobAsBytes(ps, 19,employee.getImageBytes());
                ps.setString(20,employee.getImageName());
                ps.setString(21,employee.getImageType());
                ps.setBoolean(22,employee.isScAdmin());
                if(employee.isScAdmin())
                	ps.setBoolean(23, true);
                else 
                	ps.setBoolean(23, employee.isScManager());
                ps.setBoolean(24,employee.isScSalesPerson());
                ps.setInt(25,employee.getRegion().getId());
                if(employee.getHireDate()!=null)
        			ps.setString(26,sdf.format(employee.getHireDate()));
                else
                	ps.setNull(26,Types.DATE);
                
            }
		});
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public void saveUploadedEmployees(final List<Employee> employees){
		getJdbcTemplate().batchUpdate("insert into uploademployee(firstname,lastname,address1,address2,city,state,statecode,zipcode," +
				"homephone,cellphone,fax,email,company_id,hourlyRate,createtime,status,region_id,positions,hiredate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Employee employee = employees.get(i);
				
				ps.setString(1,employee.getFirstname());
				ps.setString(2,employee.getLastname());
				ps.setString(3, employee.getAddress1());
				ps.setString(4, employee.getAddress2());
				ps.setString(5, employee.getCity());
                ps.setString(6, employee.getState().getStateName());
                ps.setString(7, employee.getState().getStateCode());
                ps.setString(8, employee.getZipcode());
                ps.setString(9, employee.getHomephone());
                ps.setString(10, employee.getCellphone());
                ps.setString(11, employee.getFax());
                ps.setString(12, employee.getEmail());
				ps.setInt(13, employee.getTenant().getId());
				ps.setBigDecimal(14, employee.getEmpRate().getHourlyRate());
                ps.setTimestamp(15,new Timestamp(new Date().getTime()));
                ps.setString(16,employee.getEmployeeStatus());
                ps.setInt(17,employee.getRegion().getId());
                ps.setString(18, employee.getUploadedPositionString());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if(employee.getHireDate()!=null)
        			ps.setString(19,sdf.format(employee.getHireDate()));
                else
                	ps.setNull(19,Types.DATE);
			}
		 
			@Override
			public int getBatchSize() {
				return employees.size();
			}
		  });
	}
	
	
	public Integer saveRate(final Integer employeeId,final EmployeeRate employeeRate){
		getJdbcTemplate().update(createEmployeeRateSql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
            		
                    ps.setInt(1, employeeId);
                    ps.setBigDecimal(2, employeeRate.getHourlyRate());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    ps.setString(3,sdf.format(employeeRate.getRateStartDate()));
                    Calendar cal = Calendar.getInstance();
                    cal.set(3013, Calendar.DECEMBER, 31);
                    ps.setString(4,sdf.format(cal.getTime()));
            		//ps.setString(3,sdf.format(new Date()));
            		//ps.setString(4,sdf.format(new Date(0)));
            }
		});
	
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public void updateRate(Integer id, final EmployeeRate empRate) {
		getJdbcTemplate().update("update employee_rate set rate =?, end_date=? where id = ? ", new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
            		
                    
                    ps.setBigDecimal(1, empRate.getHourlyRate());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Long millis = empRate.getRateStartDate().getTime()-24*60*60*1000;
                    ps.setString(2,sdf.format(new Date(millis)));
                    ps.setInt(3, empRate.getId());
            }
		});
		
	}
	
	public void update(final boolean isadminView,final Employee employee) throws FileNotFoundException{
		LobHandler lobHandler = new DefaultLobHandler();
		
		if(isadminView){
			getJdbcTemplate().execute(updateEmployeeSql+",specialnotes=?,rating=?,scAdmin=?,scManager=?,scSalesPerson=?,code =?, emergency_contact=?,emergency_phone=?,hiredate=? where id=? and company_id=?", new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
	
				@Override
				protected void setValues(PreparedStatement ps, LobCreator lc)
						throws SQLException, DataAccessException {
					ps.setString(1, employee.getFirstname());
	        		ps.setString(2, employee.getLastname());
	        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        		if(employee.getDob()!=null)
	        			ps.setString(3,sdf.format(employee.getDob()));
	        		else
	        			ps.setString(3,sdf.format(new Date()));
	        		ps.setString(4, employee.getAddress1());
	                ps.setString(5, employee.getAddress2());
	                ps.setString(6, employee.getCity());
	                ps.setString(7, employee.getState().getStateName());
	                ps.setString(8, employee.getState().getStateCode());
	                ps.setString(9, employee.getZipcode());
	                ps.setString(10, employee.getHomephone());
	                ps.setString(11, employee.getCellphone());
	                
	                ps.setString(12, employee.getFax());
	                ps.setString(13, employee.getEmail());
	                if(employee.isGetEmail())
	                	ps.setString(14, "Y");
	                else
	                	ps.setString(14, "N");
	                if(employee.isGetSMS())
	                	ps.setString(15, "Y");
	                else
	                	ps.setString(15, "N");
	                ps.setString(16,employee.getSpecialNotes());
	                if(employee.getRating()!=null)
	                	ps.setInt(17,employee.getRating());
	                else
	                	ps.setNull(17, java.sql.Types.INTEGER);
	                
	                ps.setBoolean(18, employee.isScAdmin());
	                if(employee.isScAdmin())
	                	ps.setBoolean(19, true);
	                else
	                	ps.setBoolean(19, employee.isScManager());
                    ps.setBoolean(20, employee.isScSalesPerson());
                    ps.setInt(21, employee.getCode());
	                ps.setString(22, employee.getEmergencyContact());
	                ps.setString(23, employee.getEmergencyPhone());
	                if(employee.getHireDate()!=null)
	        			ps.setString(24,sdf.format(employee.getHireDate()));
	                else
	                	ps.setNull(24,Types.DATE);
	                ps.setInt(25, employee.getId());
	                ps.setInt(26, employee.getTenant().getId());
	                
				}
				       
			}
			);
		}else {
			
			getJdbcTemplate().execute(updateEmployeeSql+", image_bytes=?,image_name=?,image_type=?,scAdmin=?,scManager=?,scSalesPerson=?,code =?, emergency_contact=?,emergency_phone=? where id=? and company_id=?", new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
	            public void setValues(PreparedStatement ps, LobCreator lc) throws SQLException {
	            		ps.setString(1, employee.getFirstname());
	            		ps.setString(2, employee.getLastname());
	            		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            		if(employee.getDob()!=null)
	            			ps.setString(3,sdf.format(employee.getDob()));
	            		else
		        			ps.setString(3,sdf.format(new Date()));
	            		ps.setString(4, employee.getAddress1());
	                    ps.setString(5, employee.getAddress2());
	                    ps.setString(6, employee.getCity());
	                    ps.setString(7, employee.getState().getStateName());
	                    ps.setString(8, employee.getState().getStateCode());
	                    ps.setString(9, employee.getZipcode());
	                    ps.setString(10, employee.getHomephone());
	                    ps.setString(11, employee.getCellphone());
	                    
	                    ps.setString(12, employee.getFax());
	                    ps.setString(13, employee.getEmail());
	                    if(employee.isGetEmail())
	                    	ps.setString(14, "Y");
	                    else
	                    	ps.setString(14, "N");
	                    if(employee.isGetSMS())
	                    	ps.setString(15, "Y");
	                    else
	                    	ps.setString(15, "N");
	                    
						lc.setBlobAsBytes(ps, 16,employee.getImageBytes());
	                    ps.setString(17,employee.getImageName());
	                    ps.setString(18,employee.getImageType());
	                    ps.setBoolean(19, employee.isScAdmin());
	                    if(employee.isScAdmin())
		                	ps.setBoolean(20, true);
		                else
		                	ps.setBoolean(20, employee.isScManager());
	                    ps.setBoolean(21, employee.isScSalesPerson());
	                    ps.setInt(22, employee.getCode());
		                ps.setString(23, employee.getEmergencyContact());
		                ps.setString(24, employee.getEmergencyPhone());
	                    ps.setInt(25, employee.getId());
	                    ps.setInt(26, employee.getTenant().getId());
	                    
	            }
			});
		}
		
	}
	
	private Date getPrevDate(Date curDate){
		long prev = curDate.getTime() -  24 * 60 * 60 * 1000;
		return new Date(prev);

		
	}
	public Employee findEmployeeByUsername(String username, Integer tenantId) {
		List<Employee> employees = getJdbcTemplate().query("select * from employee where employee.username =? and employee.company_id = ? ",new Object[]{username,tenantId}, new EmployeeRowMapper());
		Assert.assertEquals(1,employees.size());
		if(employees.size()>0)
			return employees.get(0);
		else
			return null;
	}

	public List<Employee> findAvailabilityByEmployee(final Tenant tenant, Date eventDate,final Integer regionId) {
		
		List<Employee> employees = getJdbcTemplate().query(selectEmployeeWithAvailabilitySql,new Object[]{tenant.getId(),eventDate,regionId} ,new EmployeeRowMapper());
		return employees;
	}

	public Employee findById(Integer empId,Integer companyId) {
		//String getRecentRate = "select * from employee,(SELECT id as empRateId,emp_id,rate,start_date,end_date FROM employee_rate where emp_id = ? ORDER BY id DESC LIMIT 1) as employee_rate where employee.id = emp_id and employee.id = ? and company_id = ?";
		String getRecentRate = "select * from (select * from employee where employee.id = ? and company_id = ?) as emp left join (SELECT id as empRateId,emp_id,rate,start_date,end_date FROM employee_rate where emp_id = ? ORDER BY id DESC LIMIT 1) as emp_rate on emp.id = emp_rate.emp_id "; 
		List<Employee> employees = getJdbcTemplate().query(getRecentRate,new Object[]{empId,companyId,empId}, new EmployeeWithRateMapper());
		if(employees!=null&&employees.size()>0)
			return employees.get(0);
		
		return null;
	}

	public void saveEmployeePositionBatch(final Integer employeeId, final List<Position> positions, final Integer companyId) {
		getJdbcTemplate().batchUpdate("insert into employee_position (employee_id,position_id,company_id) values (?,?,?)", new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Position position = positions.get(i);
				Integer positionId = position==null?0:position.getId();
				ps.setInt(1, employeeId);
				ps.setInt(2, positionId);
				ps.setInt(3, companyId);
			}
		 
			@Override
			public int getBatchSize() {
				return positions.size();
			}
		  });
	}

	public void updateEmployeeStatus(final Integer id, final String status) {
		getJdbcTemplate().update("update Employee set employee.status = ? where employee.id = ?", new Object[]{status,id});
	}
	
	public Employee selectAllEmployeeWithPositions(final Integer emp_id, final Integer tenantId){
		
		List<Employee> employees = getJdbcTemplate().query("select distinct * from employee e left join employee_position pe inner join position p on pe.position_id = p.id on e.id = pe.employee_id where e.id = ? and e.company_id = ? and (e.status = 'invitesent' or e.status = 'active')",new Object[]{emp_id,tenantId} ,new EmployeePositionExtractor());
		//Assert.assertEquals(1,employees.size());
		if(employees!=null && employees.size()>0)
			return employees.get(0);
		
		return null;
	}
	
	public List<Employee> getAllEmployeeByPosition(Integer positionId,Integer regionId,Integer tenantId){
		return getJdbcTemplate().query(selectEmployeeByPosition,new Object[]{positionId,regionId,tenantId}, new EmployeeRowMapper());
		
	}
	public List<Employee> getOverrideEmployeeByPosition(Integer locationId,Integer positionId, final  Integer regionId, final  Integer tenantId){
		
		return getJdbcTemplate().query("select e.id as employee_id, e.firstname, e.lastname,e.rating, e.status,e.homephone,e.email,e.hiredate " +
				"from employee e ,employee_position ep " +
				"where e.id = ep.employee_id " +
				"and ep.position_id = ? " +
				"and e.region_id = ? " +
				"and e.company_id = ? " +
				"and ep.employee_id not in (select re.emp_id from restrict_emploc re where re.loc_id = ? and re.company_id = ?) " +
				"and e.status = 'active'",new Object[]{positionId,regionId,tenantId,locationId,tenantId}, new RowMapper<Employee>(){

					@Override
					public Employee mapRow(ResultSet rs, int col)
							throws SQLException {
						Employee employee  = new Employee();
						employee.setId(rs.getInt("employee_id"));
						employee.setTenant(new Tenant(tenantId));
						employee.setRegion(new Region(regionId));
						employee.setEmail(rs.getString("email"));
						employee.setFirstname(rs.getString("firstname"));
						employee.setLastname(rs.getString("lastname"));
						employee.setRating(rs.getInt("rating"));
						employee.setHomephone(rs.getString("homephone"));
						employee.setEmployeeStatus(rs.getString("status"));
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						try {

							if(rs.getString("hiredate")!=null)
								employee.setHireDate(format.parse(rs.getString("hiredate")));
							
						} catch (ParseException e) {
							
							e.printStackTrace();
						}
						employee.setAvailableLabel("Avail.png");
						employee.setAvailable(true);
						return employee;
					}});
	}
	public List<Employee> findAllOwners(final Integer companyId) {
		//SELECT e.* FROM employee e,employee_position ep,position p where ep.employee_id = e.id and ep.position_id = p.id and p.name='Admin' and ep.company_id = ?
		return getJdbcTemplate().query("SELECT e.* FROM employee e where e.scAdmin = true and e.company_id = ? and status = 'active' ",new Object[]{companyId} ,new EmployeeRowMapper());
	}

	public List<Employee> findAllCoordinators(final Integer companyId) {
		//  SELECT e.* FROM employee e,employee_position ep,position p where ep.employee_id = e.id and ep.position_id = p.id and p.name='Manager' and ep.company_id = ?
		return getJdbcTemplate().query("SELECT e.* FROM employee e where e.scManager = true and e.company_id = ? and status = 'active' ",new Object[]{companyId} ,new EmployeeRowMapper());
	}

	public List<Employee> findEmployeesByPosition(EventPosition eventPosition, Event event, Integer companyId, final Integer region_id) {
		final Integer position_id = eventPosition.getPosition().getId();
		/*SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		final String event_date = dateformat.format(event.getEventDate());
		final String position_starttime = timeformat.format(eventPosition.getStartTime());
		final String position_endtime = timeformat.format(eventPosition.getEndTime());*/
		
		String selectEmployeeByPosition = "select e.* from employee_position empos, employee e where empos.position_id = ? and empos.company_id = ? and e.region_id = ? and empos.employee_id = e.id and e.status='active'";
		return getJdbcTemplate().query(selectEmployeeByPosition,new Object[]{position_id,companyId,region_id}, new EmployeeRowMapper()) ;
		//return getJdbcTemplate().query("select * from( select e.* from employee_position empos, employee e where empos.position_id = ? and empos.employee_id = e.id ) as temp2 left join availability a on temp2.id = a.employee_id and  DATE(?) = a.avail_date and TIMEDIFF(TIME(?),a.starttime)>0 and TIMEDIFF(a.endtime,TIME(?))>0",new Object[]{position_id,event_date,position_starttime,position_endtime}, new EmployeeRowMapper()) ;		
	}

	public void deleteEmployeePositionBatch(final Integer employeeId, final List<Position> deletedPositions,final Integer companyId) {
		getJdbcTemplate().batchUpdate("delete from employee_position  where employee_id = ? and position_id = ? and company_id = ?  ", new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Position position = deletedPositions.get(i);
				Integer positionId = position==null?0:position.getId();
				ps.setInt(1, employeeId);
				ps.setInt(2, positionId);
				ps.setInt(3, companyId);
			}
		 
			@Override
			public int getBatchSize() {
				return deletedPositions.size();
			}
		  });
		
	}

	public EmployeeRate findEmployeeRateById(Date eventDate, final Integer empId, Integer id) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String edate = sdf.format(eventDate);
        EmployeeRate employeeRate = null;
		List<EmployeeRate> employeeRates =  getJdbcTemplate().query("SELECT * FROM employee_rate e where e.emp_id = ? and ( Date(?) BETWEEN start_date AND end_date)",new Object[]{empId,edate}, new EmployeeRateRowMapper()) ;
		if(employeeRates.size()>0)
			employeeRate = employeeRates.get(0);
		
		return employeeRate;
		
	}

	public void saveBatch(final Tenant tenant, final List<Employee> uploadEmployeeList) {
			getJdbcTemplate().batchUpdate("insert into employee(firstname,lastname,adminnotes,status,company_id,createtime,image_bytes,image_name,image_type) values (?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {
				 
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					
				}
			 
				@Override
				public int getBatchSize() {
					return uploadEmployeeList.size();
				}
			  });
		}

	public void deleteEmployee(Integer employeeId, Integer companyId) {
		
		getJdbcTemplate().update("update employee set status = 'InActive' where id = ? and company_id=?",new Object[]{employeeId,companyId});
	}

	public List<Employee> findAllUploadedEmployee(final Integer tenantId, final Integer regionId) {
		// TODO Auto-generated method stub
		return getJdbcTemplate().query("select * from uploademployee where company_id = ? and region_id = ?",new Object[]{tenantId, regionId}, new RowMapper<Employee>() {

			@Override
			public Employee mapRow(ResultSet rs, int row)
					throws SQLException {
				Employee employee = new Employee();
				Integer employeeId = rs.getInt("id"); 
				employee.setId(employeeId);
				employee.setFirstname(rs.getString("firstname"));
				employee.setLastname(rs.getString("lastname"));
				employee.setAddress1(rs.getString("address1"));
				employee.setAddress2(rs.getString("address2"));
				employee.setCity(rs.getString("city"));
				State state = new State();
				state.setStateName(rs.getString("state"));
				state.setStateCode(rs.getString("statecode"));
				employee.setState(state);
				employee.setZipcode(rs.getString("zipcode"));
				employee.setHomephone(rs.getString("homephone"));
				employee.setCellphone(rs.getString("cellphone"));
				employee.setFax(rs.getString("fax"));
				employee.setEmail(rs.getString("email"));
				employee.setEmployeeUploadStatus(rs.getString("status"));
				employee.setUploadedPositionString(rs.getString("positions"));
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					
					
					if(rs.getString("hiredate")!=null)
						employee.setHireDate(format.parse(rs.getString("hiredate")));
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				EmployeeRate employeeRate = new EmployeeRate();
				employeeRate.setHourlyRate(rs.getBigDecimal("hourlyRate"));
				employee.setEmpRate(employeeRate);
				return employee;
			}
		});
	}

	public void updateUploadedEmployeeStatus(final Employee employee, final String status) {
		getJdbcTemplate().update("update uploademployee set status = ? where id = ? and status = 'upload'", new Object[]{status,employee.getId()});
	}

	public void deleteUploadedEmployee(Integer employeeId, Integer companyId) {
		// TODO Auto-generated method stub
		getJdbcTemplate().update("delete from uploademployee where id = ? and company_id=?",new Object[]{employeeId,companyId});
	}

	public String findUsernameByEmail(String resetPasswdEmail) {
		// TODO Auto-generated method stub
		return getJdbcTemplate().queryForObject("select username from employee,users where employee.id = users.emp_id and users.enabled = true and email = ? ", String.class, new Object[]{resetPasswdEmail});
	}

	public void updateInvite(final Employee employee) {
		getJdbcTemplate().update("update employee set firstname=?, lastname=?,email = ?, scAdmin=?,scManager=?,scSalesPerson=? where id=? and company_id=? and region_id = ?", new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, employee.getFirstname());
        		ps.setString(2, employee.getLastname());
        		ps.setString(3, employee.getEmail());
        		ps.setBoolean(4,employee.isScAdmin());
                if(employee.isScAdmin())
                	ps.setBoolean(5, true);
                else 
                	ps.setBoolean(5, employee.isScManager());
                ps.setBoolean(6,employee.isScSalesPerson());
				ps.setInt(7,employee.getId());
				ps.setInt(8,employee.getTenant().getId());
				ps.setInt(9,employee.getRegion().getId());
			}
		});
	}

	public Region findRegionById(final Integer employeeId, final Integer companyId) {
		List<Region> regions =  getJdbcTemplate().query("select r.* from employee e, region r where e.region_id = r.id and e.id = ? and e.company_id = ?",new Object[]{employeeId,companyId}, new RowMapper<Region>() {

			@Override
			public Region mapRow(ResultSet rs, int row) throws SQLException {
				Region region = new Region(new Tenant(companyId));
				region.setId(rs.getInt("id"));
				region.setName(rs.getString("name"));
				region.setDescription(rs.getString("description"));
				return region;
			}
		});
		
		return regions.get(0);
	}

	public void saveSalesPersonLocation(final Integer employeeId,final List<Location> salesPersonLocations, final Integer companyId) {
		
		getJdbcTemplate().batchUpdate("insert into salesperson_loc(emp_id,loc_id,company_id) values (?,?,?)", new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Location salesPersonLocation = salesPersonLocations.get(i);
				ps.setInt(1, employeeId);
				ps.setInt(2, salesPersonLocation.getId());
				ps.setInt(3, companyId);
			}
		 
			@Override
			public int getBatchSize() {
				return salesPersonLocations.size();
			}
		  });
	
	}

	public List<Location> findSalesPersonLocation(Integer employeeId, Integer companyId) {
		List<Location> locations =  getJdbcTemplate().query("SELECT l.* FROM Location l,salesperson_loc s where s.loc_id=l.id and s.emp_id = ? and s.company_id = ?",new Object[]{employeeId,companyId},new LocationRowMapper() );
		
		return locations;
	}

	public void deleteSalesPersonLocation(final Integer empId, final List<Location> deleteSalesPersonLocation, final Integer companyId) {
		getJdbcTemplate().batchUpdate("delete from salesperson_loc  where emp_id = ? and loc_id = ? and company_id = ?  ", new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Location location = deleteSalesPersonLocation.get(i);
				ps.setInt(1, empId);
				ps.setInt(2, location.getId());
				ps.setInt(3, companyId);
			}
		 
			@Override
			public int getBatchSize() {
				return deleteSalesPersonLocation.size();
			}
		  });
	}

	public void saveRestrictEmpLocation(final Integer employeeId,
			final List<Location> newRestrictEmpLoc, final Integer companyId) {
		
		getJdbcTemplate().batchUpdate("insert into restrict_emploc(emp_id,loc_id,company_id) values (?,?,?)", new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Location restrictLocation = newRestrictEmpLoc.get(i);
				ps.setInt(1, employeeId);
				ps.setInt(2, restrictLocation.getId());
				ps.setInt(3, companyId);
			}
		 
			@Override
			public int getBatchSize() {
				return newRestrictEmpLoc.size();
			}
		  });
	
	}

	public void deleteRestrictEmpLocation(final Integer empId, final List<Location> deleteRestrictEmpLoc,final Integer companyId) {
		getJdbcTemplate().batchUpdate("delete from restrict_emploc  where emp_id = ? and loc_id = ? and company_id = ?  ", new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Location location = deleteRestrictEmpLoc.get(i);
				ps.setInt(1, empId);
				ps.setInt(2, location.getId());
				ps.setInt(3, companyId);
			}
		 
			@Override
			public int getBatchSize() {
				return deleteRestrictEmpLoc.size();
			}
		  });
		
	}

	public List<Location> findRestrictEmployeeLocation(Integer employeeId, Integer companyId) {
		List<Location> locations =  getJdbcTemplate().query("SELECT l.* FROM Location l,restrict_emploc s where s.loc_id=l.id and s.emp_id = ? and s.company_id = ?",new Object[]{employeeId,companyId},new LocationRowMapper() );
		
		return locations;
	}

	public List<Employee> findAllByEmailGroup(final EmailGroup emailGroup) {
		Integer emailGroupId = emailGroup.getId();
		Integer tenantId = emailGroup.getCompanyId();
		Integer regionId = emailGroup.getRegionId();
		List<Employee> employees = getJdbcTemplate().query("",new Object[]{emailGroupId,regionId,tenantId} ,new EmployeeRowMapper());
		return employees;
	}

	public Integer saveEmailGroup(final EmailGroup emailGroup) {
		getJdbcTemplate().update("insert into emailgroup (group_name,description,region_id,company_id) values (?,?,?,?)", new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, emailGroup.getGroupName());
        		ps.setString(2, emailGroup.getDescription());
        		ps.setInt(3,emailGroup.getRegionId());
        		ps.setInt(4,emailGroup.getCompanyId());
				
			}
			
			
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}

	public void SaveEmployeeEmailGroup(final List<Employee> employees,
			final Integer emailGroupId, final Integer companyId) {
		getJdbcTemplate().batchUpdate("insert into emp_emailgroup (emailGroup_id,emp_id,tenant_id) values (?,?,?)  ", new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Employee employee = employees.get(i);
				ps.setInt(1, emailGroupId);
				ps.setInt(1, employee.getId());
				ps.setInt(3, companyId);
			}

			@Override
			public int getBatchSize() {
				return employees.size();
			}
		 
		  });
	}

	

	public List<Employee> findAllEmployeesByRegionAndPosition(
			List<Integer> positionIds, List<Integer> regionIds, Tenant tenant) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(super.getDataSource());
		
		Object pids = Collections.singletonMap("positionIds", positionIds).get("positionIds");
		
		Object rids = Collections.singletonMap("regionIds", regionIds).get("regionIds");
		String filterEmployeeSQL = "select distinct e.id, e.firstname, e.lastname, e.email  from employee e left join  employee_position p  on p.employee_id = e.id where p.company_id = :tenantId and e.status='active'";
		String wherePosition = " and p.position_id in ( :positionIds)";
		String whereRegion = " and e.region_id in ( :regionIds)";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		if(positionIds!=null && positionIds.size()>0){
			filterEmployeeSQL = filterEmployeeSQL+wherePosition;
			namedParameters.addValue("positionIds",pids);
		}
		if(regionIds!=null && regionIds.size()>0){
			filterEmployeeSQL = filterEmployeeSQL+whereRegion;
			namedParameters.addValue("regionIds", rids);
		}
		
		namedParameters.addValue("tenantId", tenant.getId());
		
		
		
		@SuppressWarnings("unchecked")
		List<Employee> employees = namedParameterJdbcTemplate.query(filterEmployeeSQL , namedParameters, new RowMapper<Employee>() {

			@Override
			public Employee mapRow(ResultSet rs, int row) throws SQLException {
				Employee employee = new Employee();
				employee.setId(rs.getInt("id"));
				employee.setFirstname(rs.getString("firstname"));
				employee.setLastname(rs.getString("lastname"));
				employee.setEmail(rs.getString("email"));
				return employee;
			}
		}
	            );
		
		return employees;
		
	}

	public List<Employee> findAllSalesPersons(Location location, Tenant tenant) {
		if(location.getLocationType().equalsIgnoreCase("standard"))
			return getJdbcTemplate().query("SELECT e.* FROM employee e,salesperson_loc as sales where e.id = sales.emp_id and e.scSalesPerson = true and e.company_id = ? and status = 'active' and sales.loc_id = ? order by lastname asc",new Object[]{tenant.getId(),location.getId()} ,new EmployeeRowMapper());
		else
			return getJdbcTemplate().query("SELECT e.* FROM employee e where e.scSalesPerson = true and e.company_id = ? and status = 'active' order by lastname asc",new Object[]{tenant.getId()} ,new EmployeeRowMapper());
	
		
		
	}
}
