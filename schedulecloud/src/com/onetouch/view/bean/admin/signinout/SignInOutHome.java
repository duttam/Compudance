package com.onetouch.view.bean.admin.signinout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeePosition;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Position;

import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.context.TenantContextUtil;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="signInOut")
@ViewScoped
public class SignInOutHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Event signInOutEvent;
	private List<Position> scheduledPositions;
	private Tenant tenant;
	private List<Event> publishedEventList;
	private CustomUser customUser;
	@PostConstruct
	public void init(){
		tenant = tenantContext.getTenant();
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
			
		}
		scheduledPositions = new ArrayList<Position>();
		publishedEventList = getSignInOutService().getAllPublishedEvents(tenant,customUser);
		if(publishedEventList!=null && publishedEventList.size()>0){
			signInOutEvent = publishedEventList.get(0);//order by event start time
			this.selectEventListener();
		}
		
	}
	public void selectEventListener(){
		scheduledPositions = getScheduleService().getScheduleByEvent(signInOutEvent, tenant);
		for(Position position : scheduledPositions){
			List<Employee> addEmp = position.getEmployees()==null?new ArrayList<Employee>():position.getEmployees();
			
		}
		/*for(Shift shift : scheduledShifts){
			Map<Integer,Position> assignedPositions = shift.getEmployeePositions();
			
			if(assignedPositions!=null){
				List<Employee> employees = new ArrayList<Employee>();
				List<Position> positions = new ArrayList<Position>(assignedPositions.values());
				for(Position position : positions){
					if(position != null){
						List<Employee> addEmp = position.getEmployees()==null?new ArrayList<Employee>():position.getEmployees();
						for(Employee employee : addEmp){
							
							//employee.setSignInOut(new SignInOut());
							employee.setPosition(position);
							employees.add(employee);
						}
						
					}
				}
				shift.setAllScheduledEmployees(employees);
			}
		}*/
			
	}
	public void enterSignInTime(ActionEvent actionEvent){
		Integer empId = Integer.parseInt(FacesUtils.getRequestParameter("empid"));
		Integer posId = Integer.parseInt(FacesUtils.getRequestParameter("posid"));
		Integer schId = Integer.parseInt(FacesUtils.getRequestParameter("scheduleid"));
		
		SignInOut signInOut = new SignInOut();
		Date signInTime = new Date();
		signInOut.setStartTime(signInTime);
		signInOut.setSchedule_id(schId);
		signInOut = getSignInOutService().saveSignIn(signInOut);
		replaceSignInOutForEmployee(schId, posId,signInOut,false); // first save so update id
		
	}
	
	public void enterSignOutTime(ActionEvent actionEvent){
		Integer empId = Integer.parseInt(FacesUtils.getRequestParameter("empid"));
		Integer posId = Integer.parseInt(FacesUtils.getRequestParameter("posid"));
		Integer schId = Integer.parseInt(FacesUtils.getRequestParameter("scheduleid"));
		
		SignInOut signInOut = findSignInOutForEmployeeShift(schId,posId);
		Date signOutTime = new Date();
		signInOut.setEndTime(signOutTime);
		getSignInOutService().updateSignOut(signInOut);
		replaceSignInOutForEmployee(schId, posId, signInOut,true);
		
	}

	public void enterBreakSignInTime(ActionEvent actionEvent){
		Integer empId = Integer.parseInt(FacesUtils.getRequestParameter("empid"));
		Integer posId = Integer.parseInt(FacesUtils.getRequestParameter("posid"));
		Integer schId = Integer.parseInt(FacesUtils.getRequestParameter("scheduleid"));
		
		SignInOut signInOut = findSignInOutForEmployeeShift(schId, posId);
		Date breakStartTime = new Date();
		signInOut.setBreakStartTime(breakStartTime);
		getSignInOutService().updateBreakSignIn(signInOut);
		replaceSignInOutForEmployee(schId, posId, signInOut,false);
	}

	public void enterBreakSignOutTime(ActionEvent actionEvent){
		Integer empId = Integer.parseInt(FacesUtils.getRequestParameter("empid"));
		Integer posId = Integer.parseInt(FacesUtils.getRequestParameter("posid"));
		Integer schId = Integer.parseInt(FacesUtils.getRequestParameter("scheduleid"));
		
		SignInOut signInOut = findSignInOutForEmployeeShift(schId, posId);
		Date breakEndTime = new Date();
		signInOut.setBreakEndTime(breakEndTime);
		getSignInOutService().updateBreakSignOut(signInOut);
		replaceSignInOutForEmployee(schId, posId, signInOut,false);
	}
	
	public void submitRemarks(ActionEvent actionEvent) {  
		Integer empId = Integer.parseInt(FacesUtils.getRequestParameter("empid"));
		Integer posId = Integer.parseInt(FacesUtils.getRequestParameter("posid"));
		SignInOut signInOut = findSignInOutForEmployeeShift(empId, posId);
		getSignInOutService().updateRemarks(signInOut);
		//replaceSignInOutForEmployee(empId, sftId, signInOut);
    } 
	public void replaceSignInOutForEmployee(Integer schId, Integer posId,SignInOut signInOut,boolean calculateTime){
		boolean empFound = false;
		for(Position position : scheduledPositions){
			if(posId.intValue() == position.getId().intValue()){
				List<Employee> allScheduledEmployees = position.getScheduledEmployees();
				for(Employee employee : allScheduledEmployees){
					Integer scheduleId = employee.getScheduleId(); //comparing employee id wont help as one employee may be scheduled for many positions
					if(schId.intValue()==scheduleId.intValue()){
						employee.setSignInOut(signInOut); 
						if(calculateTime){
							int shiftHrs=0,shiftMins=0,breakHrs=0,breakMins=0;
							if(signInOut.getStartTime()!=null && signInOut.getEndTime()!=null){
								if(DateUtil.compareTimeOnly(signInOut.getStartTime(),signInOut.getEndTime())<=0){
									shiftHrs = DateUtil.getHours(signInOut.getStartTime(),signInOut.getEndTime());
									shiftMins = DateUtil.getMinutes(signInOut.getStartTime(),signInOut.getEndTime());
								}
								else{// calculation needs to be done properly
									shiftHrs = 23 - (DateUtil.getHours(signInOut.getEndTime(),signInOut.getStartTime()));
									shiftMins = 60-(DateUtil.getHours(signInOut.getEndTime(),signInOut.getStartTime()));
								}
							}
							if(signInOut.getBreakStartTime()!=null && signInOut.getBreakEndTime()!=null){
								if(DateUtil.compareTimeOnly(signInOut.getBreakStartTime(),signInOut.getBreakEndTime())<=0){
									breakHrs = DateUtil.getHours(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
									breakMins = DateUtil.getMinutes(signInOut.getBreakStartTime(),signInOut.getBreakEndTime());
								}
								else{
									breakHrs = 24 - DateUtil.getHours(signInOut.getBreakEndTime(),signInOut.getBreakStartTime());
									breakMins = 60 - DateUtil.getMinutes(signInOut.getBreakEndTime(),signInOut.getBreakStartTime());
								}
							}
							//int shiftHrs = DateUtil.getHours(signInOut.getStartTime(),signInOut.getEndTime());
							//int shiftMins = DateUtil.getMinutes(signInOut.getStartTime(),signInOut.getEndTime());
							employee.setHours(shiftHrs);
							employee.setMinutes(shiftMins);
							employee.setBreakHours(breakHrs);
							employee.setBreakMinutes(breakMins);
						}
						empFound = true;
					
					}
					
				}
					
			}	
		}
		
	}
	
	public SignInOut findSignInOutForEmployeeShift(Integer schId, Integer posId){
		for(Position position : scheduledPositions){
			if(posId.intValue() == position.getId().intValue()){
				List<Employee> allScheduledEmployees = position.getScheduledEmployees();
				for(Employee employee : allScheduledEmployees){
					if(schId.intValue()==employee.getScheduleId().intValue()){
						SignInOut signInOut = employee.getSignInOut(); 
						return signInOut;
					}
				}
					
			}
		}
		
		return null;
	}
	public Event getSignInOutEvent() {
		return signInOutEvent;
	}
	public void setSignInOutEvent(Event signInOutEvent) {
		this.signInOutEvent = signInOutEvent;
	}
	
	public List<Event> getPublishedEventList() {
		return publishedEventList;
	}
	public void setPublishedEventList(List<Event> publishedEventList) {
		this.publishedEventList = publishedEventList;
	}
	public List<Position> getScheduledPositions() {
		return scheduledPositions;
	}
	public void setScheduledPositions(List<Position> scheduledPositions) {
		this.scheduledPositions = scheduledPositions;
	}
	
	
	
}
