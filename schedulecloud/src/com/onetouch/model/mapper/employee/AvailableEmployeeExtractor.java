package com.onetouch.model.mapper.employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.util.DateUtil;

public class AvailableEmployeeExtractor implements ResultSetExtractor<List<Employee>>{

	private List<Employee> sickEmployees;
	private Region region;	
	private Tenant tenant;
	private EventPosition eventPosition;
	public AvailableEmployeeExtractor(Tenant tenant,Region region,EventPosition eventPosition){
		//this.sickEmployees = sickEmployees;
		this.tenant = tenant;
		this.region = region;
		this.eventPosition = eventPosition;
	}
	@Override
	public List<Employee> extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		Date epst = DateUtil.getDateAndTime(eventPosition.getStartDate(), eventPosition.getStartTime());
		Date epet = DateUtil.getDateAndTime(eventPosition.getEndDate(), eventPosition.getEndTime());
		if(DateUtil.dateRange(epst, epet)==1){
			if(DateUtil.afterMidnight(epst,epet))
			epet = DateUtil.getMidnight(eventPosition.getStartDate());
		}
		
		Map<Integer,Employee> employeeMap = new HashMap<Integer, Employee>();
		Employee employee = null;
		while(rs.next()){
			Integer employeeId = rs.getInt("employee_id");
			employee = employeeMap.get(employeeId);
			if(employee==null){
				employee  = new Employee();
				employee.setId(employeeId);
				employee.setTenant(tenant);
				employee.setRegion(region);
				employee.setEmail(rs.getString("email"));
				employee.setFirstname(rs.getString("firstname"));
				employee.setLastname(rs.getString("lastname"));
				employee.setRating(rs.getInt("rating"));
				employee.setHomephone(rs.getString("homephone"));
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {

					if(rs.getString("hiredate")!=null)
						employee.setHireDate(format.parse(rs.getString("hiredate")));
					
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
				
				/*if(sickEmployees.contains(employee))
					employee.setSick(true);*/
				if(!employee.isSick()){
					if(rs.getObject("timeoffId")!=null)
						employee.setSick(true);
				}
				if(rs.getObject("notify")!=null){
					int notified = rs.getInt("notify");
					//System.out.println(notified);
					if(notified==1)
						employee.setAlreadyNotified(true);
				}
				/*if(rs.getObject("notifiedEvent")!=null){
					employee.setNotifiedEventPositionId(rs.getInt("notifiedEvent"));
				}*/
				employeeMap.put(employeeId,employee);
			}
			List<Availability> allAvailability = employee.getAllAvailability();
			if(allAvailability==null){
				allAvailability = new ArrayList<Availability>();
				employee.setAllAvailability(allAvailability);
			}
			
			Integer employeeAvailabilityId = rs.getInt("id");
			if(employeeAvailabilityId>0){
				Availability availability = new Availability();
				availability.setId(employeeAvailabilityId);
				
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
				try {
					availability.setAvailDate(dateformat.parse(rs.getString("avail_date")));
					availability.setStartTime(timeformat.parse(rs.getString("starttime")));
					availability.setEndTime(timeformat.parse(rs.getString("endtime")));
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
				allAvailability.add(availability);
				
				//Date evntStartTime = DateUtil.getDateAndTime(event.getStartDate(), event.getStartTime());
				//Date evntEndTim = DateUtil.getDateAndTime(event.getEndDate(), event.getEndTime());
				if(epst!=null && epet!=null){
					for(Availability avail : allAvailability){
						Date ast = DateUtil.getDateAndTime(avail.getAvailDate(),avail.getStartTime());
						Date aet = DateUtil.getDateAndTime(avail.getAvailDate(),avail.getEndTime());
						if(ast!=null && aet!=null){
							
							
							if(!employee.isAvailable()){
								int a = DateUtil.compareDateAndTime(ast,epet);
								int b = DateUtil.compareDateAndTime(epst,aet);
								if((!DateUtil.afterMidnight(eventPosition.getStartTime(), eventPosition.getEndTime()))&&(a>0 || b>0)){
									employeeMap.remove(employeeId);
								}
								else{
									// considering no shift will start after midnight
									if(DateUtil.compareDateAndTime(ast,epst)<=0 && DateUtil.compareDateAndTime(aet,epet)>=0){
										employee.setAvailableLabel("Avail.png");
										employee.setAvailable(true);
									}
									else{
										if(DateUtil.isMidnight(availability.getEndTime())){
											if(DateUtil.compareDateAndTime(ast,epst)<=0 && DateUtil.compareDateAndTime(aet,epet,3)>=0){
												employee.setAvailableLabel("Avail.png");
												employee.setAvailable(true);
											}
											else{
												employee.setAvailableLabel("NAvail.png");
												employee.setAvailable(false);
											}
										}else{
											employee.setAvailableLabel("NAvail.png");
											employee.setAvailable(false);
										}
									}
									
									
								}
							}
						}
					}
				}
				
			}
		}
		return new ArrayList<Employee>(employeeMap.values());
	}
}
