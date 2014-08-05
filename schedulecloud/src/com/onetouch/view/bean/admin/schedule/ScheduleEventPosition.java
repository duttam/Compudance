package com.onetouch.view.bean.admin.schedule;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.expression.impl.PreviousExpressionResolver;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.util.Constants;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.FormatType;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.event.EventPositionPDFExporter;
import com.onetouch.view.bean.admin.event.EventSupport;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.admin.report.EventPositionToExcel;

import com.onetouch.view.bean.util.DateBean;
import com.onetouch.view.bean.util.PopulateEmailAttributes;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.comparator.EmployeeNameComparator;
import com.onetouch.view.context.TenantContextUtil;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.EmployeeUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.PageXofY;

@ManagedBean(name="scheduleEventPosition")
@ViewScoped
public class ScheduleEventPosition extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EventPosition scheduledEventPosition;
	private Position scheduledPosition;
	private List<Employee> allEmployees; 
	
	private List<Employee> notifyAndAcceptedEmployees;//ALL EMPLOYEES WHO HAVE NOTIFIED AND ACCEPTED 
	private List<Employee> prevSelectedEmployees; // collection that holds all previously selected employees
	private List<Employee> deletePrevNotifiedEmployees; // collection that holds all previously selected employees which needs to be deleted because of time change
	private List<Schedule> notifiedEventPositions;
	private List<Availability> viewEmployeeAvailability;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	@ManagedProperty(value="#{eventSupport}")
	private EventSupport eventSupport;
	private Tenant tenant;
	private Event event;
	private List<Schedule> saveSchedules;
	private List<Schedule> updateSchedules;
	private List<Schedule> deleteSchedules;
	private String editConfirmMessage;
	private String viewId;
	private String operation;
	
	private List<Employee> offeredEmployees;
	@PostConstruct
	public void init(){
		
		viewId = FacesUtils.getRequestParameter("viewId");
		operation = FacesUtils.getRequestParameter("operation");
		tenant = tenantContext.getTenant();
		event = eventSupport.getEvent();//getEventService().getEvent(eventId, tenant,regionBean.getSelectedRegion());
		DateBean dateBean = (DateBean)FacesUtils.getManagedBean("dateBean");
		dateBean.createShiftDateAndTime(event.getStartDate(),event.getEndDate());
		if(operation.equalsIgnoreCase("add")){
			this.scheduledEventPosition = new EventPosition(this.event, this.tenant);
			scheduledEventPosition.setPosition(new Position(this.tenant));
		}else{
			scheduledEventPosition = eventSupport.getScheduleEventPosition();
			scheduledPosition = scheduledEventPosition.getPosition();// mark position
			
			List<Employee> pos_scheduledEmps = getScheduleService().getAllScheduledEmployeeByEventPosition(scheduledEventPosition,tenant); // get scheduled employees for position
			//allEmployees = getScheduleService().getTempAvailableEmployeesByEventPosition(eventPosition,event,tenant,regionBean.getSelectedRegion());// get all available employees for this position/shift
			if(operation.equalsIgnoreCase("override")){
				Integer locationId = event.getLocation().getId();
				
				allEmployees = getEmployeeService().getAllOverrideEmployeeByPosition(event, scheduledEventPosition, regionBean.getSelectedRegion(), tenant);
			}
			else
				allEmployees = getScheduleService().getTempAvailableEmployeesByEventPosition(scheduledEventPosition,event,tenant,regionBean.getSelectedRegion());// get all available employees for this position/shift
			
			
			// check if available employees is 0 or this position is not assigned to any employee
			if(allEmployees.size()<1){
				List<Employee> assignedEmps = getEmployeeService().getEmployeeByEventPosition(scheduledEventPosition, event, tenant,regionBean.getSelectedRegion()) ;
				if(assignedEmps.size()<1)
					scheduledPosition.setNoEmpAssign(true);				
			}
			
			
			notifyAndAcceptedEmployees = new ArrayList<Employee>();
			if(operation.equalsIgnoreCase("offer")){
				// only show notified employees
				offeredEmployees = getScheduleService().getAllOfferedEmployeesByEventPosition(scheduledEventPosition,tenant);
				for(Employee employee : pos_scheduledEmps){
					Schedule schedule = employee.getSchedule();
					if(schedule!=null && schedule.getSchedulestatus()== 1)
						notifyAndAcceptedEmployees.add(employee);
					if(allEmployees.contains(employee)){
						if(schedule.getOverrideTime()!=null || schedule.getSchedulestatus()==2)
							allEmployees.remove(employee);
					}
					allEmployees.removeAll(offeredEmployees);
				}
				if(scheduledEventPosition.getOfferCount()==0){
					Integer offerCnt = scheduledEventPosition.getReqdNumber() - scheduledEventPosition.getPosition().getScheduledAcceptedCount();
					scheduledEventPosition.setOfferCount(offerCnt);
				}
					
			}else{
				for(Employee employee : pos_scheduledEmps){
					Schedule schedule = employee.getSchedule();
					if(schedule!=null && schedule.getSchedulestatus()<= 2)
						notifyAndAcceptedEmployees.add(employee);
					if(!allEmployees.contains(employee)){
						if(schedule.getOverrideTime()!=null || schedule.getSchedulestatus()==2)
							allEmployees.add(employee);
					}
				}
			}
			prevSelectedEmployees = notifyAndAcceptedEmployees;
			Date oldStartTime = new Date(scheduledEventPosition.getStartTime().getTime());
			Date oldEndTime = new Date(scheduledEventPosition.getEndTime().getTime());
			scheduledEventPosition.setOldStartTime(oldStartTime);
			scheduledEventPosition.setOldEndTime(oldEndTime);
		}
	}
	/**
	 * 
	 */
	public void showNewEventPositionSummary(ActionEvent actionEvent){
		this.editConfirmMessage = "Please verify your changes before submmitting, Are you sure?";
		String websiteUrl = getApplicationData().getServer_url();
		Integer empCnt = scheduledEventPosition.getReqdNumber();
		saveSchedules = new ArrayList<Schedule>();
		Position position = scheduledEventPosition.getPosition();
		//List<Employee> newScheduledEmployees = position.getScheduledEmployees();
		if(empCnt!=null && empCnt.intValue()>0){
			
			scheduledEventPosition.setCompanyId(tenant.getId());
			scheduledEventPosition.setDisplayOrder(0);
			scheduledEventPosition.setActive(true);
			scheduledEventPosition.setRegion(regionBean.getSelectedRegion());
			for(Employee employee : notifyAndAcceptedEmployees){
					Schedule schedule = new Schedule();
					schedule.setPosition(position);
					schedule.setEmployee(employee);
					schedule.setTenant(tenant);
					schedule.setActive(true);
					schedule.setSchedulestatus(1);
					Date notifiedDate = new Date();
					
					schedule.setNotifiedtime(notifiedDate);
					if(DateUtil.dateRange(new Date(),new Date(event.getStartDate().getTime()+event.getStartTime().getTime()))+1<=3){
						Date expDate = DateUtil.getDateAndTime(event.getStartDate(),DateUtil.getDate("11:59 PM","hh:mm a"));
						schedule.setExpiretime(expDate);
					}
					else{	
						Date expDate = DateUtil.getDateAndTime(DateUtil.incrementByDay(new Date(),3),DateUtil.getDate("11:59 PM","hh:mm a"));
						schedule.setExpiretime(expDate);
					}
					
					Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, position);//populateEmailAttributes(event,customEventPosition,employee,position);
					schedule.setEmailAttributes(attributes);
					saveSchedules.add(schedule);
				
			}
		}else{
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Verify Employee Count","Must be greater than 0"));
			
		}
	}
	/**
	 * Add new position to an event scheduled previously
	 * @return redirect url to admin home page
	 */
	public String addNewEventPosition(){
			
			saveSchedules =	getScheduleService().scheduleNewEventPosition(scheduledEventPosition,saveSchedules);
			this.sendEmail(saveSchedules);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesUtils.addInfoMessage("Event Position saved Successfully, Email will be sent shortly ");
			
			return "scheduleDetail?faces-redirect=true&eventId="+event.getId();
		
		
	}
	/**
	 * filter employee list when add new position button is clicked
	 * @param ajaxBehaviorEvent
	 */
	public void filterEmployeeList(AjaxBehaviorEvent ajaxBehaviorEvent){
		if(scheduledEventPosition.getPosition()!=null){
			Date startTime = scheduledEventPosition.getStartTime();
			Date endTime = scheduledEventPosition.getEndTime();
			
			if(startTime!=null ){
				if(endTime!=null){
					scheduledEventPosition.setStartDate(startTime);
					scheduledEventPosition.setEndDate(endTime);
					List<Employee> availableEmployees = getScheduleService().getTempAvailableEmployeesByEventPosition(scheduledEventPosition, event, tenant,regionBean.getSelectedRegion()); //get all employees that are not notified and accepted 
					//scheduledEventPosition.getPosition().setEmployees(availableEmployees);
					allEmployees = availableEmployees;
					ScheduleEmployeeSort employeeSortBean = (ScheduleEmployeeSort)FacesUtils.getManagedBean("scheduleEmployeeSort");
					if(tenantContext.getTenant().isSortByRankAndHiredate()){
						employeeSortBean.setSelectedSortOption("default");
					}
					else{
						employeeSortBean.setSelectedSortOption("name");
					}
					allEmployees = employeeSortBean.sortEmployee(allEmployees);
				}else{
					
				}
			}else{
				
			}
		}
	}
	
	/**
	 * filter edit employee list when start/end time is clicked
	 * @param ajaxBehaviorEvent
	 */
	public void filterEditEmployeeList(AjaxBehaviorEvent ajaxBehaviorEvent){
		if(scheduledEventPosition!=null && scheduledEventPosition.getPosition()!=null){
			Date startTime = scheduledEventPosition.getStartTime();
			Date endTime = scheduledEventPosition.getEndTime();
			deletePrevNotifiedEmployees = new ArrayList<Employee>();
			if(startTime!=null ){
				if(endTime!=null){
					//List<Employee> pos_scheduledEmps = getScheduleService().getAllScheduledEmployeeByEventPosition(scheduledEventPosition,tenant); // get scheduled employees for position
					
					allEmployees = getScheduleService().getTempAvailableEmployeesByEventPosition(scheduledEventPosition,event,tenant,regionBean.getSelectedRegion());// get all available employees for this position/shift
					prevSelectedEmployees = notifyAndAcceptedEmployees;
					Iterator<Employee> notifiedEmpIterator =  notifyAndAcceptedEmployees.iterator();
					while(notifiedEmpIterator.hasNext()){
						Employee employee = notifiedEmpIterator.next();
						if(!allEmployees.contains(employee)){
							deletePrevNotifiedEmployees.add(employee);
							notifiedEmpIterator.remove();
						}
					}
					ScheduleEmployeeSort employeeSortBean = (ScheduleEmployeeSort)FacesUtils.getManagedBean("scheduleEmployeeSort");
					if(tenantContext.getTenant().isSortByRankAndHiredate()){
						employeeSortBean.setSelectedSortOption("default");
					}
					else{
						employeeSortBean.setSelectedSortOption("name");
					}
					allEmployees = employeeSortBean.sortEmployee(allEmployees);
					
					
				}else{
					
				}
			}else{
				
			}
		}
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
	/**
	 * Display Event position details when user hover over the icon
	 */
	public void displayEmployeeAvailability(){
		FacesContext context = FacesContext.getCurrentInstance();
	    Map map = context.getExternalContext().getRequestParameterMap();
	    String empId = (String) map.get("empid");
		Integer employeeId = Integer.parseInt(empId);
		
		for(Employee employee : allEmployees){
			if(employee.getId().intValue()==employeeId.intValue()){
				viewEmployeeAvailability = getEmployeeAvailability().getAllAvailabilityByDate(tenant, employee, event.getStartDate());//employee.getAllAvailability();
			}
		}
		//Employee employee = employeeMap.get(employeeId);
		//viewEmployeeAvailability = employee.getAllAvailability();
		RequestContext.getCurrentInstance().update("availdialog");
	}
	/**
	 * Display pop up showing the status of all the saved,updated,deleted schedules  
	 * 
	 */
	public void showEditSummary(ActionEvent actionEvent){
		this.editConfirmMessage = "Please verify your changes before submmitting, Are you sure?";
		
		saveSchedules = new ArrayList<Schedule>();
		updateSchedules = new ArrayList<Schedule>();
		deleteSchedules = new ArrayList<Schedule>();
		String websiteUrl = getApplicationData().getServer_url();
		Set<Employee> notifyAndAcceptedEmployeesSet = new HashSet<Employee>(notifyAndAcceptedEmployees);
		Set<Employee> prevSelectedEmployeesSet = new HashSet<Employee>(prevSelectedEmployees);
			//for(Employee employee : notifyAndAcceptedEmployees){
				//if(!prevSelectedEmployees.contains(employee)){ // not already scheduled , add to saveemployee list
			for(Employee employee : notifyAndAcceptedEmployeesSet){
				if(!prevSelectedEmployeesSet.contains(employee)){ // not already scheduled , add to saveemployee list		
					Schedule schedule = new Schedule();
					schedule.setEventPositionId(scheduledEventPosition.getId());
					schedule.setEmployee(employee);
					schedule.setPosition(scheduledPosition);
					schedule.setTenant(tenant);
					schedule.setActive(true);
					
					schedule.setSchedulestatus(1);
					Date notifiedDate = new Date();
					schedule.setNotifiedtime(notifiedDate);
					schedule.setExpiretime(getEventExpireTime(event));
					/*if(DateUtil.dateRange(new Date(),new Date(event.getStartDate().getTime()+event.getStartTime().getTime()))+1<=3){
						Date expDate = DateUtil.getDateAndTime(event.getStartDate(),DateUtil.getDate("11:59 PM","hh:mm a"));
						schedule.setExpiretime(expDate);
					}
					else{	
						Date expDate = DateUtil.getDateAndTime(DateUtil.incrementByDay(new Date(),3),DateUtil.getDate("11:59 PM","hh:mm a"));
						schedule.setExpiretime(expDate);
					}*/
					if(operation.equalsIgnoreCase("offer")){
						Map<Object,Object> attributes = PopulateEmailAttributes.populateOfferEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
						
						schedule.setEmailAttributes(attributes);
					}else{
						Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
						schedule.setEmailAttributes(attributes);
					}
					saveSchedules.add(schedule);
				}
			}
			
			for(Employee employee : prevSelectedEmployeesSet){
				if(!notifyAndAcceptedEmployeesSet.contains(employee)){ // unchecked employee, add to deleteemployee list
					Schedule schedule = new Schedule();
					Integer schId = employee.getSchedule().getId();
					schedule.setId(schId);
					schedule.setEventPositionId(scheduledEventPosition.getId());
					schedule.setEmployee(employee);
					schedule.setPosition(scheduledPosition);
					schedule.setTenant(tenant);
					schedule.setActive(false);
					Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
					attributes.put("emailsubject","YOU ARE NO LONGER SCHEDULED FOR " + tenant.getName()+", "+event.getLocation().getName()+", "+event.getName());
					attributes.put("priority", "HIGH");
					attributes.put("scheduleId", schId);
					schedule.setEmailAttributes(attributes);
					deleteSchedules.add(schedule);
				}else{
					if(scheduledEventPosition.isShiftTimeChanged()){
						Schedule schedule = new Schedule();
						schedule.setActive(true);
						Integer schId = employee.getSchedule().getId();
						schedule.setId(schId);
						schedule.setEventPositionId(scheduledEventPosition.getId());
						schedule.setEmployee(employee);
						schedule.setPosition(scheduledPosition);
						schedule.setTenant(tenant);
						schedule.setSchedulestatus(1);
						Date notifiedDate = new Date();
						schedule.setNotifiedtime(notifiedDate);
						if(DateUtil.dateRange(new Date(),new Date(event.getStartDate().getTime()+event.getStartTime().getTime()))+1<=3){
							Date expDate = new Date(event.getStartDate().getTime()+event.getStartTime().getTime());
							schedule.setExpiretime(expDate);
						}
						else{	
							Date expDate = new Date(event.getStartDate().getTime()+event.getStartTime().getTime()+tenant.getEmailExpLmt());
							schedule.setExpiretime(expDate);
						}
						Map<Object,Object> attributes = PopulateEmailAttributes.populateUpdatedEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
						attributes.put("scheduleId", schId);
						schedule.setEmailAttributes(attributes);
						updateSchedules.add(schedule);
						
						
					}
					
				}
			}
			// remove previously accepted employees when edit button is clicked and admin changed the time only
			if(operation.equalsIgnoreCase("edit")){
				if(deletePrevNotifiedEmployees!=null && deletePrevNotifiedEmployees.size()>0){
					for(Employee employee:  deletePrevNotifiedEmployees){
						Schedule schedule = new Schedule();
						Integer schId = employee.getSchedule().getId();
						schedule.setId(schId);
						schedule.setEventPositionId(scheduledEventPosition.getId());
						schedule.setEmployee(employee);
						schedule.setPosition(scheduledPosition);
						schedule.setTenant(tenant);
						schedule.setActive(false);
						Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
						attributes.put("emailsubject","YOU ARE NO LONGER SCHEDULED FOR " + tenant.getName()+", "+event.getLocation().getName()+", "+event.getName());
						attributes.put("priority", "HIGH");
						attributes.put("scheduleId", schId);
						schedule.setEmailAttributes(attributes);
						deleteSchedules.add(schedule);
					}
					
				}
			}
			
	}
	
	
	
	/**
	 * update event schedules on click edit
	 * @return scheduleDetail page
	 */
	public String updateEmployees(){
		
		saveSchedules = getScheduleService().updateSchedules(event, scheduledEventPosition, saveSchedules,deleteSchedules,updateSchedules);
		this.sendEmail(saveSchedules);
		this.sendUpdatedEmail(updateSchedules);
		this.sendScheduleDeleteEmail(deleteSchedules);
		scheduledPosition.setEmployees(notifyAndAcceptedEmployees);
		saveSchedules = null;
		updateSchedules = null;
		deleteSchedules = null;
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("Schedule Updated Successfully, Email will be sent shortly ");
		if(viewId!=null && viewId.equalsIgnoreCase("roster"))
		{
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			String date = dateformat.format(event.getStartDate());
			
			return "/ui/admin/event/roster?faces-redirect=true&includeViewParams=true&startDate="+date+"&endDate="+date;
		}
		else
			return "/ui/admin/schedule/scheduleDetail?faces-redirect=true&eventId="+event.getId();
		
		
	}
	/**
	 * offered employees
	 */
	
	public String showOfferedSummary(String selection){
		
		saveSchedules = new ArrayList<Schedule>();
		deleteSchedules = new ArrayList<Schedule>();
		Set<Employee> prevSelectedEmployeesSet = new HashSet<Employee>(prevSelectedEmployees);
		String websiteUrl = getApplicationData().getServer_url();
		Set<Employee> notifyAndAcceptedEmployeesSet = null;
		if(selection.equalsIgnoreCase("selected"))
			notifyAndAcceptedEmployeesSet = new HashSet<Employee>(notifyAndAcceptedEmployees);
		else
			notifyAndAcceptedEmployeesSet = new HashSet<Employee>(allEmployees);
		for(Employee employee : notifyAndAcceptedEmployeesSet){
			Schedule schedule = new Schedule();
			schedule.setEventPositionId(scheduledEventPosition.getId());
			schedule.setEmployee(employee);
			schedule.setPosition(scheduledPosition);
			schedule.setTenant(tenant);
			schedule.setActive(true);
			schedule.setSchedulestatus(1);
			Date notifiedDate = new Date();
			schedule.setNotifiedtime(notifiedDate);
			schedule.setExpiretime(scheduledEventPosition.getEndTime());
			Map<Object,Object> attributes = PopulateEmailAttributes.populateOfferEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
			schedule.setEmailAttributes(attributes);
			saveSchedules.add(schedule);
			
		}
		for(Employee employee : prevSelectedEmployeesSet){
			 // delete notified employee
				Schedule schedule = new Schedule();
				Integer schId = employee.getSchedule().getId();
				schedule.setId(schId);
				schedule.setEventPositionId(scheduledEventPosition.getId());
				schedule.setEmployee(employee);
				schedule.setPosition(scheduledPosition);
				schedule.setTenant(tenant);
				schedule.setActive(false);
				Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
				attributes.put("emailsubject","YOU ARE NO LONGER SCHEDULED FOR " + tenant.getName()+", "+event.getLocation().getName()+", "+event.getName());
				attributes.put("priority", "HIGH");
				attributes.put("scheduleId", schId);
				schedule.setEmailAttributes(attributes);
				deleteSchedules.add(schedule);
			}
		
		return null;
	}
	public String OfferSchedule(){
		Integer offerCount = scheduledEventPosition.getOfferCount();
		// reqd employee have not update, ask frank if we need to update the no of required employees
		
		if(offerCount>0){
			Integer updateReqdCnt = offerCount+scheduledEventPosition.getPosition().getScheduledAcceptedCount();
			scheduledEventPosition.setReqdNumber(updateReqdCnt);
			scheduledEventPosition.setOfferCount(offerCount);
			saveSchedules =  getScheduleService().saveOfferSchedules(event, scheduledEventPosition, saveSchedules, deleteSchedules);
			this.sendOfferEmail(saveSchedules);
			
			FacesUtils.addInfoMessage("Offer Schedules Updated Successfully, Email will be sent shortly ");
		}else
			FacesUtils.addErrorMessage("Offer Schedules Error, All positions have already been filled up ");
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "scheduleDetail?faces-redirect=true&eventId="+event.getId();
	} 
	private void sendOfferEmail(List<Schedule> overrideSchedules) {
		// TODO Auto-generated method stub
		int offerCount = this.scheduledEventPosition.getReqdNumber() - this.scheduledPosition.getScheduledAcceptedCount();
		for(Schedule schedule: overrideSchedules){
			Map<Object,Object> attributes = schedule.getEmailAttributes();
			attributes.put("offerCount",offerCount);
			attributes.put("scheduleId",schedule.getId());
			if(attributes.get("eventPosId")==null)
				attributes.put("eventPosId", schedule.getEventPositionId());
			Employee employee = schedule.getEmployee();
			List<String> receipients = new ArrayList<String>();
			receipients.add(employee.getEmail());
			//getSender().scheduleEmployee(scheduleMessage, attributes, receipients,tenant.getEmailSender());
			getSheduleEmployeeSender().scheduleEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(), 
					tenant.getEmailSender(), "offer schedule");
		}
	}
	/**
	 *  show summary for override position
	 */
	public void showOverrideSummary(ActionEvent actionEvent){
		this.editConfirmMessage = "Please verify your changes before submmitting, Are you sure?";
		
		saveSchedules = new ArrayList<Schedule>();
		updateSchedules = new ArrayList<Schedule>();
		deleteSchedules = new ArrayList<Schedule>();
		String websiteUrl = getApplicationData().getServer_url();
		Set<Employee> notifyAndAcceptedEmployeesSet = new HashSet<Employee>(notifyAndAcceptedEmployees);
		Set<Employee> prevSelectedEmployeesSet = new HashSet<Employee>(prevSelectedEmployees);
			
			for(Employee employee : notifyAndAcceptedEmployeesSet){
				if(!prevSelectedEmployeesSet.contains(employee)){ // not already scheduled , add to saveemployee list		
					Schedule schedule = new Schedule();
					schedule.setEventPositionId(scheduledEventPosition.getId());
					schedule.setEmployee(employee);
					schedule.setPosition(scheduledPosition);
					schedule.setTenant(tenant);
					schedule.setActive(true);
					
					schedule.setSchedulestatus(1);
					Date notifiedDate = new Date();
					schedule.setNotifiedtime(notifiedDate);
					schedule.setExpiretime(getEventExpireTime(event));
					saveSchedules.add(schedule);
				}
			}
			
	}
	/**
	 * 
	 * @param actionEvent
	 */
	public String OverrideSchedule(){
		List<Schedule> overrideSchedules = new ArrayList<Schedule>();
		for(Schedule schedule : saveSchedules){
							
				
				schedule.setSchedulestatus(2);
				
				schedule.setOverrideTime(new Date());
				overrideSchedules.add(schedule);
			
		}
		
		getScheduleService().saveOverrideSchedules(event, scheduledEventPosition, overrideSchedules);
		return "/ui/admin/schedule/scheduleDetail?faces-redirect=true&eventId="+event.getId();
	} 
	
	/**
	 * send emails to saved schedule 
	 * @param schedules
	 */
	private void sendEmail(List<Schedule> schedules){
		for(Schedule schedule: schedules){
			Map<Object,Object> attributes = schedule.getEmailAttributes();
			attributes.put("scheduleId",schedule.getId());
			if(attributes.get("eventPosId")==null)
				attributes.put("eventPosId", schedule.getEventPositionId());
			Employee employee = schedule.getEmployee();
			List<String> receipients = new ArrayList<String>();
			receipients.add(employee.getEmail());
			getSheduleEmployeeSender().scheduleEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(),tenant.getEmailSender(), "notify employee");
		}
	}
	/**
	 * send emails to updated schedule 
	 * @param schedules
	 */
	private void sendUpdatedEmail(List<Schedule> schedules){
		for(Schedule schedule: schedules){
			Map<Object,Object> attributes = schedule.getEmailAttributes();
			attributes.put("scheduleId",schedule.getId());
			if(attributes.get("eventPosId")==null)
				attributes.put("eventPosId", schedule.getEventPositionId());
			Employee employee = schedule.getEmployee();
			List<String> receipients = new ArrayList<String>();
			receipients.add(employee.getEmail());
			
			getSheduleEmployeeSender().scheduleEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(), 
					tenant.getEmailSender(), "resend schedule");
		}
	}
	/**
	 * send emails to deleted schedule 
	 * @param schedules
	 */
	private void sendScheduleDeleteEmail(List<Schedule> schedules){
		for(Schedule schedule: schedules){
			Map<Object,Object> attributes = schedule.getEmailAttributes();
			attributes.put("scheduleId",schedule.getId());
			Employee employee = schedule.getEmployee();
			List<String> receipients = new ArrayList<String>();
			receipients.add(employee.getEmail());
			getSheduleEmployeeSender().scheduleEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(), 
					tenant.getEmailSender(), "delete schedule");
			
		}
	}
	public void showDeleteSummary(ActionEvent actionEvent){
		String websiteUrl = getApplicationData().getServer_url();
		deleteSchedules = new ArrayList<Schedule>();
		for(Employee employee : prevSelectedEmployees){
			Schedule schedule = new Schedule();
			schedule.setId(employee.getSchedule().getId());
			schedule.setEventPositionId(scheduledEventPosition.getId());
			schedule.setEmployee(employee);
			schedule.setActive(false);
			schedule.setTenant(tenant);
			Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
			attributes.put("emailsubject","YOU ARE NO LONGER SCHEDULED FOR " + tenant.getName()+", "+event.getLocation().getName()+", "+event.getName());
			attributes.put("priority", "HIGH");
			schedule.setEmailAttributes(attributes);
			deleteSchedules.add(schedule);
		}
	}
	public String deleteEventPosition(){
		getScheduleService().deleteScheduleByEventPosition(this.scheduledEventPosition,deleteSchedules,tenant);
		this.sendScheduleDeleteEmail(deleteSchedules);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("EventPosition deleted Successfully, Email will be sent shortly ");
		
		return "/ui/admin/schedule/scheduleDetail?faces-redirect=true&eventId="+event.getId();
	}
	public EventPosition getScheduledEventPosition() {
		return scheduledEventPosition;
	}
	public void setScheduledEventPosition(EventPosition scheduledEventPosition) {
		this.scheduledEventPosition = scheduledEventPosition;
	}
	public Position getScheduledPosition() {
		return scheduledPosition;
	}
	public void setScheduledPosition(Position scheduledPosition) {
		this.scheduledPosition = scheduledPosition;
	}
	public List<Employee> getAllEmployees() {
		/*if(allEmployees!=null && allEmployees.size()>0)
			Collections.sort(allEmployees,new EmployeeNameComparator());*/
		return allEmployees;
	}
	public void setAllEmployees(List<Employee> allEmployees) {
		this.allEmployees = allEmployees;
	}
	public RegionBean getRegionBean() {
		return regionBean;
	}
	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
	public List<Employee> getNotifyAndAcceptedEmployees() {
		return notifyAndAcceptedEmployees;
	}
	public void setNotifyAndAcceptedEmployees(
			List<Employee> notifyAndAcceptedEmployees) {
		this.notifyAndAcceptedEmployees = notifyAndAcceptedEmployees;
	}
	
	public EventSupport getEventSupport() {
		return eventSupport;
	}
	public void setEventSupport(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}
	public List<Schedule> getNotifiedEventPositions() {
		return notifiedEventPositions;
	}
	public void setNotifiedEventPositions(List<Schedule> notifiedEventPositions) {
		this.notifiedEventPositions = notifiedEventPositions;
	}
	public List<Availability> getViewEmployeeAvailability() {
		return viewEmployeeAvailability;
	}
	public void setViewEmployeeAvailability(
			List<Availability> viewEmployeeAvailability) {
		this.viewEmployeeAvailability = viewEmployeeAvailability;
	}
	
	public List<Schedule> getSaveSchedules() {
		return saveSchedules;
	}
	public void setSaveSchedules(List<Schedule> saveSchedules) {
		this.saveSchedules = saveSchedules;
	}
	public List<Schedule> getDeleteSchedules() {
		return deleteSchedules;
	}
	public void setDeleteSchedules(List<Schedule> deleteSchedules) {
		this.deleteSchedules = deleteSchedules;
	}
	
	public List<Schedule> getUpdateSchedules() {
		return updateSchedules;
	}
	public void setUpdateSchedules(List<Schedule> updateSchedules) {
		this.updateSchedules = updateSchedules;
	}
	public String getEditConfirmMessage() {
		return editConfirmMessage;
	}
	public void setEditConfirmMessage(String editConfirmMessage) {
		this.editConfirmMessage = editConfirmMessage;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public List<Employee> getOfferedEmployees() {
		return offeredEmployees;
	}
	public void setOfferedEmployees(List<Employee> offeredEmployees) {
		this.offeredEmployees = offeredEmployees;
	}
	
}
