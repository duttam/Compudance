package com.onetouch.view.bean.admin.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.Location;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeePosition;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.ScheduleType;

import com.onetouch.model.domainobject.Schedule;



import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.admin.position.PositionController;
import com.onetouch.view.bean.admin.position.PositionSupport;
import com.onetouch.view.bean.util.PopulateEmailAttributes;

import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="scheduleEmployeeBean")
@ViewScoped
public class ScheduleEmployeeBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Tenant tenant;
	private Event event;
	private String action;
	//private List<Shift> selectedShifts;
	private List<EventPosition> allEventPositions;
	private List<Employee> allAvailableEmployee;
	private Map<Integer,List<Employee>> positionEmployeeMap;
    @ManagedProperty(value="#{eventSupport}")
    private EventSupport eventSupport;
	private Employee deletedEmployee;
	private Employee selectedEmployee;
	private List<Availability> viewEmployeeAvailability;
	private EventPosition scheduledEventPosition;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	private Integer eventId;
	private Integer total_EmpCnt = 0;
	private List<Schedule> notifiedEventPositions;
	public void init(){
		if (!FacesContext.getCurrentInstance().isPostback()) {


			if(total_EmpCnt>500){
				String contextRoot = FacesUtils.getServletContext().getContextPath();
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect( contextRoot  + "/ui/admin/schedule/scheduleDetail.jsf?eventId="+event.getId());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	@PostConstruct
	public void initEventController(){
		tenant = tenantContext.getTenant();
		action = eventSupport.getAction();
		event = eventSupport.getEvent();
	
		//loadImagesToDisk(allAvailableEmployee);
		
		allEventPositions = getEventService().getTempAllAvailableEmployeesByPosition(event, tenant,regionBean.getSelectedRegion());
		
		for(EventPosition eventPosition : allEventPositions){
			Position position = eventPosition.getPosition();
			total_EmpCnt = total_EmpCnt+position.getEmployees().size();
			
			
				// employee found for position 
				/*if(position!=null &&position.getEmployees().size()==0){
					//display message
				}else{
					List<Employee> employees = position.getEmployees();
					List<Employee> originialEmpList = this.getNewEmployeeList(employees);
					position.setOriginalEmployees(originialEmpList);
				}*/
			
			
			
		}
		
		//allAvailableEmployee = getEmployeeService().getAllEmployeeAvailability(tenant,event.getStartDate(),event.getEventRegion()); //replace with this later
		
		positionEmployeeMap = new HashMap<Integer, List<Employee>>();
	}
	public void loadImagesToDisk(List<Employee> allEmployee){
		//String path = FacesUtils.getServletContext().getRealPath("/images");
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
		File imageDir = new File(path+"/"+tenant.getCode());
		if (!imageDir.exists()) {
			if (imageDir.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		for(Employee employee : allEmployee){
			
				File file = new File(imageDir+"/"+employee.getImageName());
				OutputStream output = null;
				try {
					output = new FileOutputStream(file);
					IOUtils.copy(employee.getInputStream(), output);
				} catch (Exception e) {
					// TODO: handle exception
				}finally {
					IOUtils.closeQuietly(output);
				}
			
		}
	}
	private List<Employee> getNewEmployeeList(List<Employee> employees){
		List<Employee> employeeList = new ArrayList<Employee>();
		if(employees==null){
			for(Employee employee : allAvailableEmployee){
				//if(employee.isAvailable(shift, event)){ //replace with this to verify availability
					Employee tempemployee = new Employee();
					BeanUtils.copyProperties(employee, tempemployee);
					employeeList.add(tempemployee);
				//}
			}
		}else{
			for(Employee employee : employees){
				//if(employee.isAvailable(shift, event)){ //replace with this to verify availability
					Employee tempemployee = new Employee();
					BeanUtils.copyProperties(employee, tempemployee);
					employeeList.add(tempemployee);
				//}
			}
		}
		return employeeList;
	}
	public void selectedItemsChanged(ValueChangeEvent event) {
	    List<Employee> oldEmps = (event.getOldValue()==null) ? new ArrayList<Employee>() : (List<Employee>) event.getOldValue();
	    List<Employee> newEmps = (event.getNewValue()==null) ? new ArrayList<Employee>() : (List<Employee>) event.getNewValue();

	    //if(oldEmps.size()>newEmps.size()){
	    	for(Employee oldEmployee: oldEmps){
	    		if(!newEmps.contains(oldEmployee))
	    			deletedEmployee = oldEmployee;
	    	}
	    //}
	    //if(newEmps.size()>oldEmps.size()){
	    	for(Employee newEmployee: newEmps){
	    		if(!oldEmps.contains(newEmployee))
	    			selectedEmployee = newEmployee;
	    	}
	    //}
	    
	}

	public String editEventPositionNotes(Integer eventPositionId){
		for(EventPosition eventPosition : allEventPositions){
			if(eventPosition.getId().intValue()==eventPositionId.intValue()){
				scheduledEventPosition = eventPosition;// selected event position for editing
			}
		}
		
		RequestContext.getCurrentInstance().update("editNoteDialog");
		return null;
	}	
	public void saveEventPositionNotes(ActionEvent actionEvent){
		scheduledEventPosition.setRegion(regionBean.getSelectedRegion());
		scheduledEventPosition.setCompanyId(tenant.getId());
		scheduledEventPosition.setActive(true);
		getScheduleService().updateEventPositionNotes(scheduledEventPosition);
		
		RequestContext.getCurrentInstance().update("evntposid");
	}
	public void selectEmployeeEventPosition(){
		boolean employeeSelected = false;
		boolean employeeDeleted = false;
		Integer employeeId = null;
		Integer currentEventPosId = Integer.parseInt(FacesUtils.getRequestParameter("currentEvntPosId"));
		if(deletedEmployee!=null){
			employeeDeleted = true;
			employeeId = deletedEmployee.getId();
		}
		else if(selectedEmployee!=null){
			employeeSelected = true;
			employeeId = selectedEmployee.getId();
		}
		
		EventPosition selectedEventPosition = null;
		for(EventPosition eventPosition : allEventPositions){
			//Position position = eventPosition.getPosition();
			if(currentEventPosId.intValue()!=eventPosition.getId().intValue())
				continue;
			else{
				//mark selected position in the list
				selectedEventPosition =eventPosition ;
				
			}
				 
		}
		
		for(EventPosition eventPosition : allEventPositions){
			
			if(currentEventPosId.intValue()!=eventPosition.getId().intValue()){
				Position position = eventPosition.getPosition();
				List<Employee> employees = position.getEmployees();
				List<Employee> originalEmployees = position.getOriginalEmployees();
				if(employeeSelected && employees.contains(new Employee(employeeId))){
					if(isTimeOverlap(selectedEventPosition,eventPosition))
						employees.remove(new Employee(employeeId));
				}
				else if(employeeDeleted && (originalEmployees!=null && originalEmployees.contains(new Employee(employeeId)))){
					
					for(Employee tempEmployee: originalEmployees)
					{
						if(tempEmployee.getId().intValue()== employeeId.intValue()){
							
							Employee addEmployee = new Employee(employeeId);
							BeanUtils.copyProperties(tempEmployee, addEmployee, new String[]{"id"});
							if(isTimeOverlap(selectedEventPosition,eventPosition))
								employees.add(addEmployee);
							
						}
					
					}		
					
				}
					
					
			}
		}
		
		deletedEmployee = null;
		selectedEmployee = null;
		
	}
	private boolean isTimeOverlap(EventPosition selectedEventPosition, EventPosition eventPosition) {
		// TODO Auto-generated method stub
		
		int a = DateUtil.compareDateAndTime(selectedEventPosition.getStartDate(),selectedEventPosition.getStartTime(),eventPosition.getStartDate(), eventPosition.getStartTime());
		
		int b = DateUtil.compareDateAndTime(selectedEventPosition.getStartDate(),selectedEventPosition.getStartTime(),eventPosition.getEndDate(), eventPosition.getEndTime());
		if(a>=0 && b<=0)
			return true;
		int c = DateUtil.compareDateAndTime(selectedEventPosition.getEndDate(),selectedEventPosition.getEndTime(),eventPosition.getStartDate(), eventPosition.getStartTime());
		int d = DateUtil.compareDateAndTime(selectedEventPosition.getEndDate(),selectedEventPosition.getEndTime(),eventPosition.getEndDate(), eventPosition.getEndTime());
		if(c>=0 && d<=0)
			return true;
		if(a<0 && d>0)	// start time is smaller than prev start time and end time is greater than prev end time  
			return true;
		
		return false;
	}
	
	public String scheduleEmployees(){
		List<Schedule> scheduleList = new ArrayList<Schedule>();
		String websiteUrl = getApplicationData().getServer_url();
		try{
			for(EventPosition eventPosition : allEventPositions){
				
				Position position = eventPosition.getPosition();
				
				List<Employee> employees = position.getSelectedEmployees();
				if(employees.size()>eventPosition.getReqdNumber()){
					FacesUtils.addErrorMessage("Problem assigning employees to "+position.getName()+", required count is less than assigned no of employees");//put a message that required number is less
					return null;
				}else{
					for(Employee employee : employees){
						Schedule schedule = new Schedule();
						schedule.setEventPositionId(eventPosition.getId());
						schedule.setEmployee(employee);
						schedule.setTenant(tenant);
						schedule.setActive(true);
						schedule.setSchedulestatus(1);
						Date notifiedDate = new Date();
						schedule.setNotifiedtime(notifiedDate);
						schedule.setExpiretime(getEventExpireTime(event));
						
						Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, event, eventPosition, employee, position);//populateEmailAttributes(event,employee,position,eventPosition.getId());
						schedule.setEmailAttributes(attributes);
						scheduleList.add(schedule);
						
					}
				}
			}
			scheduleList = getScheduleService().saveSchedules(event,scheduleList);
			for(Schedule schedule: scheduleList){
				Map<Object,Object> attributes = schedule.getEmailAttributes();
				attributes.put("scheduleId",schedule.getId());
				Employee employee = schedule.getEmployee();
				List<String> receipients = new ArrayList<String>();
				receipients.add(employee.getEmail());
				getSheduleEmployeeSender().scheduleEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(), tenant.getEmailSender(), "notify employee");
				//getSender().scheduleEmployee(scheduleMessage, attributes, receipients,tenant.getEmailSender());
			}
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesUtils.addInfoMessage("Event published Successfully, Emails will be sent shortly");
			return "eventHome?faces-redirect=true";
		}catch (DataAccessException dae) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesUtils.addErrorMessage("Information couldn't be Saved, Data Error!");
			dae.printStackTrace();
		}catch (MailException me){
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			me.printStackTrace();
			FacesUtils.addErrorMessage("Information Saved, Schedule Email not sent!");
			//getSender().inviteEmployee(inviteMessage, attributes, receipients,tenant.getCompanyEmail());
		}catch(Exception exception){
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			exception.printStackTrace();
			FacesUtils.addErrorMessage("Error!");
		}
		return "eventHome?faces-redirect=true";
	}
	
	/**
	 * Display Event position details when user hover over "N"
	 */
	public void displayNotifiedEventPosition(){
		FacesContext context = FacesContext.getCurrentInstance();
	    Map map = context.getExternalContext().getRequestParameterMap();
	    String empId = (String) map.get("empid");
		Integer employeeId = Integer.parseInt(empId);
		notifiedEventPositions = getScheduleService().getAllNotifiedEventPositionsByDate(new Employee(employeeId),tenant,event.getStartDate());
		RequestContext.getCurrentInstance().update("notifieddialog");
	}
	public void displayEmployeeAvailability(){
		FacesContext context = FacesContext.getCurrentInstance();
	    Map map = context.getExternalContext().getRequestParameterMap();
	    String empId = (String) map.get("empid");
		Integer employeeId = Integer.parseInt(empId);
		for(EventPosition eventPosition : allEventPositions){
			Position position = eventPosition.getPosition();
			List <Employee> employees = position.getEmployees();
			for(Employee employee : employees){
				if(employee.getId().intValue()==employeeId.intValue()){
					viewEmployeeAvailability = employee.getAllAvailability();
				}
			}
		}
		RequestContext.getCurrentInstance().update("availdialog");
	}
	
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public EventSupport getEventSupport() {
		return eventSupport;
	}
	public void setEventSupport(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}
	public List<EventPosition> getAllEventPositions() {
		return allEventPositions;
	}
	public void setAllEventPositions(List<EventPosition> allEventPositions) {
		this.allEventPositions = allEventPositions;
	}
	public List<Employee> getAllAvailableEmployee() {
		return allAvailableEmployee;
	}
	public void setAllAvailableEmployee(List<Employee> allAvailableEmployee) {
		this.allAvailableEmployee = allAvailableEmployee;
	}
	public List<Availability> getViewEmployeeAvailability() {
		return viewEmployeeAvailability;
	}
	public EventPosition getScheduledEventPosition() {
		return scheduledEventPosition;
	}
	public void setScheduledEventPosition(EventPosition scheduledEventPosition) {
		this.scheduledEventPosition = scheduledEventPosition;
	}
	public RegionBean getRegionBean() {
		return regionBean;
	}
	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
	public Integer getEventId() {
		return eventId;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public List<Schedule> getNotifiedEventPositions() {
		return notifiedEventPositions;
	}
	public void setNotifiedEventPositions(List<Schedule> notifiedEventPositions) {
		this.notifiedEventPositions = notifiedEventPositions;
	}
	
	
	
}
