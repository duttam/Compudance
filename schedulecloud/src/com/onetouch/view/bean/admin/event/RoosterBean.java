package com.onetouch.view.bean.admin.event;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.util.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.Location;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Position;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.OneTouchReport;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Schedule;



import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.admin.position.PositionController;
import com.onetouch.view.bean.admin.position.PositionSupport;

import com.onetouch.view.bean.menu.MainMenuBean;
import com.onetouch.view.bean.util.DateBean;
import com.onetouch.view.bean.util.PopulateEmailAttributes;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.PageNumber;
import com.onetouch.view.util.PageXofY;

@ManagedBean(name="rosterBean")
@ViewScoped
public class RoosterBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Tenant tenant;
	private Event rosterEvent; // on load first event and event associated with the clicked tab
	
	private List<Event> eventList;
	private Location selectedLocation;
	private CustomUser customUser=null;
	private Date rosterDate;
	private List<Player> players;
	private List<EventPosition> allEventPositions;
	private Integer selectEventId;
	private Date rosterStartDate;
	private Date rosterEndDate;
	private Employee selectedEmployee;
	private List<Employee> employees;
	@ManagedProperty(value="#{mainMenuBean}")
    private MainMenuBean mainMenuBean;
	
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	
	private List<OneTouchReport> staffReqtListRpt;
	private Event transferEvent;
	private Employee transferEmployee;
	// add back functionality
	private String startDate;
	private String endDate;
	private Integer locationId;
	private Integer eventId;
	private EventPosition transferEventPosition; 
	private String activeTabIndex;
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}	
	
	public void init(){
		if (!FacesContext.getCurrentInstance().isPostback()) {
	        if(startDate!=null && endDate!=null){
	        	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	        	try {
					rosterStartDate = dateformat.parse(startDate);
					rosterEndDate = dateformat.parse(endDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	if(locationId!=null)
	        		selectedLocation = getLocationService().getLocation(locationId);
	        	eventList = getEventService().getAllPublishedEventsByDateRangeAndLocation(tenant,rosterStartDate,rosterEndDate,customUser,selectedLocation,regionBean.getSelectedRegion());
	        	
	        	if(eventList.size()>0){
	        		boolean found = false;
	        		for(Event event : eventList){
	        			
	        			List<EventPosition> eventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
		    			event.setAssignedEventPosition(eventPositions);
		    			if(eventId!=null && !found){
			    			if(eventId.intValue()==event.getId().intValue()){
			    				Integer index = eventList.indexOf(event);
			    				activeTabIndex = index.toString();
			    				found = true;
			    			}
		    			}
	        		}
	    			
	        	}
	        }
	    }
	}
	@PostConstruct
	public void initAddEventBean(){
		tenant = tenantContext.getTenant();
		
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
			
		}
		String date = FacesUtils.getRequestParameter("eventDate");
		if(date!=null){
			rosterStartDate = DateUtil.getDate(date, "yyyy-MM-dd");
			mainMenuBean.setActiveMenuIndex(1);
		}else
			rosterStartDate = new Date();
		rosterDate = rosterEndDate = rosterStartDate;
		String renderKitId = FacesContext.getCurrentInstance().getViewRoot().getRenderKitId();       
        if(renderKitId.equalsIgnoreCase("PRIMEFACES_MOBILE")){
        	if(tenant==null){ // this is mobile home page so if tenant is null here, then get it and set it in tenantcontext
				tenant = customUser.getTenant();
				tenantContext.setTenant(tenant);
			}
        	Employee employee = getEmployeeService().getEmplyeePositionById(customUser.getEmp_id(), tenant);
			SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
			supportBean.setCurrentLoggedEmployee(employee);
			eventList = getEventService().getAllPublishedEventsByDateRangeAndLocation(tenant, rosterDate,rosterDate, customUser,selectedLocation,regionBean.getSelectedRegion());//        getAllPublishedEventsByDateRange(tenant,rosterDate,rosterDate,customUser); 
        }else{
        	//staffReqtListRpt = this.getReport();
        }
        activeTabIndex = "";
        /*else
        	eventList = getEventService().getAllPublishedEventsByDateRangeAndLocation(tenant,rosterStartDate,rosterEndDate,customUser,selectedLocation,regionBean.getSelectedRegion());
		if(eventList.size()>0){
			Event event = eventList.get(0);
			rosterEvent = getEventService().getEventDetail(event.getId(), tenant);
			rosterEvent.setTenant(tenant);	
			allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
			
			rosterEvent.setAssignedEventPosition(allEventPositions);
			//eventList.remove(event);
			//eventList.add(0,rosterEvent);
		}*/
		
		
	}
	@PreDestroy
	public void destroy()
	{
		System.out.println("Roster Destroy");
	}
	public void expandAllTabs(ActionEvent actionEvent){
		StringBuilder out = new StringBuilder();
	    for(int i = 0; i < eventList.size(); i++){
	        out.append(i);
	        if(i != eventList.size()-1)
	           out.append(",");
	    }
	     activeTabIndex = out.toString();
	}
	
	public void collapseAllTabs(ActionEvent actionEvent){
		activeTabIndex = "";
	}

	public void onTabChange(TabChangeEvent changeevent) {  
		Event selectedEvent = (Event)changeevent.getData();
        rosterEvent = selectedEvent;//getEventService().getEventDetail(selectedEvent.getId(), tenant);
        rosterEvent.setTenant(tenant);	
		List<EventPosition> allEventPositions = getScheduleService().getScheduleByEventPosition(selectedEvent, tenant);
		
		rosterEvent.setAssignedEventPosition(allEventPositions);
		
    } 
	
	public void onTabClose(TabCloseEvent event) {  
		
    } 
	public void onEmployeeSelect(){
		transferEmployee.setTenant(tenant);
	}
	
	public String onSelectTransferEmployee(){
		List<EventPosition> allEventPositions = getScheduleService().getTransferredEventPositionsByEmployee(transferEmployee,rosterEvent, tenant);// getScheduleByEventPosition(transferEvent, tenant);
		rosterEvent.setAssignedEventPosition(allEventPositions);
		
		return null;
		
	}
	public void onSelectTransferEvent(){
		transferEvent.setTenant(tenant);	
		List<EventPosition> allEventPositions = getScheduleService().getTransferredEventPositionsByEmployee(transferEmployee,transferEvent, tenant);// getScheduleByEventPosition(transferEvent, tenant);
		transferEvent.setAssignedEventPosition(allEventPositions);
		RequestContext.getCurrentInstance().update("eventPositionList");
	}
	public void handleClose(CloseEvent event) {
        transferEvent = null;
        transferEmployee = null;
    }
	public String moveEmployee(){
		String websiteUrl = getApplicationData().getServer_url();
		Schedule schedule = new Schedule();
		schedule.setEventPositionId(transferEventPosition.getId());
		schedule.setEmployee(transferEmployee);
		schedule.setPosition(transferEventPosition.getPosition());
		schedule.setTenant(tenant);
		schedule.setActive(true);
		
		schedule.setSchedulestatus(1);
		Date notifiedDate = new Date();
		schedule.setNotifiedtime(notifiedDate);
		schedule.setExpiretime(getEventExpireTime(transferEvent));
		Map<Object,Object> attributes = PopulateEmailAttributes.populateMoveEmailAttributes(websiteUrl, tenant, transferEvent,rosterEvent, transferEventPosition,selectedEventPosition, transferEmployee, transferEventPosition.getPosition());
		schedule.setEmailAttributes(attributes);
		
		Schedule deleteSchedule = new Schedule();
		Integer schId = transferEmployee.getSchedule().getId();
		deleteSchedule.setId(schId);
		deleteSchedule.setEventPositionId(selectedEventPosition.getId());
		deleteSchedule.setEmployee(transferEmployee);
		deleteSchedule.setTenant(tenant);
		deleteSchedule.setActive(false);
		List<Schedule> deleteSchedules = new ArrayList<Schedule>();
		deleteSchedules.add(deleteSchedule);
		schedule = getScheduleService().reAssignEmployee(schedule,deleteSchedules);
		this.sendMoveEmail(schedule);
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("Employee moveed successfully, an Email will be send shortly");
		if(selectedLocation!=null)
          	return "roster.jsf?faces-redirect=true&startDate="+startDate+"&endDate="+endDate+"&locationId="+selectedLocation.getId()+"&eventId="+transferEvent.getId();
        else
           	return "roster.jsf?faces-redirect=true&startDate="+startDate+"&endDate="+endDate+"&eventId="+transferEvent.getId();
	}
	public void sendMoveEmail(Schedule schedule){
		Map<Object,Object> attributes = schedule.getEmailAttributes();
		attributes.put("scheduleId",schedule.getId());
		
		Employee employee = schedule.getEmployee();
		List<String> receipients = new ArrayList<String>();
		receipients.add(employee.getEmail());
		getSheduleEmployeeSender().scheduleEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(),tenant.getEmailSender(), "move schedule");
	}
	/**
	 * Company policy notes update and retrieve
	 * @param actionEvent
	 */
	public void showCompanyPolicy(ActionEvent actionEvent){
		
	}
	public void saveCompanyPolicyNotes(ActionEvent actionEvent){
		getTenantService().savePolicyNotes(tenant);
	}
	public String deleteEvent(){
		if(rosterEvent.getStatus().equalsIgnoreCase("active")){
			getEventService().deleteEventAndPositions(this.rosterEvent,this.tenant);
			
		}else if(rosterEvent.getStatus().equalsIgnoreCase("published")){
			
			List<Schedule> deletedSchedules = getEventService().deletePublishedEvent(this.rosterEvent,this.tenant);
			this.sendDeleteEmail(deletedSchedules);
			if(deletedSchedules!=null && deletedSchedules.size()>0){
				eventList = getEventService().getAllPublishedEventsByDateRangeAndLocation(tenant,rosterStartDate,rosterEndDate,customUser,selectedLocation,regionBean.getSelectedRegion());
				if(eventList.size()>0){
					Event event = eventList.get(0);
					rosterEvent = getEventService().getEventDetail(event.getId(), tenant);
					rosterEvent.setTenant(tenant);	
					List<EventPosition> allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
					
					rosterEvent.setAssignedEventPosition(allEventPositions);
					//eventList.remove(event);
					//eventList.add(0,rosterEvent);
				}
				
				FacesContext context = FacesContext.getCurrentInstance();
				context.getExternalContext().getFlash().setKeepMessages(true);
				FacesUtils.addInfoMessage("event deleted Successfully");
				
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
				context.getExternalContext().getFlash().setKeepMessages(true);
				FacesUtils.addInfoMessage("Couldn't delete event, employee already signed in for the event");
				
			}
		}else{
			
		}
		return null;
		
	}
	private void sendDeleteEmail(List<Schedule> schedules){
		String websiteUrl = getApplicationData().getServer_url();
		for(Schedule schedule: schedules){
			EventPosition scheduledEventPosition = schedule.getEventPosition();
			Position scheduledPosition = scheduledEventPosition.getPosition();
			Employee employee = schedule.getEmployee();
			Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, this.rosterEvent, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
			attributes.put("emailsubject","YOU ARE NO LONGER SCHEDULED FOR " + tenant.getName()+", "+this.rosterEvent.getLocation().getName()+", "+this.rosterEvent.getName());
			attributes.put("priority", "HIGH");
			List<String> receipients = new ArrayList<String>();
			receipients.add(employee.getEmail());
			getSender().deleteScheduleEmployee(scheduleMessage, attributes, receipients,tenant.getEmailSender());
		}
	}
	/*private void sendEmail(List<Schedule> schedules){
		for(Schedule schedule: schedules){
			Map<Object,Object> attributes = schedule.getEmailAttributes();
			Employee employee = schedule.getEmployee();
			List<String> receipients = new ArrayList<String>();
			receipients.add(employee.getEmail());
			getSender().scheduleEmployee(scheduleMessage, attributes, receipients,tenant.getCompanyEmail());
		}
	}*/
	public String filterRoster() {  
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    	String startDate = dateformat.format(rosterStartDate);
		String endDate = dateformat.format(rosterEndDate);
		if(selectedLocation!=null)
          	return "roster.jsf?faces-redirect=true&startDate="+startDate+"&endDate="+endDate+"&locationId="+selectedLocation.getId();
        else
           	return "roster.jsf?faces-redirect=true&startDate="+startDate+"&endDate="+endDate;
        /*eventList = getEventService().getAllPublishedEventsByDateRangeAndLocation(tenant,rosterStartDate,rosterEndDate,customUser,selectedLocation,regionBean.getSelectedRegion());
		if(eventList.size()>0){
			Event event = eventList.get(0);
			rosterEvent = getEventService().getEventDetail(event.getId(), tenant);
			rosterEvent.setTenant(tenant);	
			allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
			rosterEvent.setAssignedEventPosition(allEventPositions);
			
		}*/

    } 
	public List<Employee> completeEmployer(String query) { 
		List<Employee> suggestions = new ArrayList<Employee>(); 
		if(employees==null || employees.size()<1)
			employees = getEmployeeService().getAllEmployee(tenant, new String[]{"active"},regionBean.getSelectedRegion());
		for(Employee emp : employees) 
		{ 
			String fullName = emp.getFirstname().toLowerCase()+" "+emp.getLastname().toLowerCase(); 
			String reverseFullName = emp.getLastname().toLowerCase()+" "+emp.getFirstname().toLowerCase();
			String querySearch = query.trim().toLowerCase();
			boolean found = fullName.startsWith(querySearch) || reverseFullName.startsWith(querySearch) || emp.getId().toString().startsWith(querySearch);
			if(found)
				suggestions.add(emp); 
		} 
		return suggestions; 
	}
	public void filterEventRosterListener(SelectEvent selectEvent){
		List<Event> allEmpEvents = getEventService().getAllSchedulesByEmployee(rosterStartDate,rosterEndDate, selectedEmployee, tenant, regionBean.getSelectedRegion());
		eventList = allEmpEvents;
		
	}
	public void selectRosterStartDate(SelectEvent selectevent){
		rosterEndDate = rosterStartDate;
	}
	public void selectRosterEndDate(SelectEvent event){
		if(this.rosterEndDate!=null && this.rosterStartDate!=null){
			if(this.rosterEndDate.getTime()<this.rosterStartDate.getTime())
				FacesUtils.addErrorMessage("Enter a valid end date");
			else{
				
			}
		}
			
	}
	
	
	public String selectEvent(){
		rosterEvent = getEventService().getEventDetail(selectEventId, tenant);
        rosterEvent.setTenant(tenant);	
		allEventPositions = getScheduleService().getScheduleByEventPosition(rosterEvent, tenant);
		
		rosterEvent.setAssignedEventPosition(allEventPositions);
		return "pm:eventdetails";
	}
	// mobile date select
	public void handleDateSelect(SelectEvent selectevent) {  
		rosterDate = (Date)selectevent.getObject();
		if(rosterDate!=null){
			eventList = getEventService().getAllPublishedEventsByDateRangeAndLocation(tenant, rosterDate,rosterDate, customUser, selectedLocation, regionBean.getSelectedRegion());//(tenant,rosterDate,rosterDate,customUser);
			if(eventList.size()>0){
				Event event = eventList.get(0);
				rosterEvent = getEventService().getEventDetail(event.getId(), tenant);
				rosterEvent.setTenant(tenant);	
				allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
				rosterEvent.setAssignedEventPosition(allEventPositions);
				//eventList.remove(event);
				//eventList.add(0,rosterEvent);
			}
		}

    } 
	public String onScheduleEventSelect(Integer eid) {
		
		Integer index = eventList.indexOf(new Event(eid));
		rosterEvent = eventList.get(index);
		Date eventDate = rosterEvent.getStartDate();
		//Integer id = rosterEvent.getId();
		//id = Integer.parseInt(FacesUtils.getRequestParameter("eventid"));
		boolean b = DateUtil.isPreviousEvent(eventDate,new Date());
		if(b)
			return "/ui/admin/schedule/oldScheduleDetail.jsf?faces-redirect=true&eventId="+eid;
		else
			return "/ui/admin/schedule/scheduleDetail.jsf?faces-redirect=true&eventId="+eid;

		
    }
	private EventPosition selectedEventPosition;
	
	public EventPosition getSelectedEventPosition() {
		return selectedEventPosition;
	}
	public void setSelectedEventPosition(EventPosition selectedEventPosition) {
		this.selectedEventPosition = selectedEventPosition;
	}
	public String editEventPosition(){
		
		EventSupport eventSupport = (EventSupport)FacesUtils.getManagedBean("eventSupport");
		eventSupport.setEvent(rosterEvent);
		
		for(EventPosition eventPosition : rosterEvent.getAssignedEventPosition()){
			if(eventPosition.getId().intValue()==selectedEventPosition.getId().intValue()){
				eventSupport.setScheduleEventPosition(eventPosition);
				
				
			}
		}
		
		return "/ui/admin/schedule/scheduleEventPosition?faces-redirect=true&viewId=roster&operation=edit&includeViewParams=true";
	}
	/*public void staffReqtListPreProcessorPDF(Object document){
		this.staffReqtListRpt = getReport();
		Document pdf = (Document) document;  
	    pdf.setPageSize(PageSize.A4.rotate());  
	    
	    try {
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(pdf, baos);
	    	this.addHeader(pdf);
	        PageXofY pageXofY = new PageXofY();
	        writer.setPageEvent(pageXofY);
			pdf.open();
			Paragraph eventDetail = new Paragraph();
			eventDetail.setAlignment(Element.ALIGN_CENTER);
		    eventDetail.add(new Paragraph("Staff Requirement Listing"));
		    SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy");
		    String locationname = "All, ";
		    if(selectedLocation!=null)
		    	locationname = selectedLocation.getLocationTitle()+" "+selectedLocation.getName();
			eventDetail.add(new Paragraph("Location: "+locationname+" Event Date : From "+dateformat.format(rosterStartDate)+" Through "+dateformat.format(rosterEndDate)));
			pdf.add(new Paragraph(""));
		    pdf.add(eventDetail);
		    pdf.add( Chunk.NEWLINE );
		    
	    } catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public void staffReqtListPreProcessorPDF(String layout){
		this.staffReqtListRpt = getReport();
		Document pdf = new Document();//(Document) document;  
		if(layout.equalsIgnoreCase("landscape"))
			pdf.setPageSize(PageSize.A4.rotate());
		else
			pdf.setPageSize(PageSize.A4); 
	    
	    try {
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(pdf, baos);
	    	this.addHeader(pdf);
	        PageXofY pageXofY = new PageXofY();
	        writer.setPageEvent(pageXofY);
			pdf.open();
			Paragraph eventDetail = new Paragraph();
			eventDetail.setAlignment(Element.ALIGN_CENTER);
		    eventDetail.add(new Paragraph("Staff Requirement Listing"));
		    SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy");
		    String locationname = "All ";
		    if(selectedLocation!=null)
		    	locationname = selectedLocation.getLocationTitle()+" "+selectedLocation.getName();
			eventDetail.add(new Paragraph("Region: "+regionBean.getSelectedRegion().getName()+", Location: "+locationname+", Event Date : From "+dateformat.format(rosterStartDate)+" Through "+dateformat.format(rosterEndDate)));
			pdf.add(new Paragraph(""));
		    pdf.add(eventDetail);
		    pdf.add( Chunk.NEWLINE );
		    EventPositionPDFExporter eventPositionPDFExporter = new EventPositionPDFExporter();
		    eventPositionPDFExporter.staffReportTable(pdf,staffReqtListRpt);
		    pdf.add(new Paragraph(""));
		    pdf.add( Chunk.NEWLINE );
		    pdf.close();
		    writePDFToResponse(FacesContext.getCurrentInstance().getExternalContext(), baos, "Event_Staff_Requirement");
			
	    } catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<OneTouchReport> getReport() {
		Map<String,OneTouchReport> reportEventMap = new LinkedHashMap<String, OneTouchReport>();
		if(eventList!=null && eventList.size()>0){
			for(Event event : eventList){
				
				SimpleDateFormat dateformat = new SimpleDateFormat("EEEEEE, MMM d, yyyy");
				String eventDate = dateformat.format(event.getStartDate());
				OneTouchReport oneTouchReport = reportEventMap.get(eventDate);
				if(oneTouchReport==null){
					oneTouchReport = new OneTouchReport(new ArrayList<Event>());
					oneTouchReport.setEventDate(eventDate);
					reportEventMap.put(eventDate, oneTouchReport);
				}
				oneTouchReport.getEventList().add(event);
			}
		}
		
		return new ArrayList<OneTouchReport>(reportEventMap.values());
	} 
	    
	

	//////////////////////////////
        
        /**
	 * add event detail to pdf document 
	 * @param document
	 */
	public void processPDF(String layout){
		Font smallBold = new Font(Font.TIMES_ROMAN, 12,Font.BOLD);
		Document pdf = new Document();
		if(layout.equals("landscape"))
			pdf.setPageSize(PageSize.A4.rotate());
		else
			pdf.setPageSize(PageSize.A4);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
	    try {
	    	PdfWriter writer = PdfWriter.getInstance(pdf, baos);
	    	this.addImage(pdf);
	    
	        PageXofY pageXofY = new PageXofY();
	        writer.setPageEvent(pageXofY);
            pdf.open(); 
            for(Event event : eventList) { 
                     
	            pdf.newPage();
	            this.addImage(pdf);
	            
	            Paragraph eventDetail = new Paragraph();
			    // We add one empty line
			    //addEmptyLine(eventDetail, 1);
			    // Lets write a big header
			    eventDetail.add(new Paragraph("Title : "+event.getName()+"["+event.getId()+"]",smallBold));
			    
			    String event_time = "";
			    SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy");
				SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
				event_time = event_time+ dateformat.format(event.getStartDate());
				event_time = event_time+ ", "+timeformat.format(event.getStartTime());
				event_time = event_time+ " to "+timeformat.format(event.getEndTime());
			    eventDetail.add(new Paragraph("Event Date/Time : "+event_time));
			    
			    Location location = event.getLocation();
			    String name = location.getName()+"["+location.getCode()+"], ";
			    String address = location.getAddress1();
			    String address2 = (location.getAddress2()==null)?"":(", "+location.getAddress2()); 
			    address = name+address+address2+", "+location.getCity()+", "+location.getState().getStateName()+", "+location.getZipcode();
			   	eventDetail.add(new Paragraph("Location : "+address));
			    
			   	//eventDetail.add(new Paragraph("Admin : "+event.getOwner().getFirstname()+event.getOwner().getLastname()));
			    //eventDetail.add(new Paragraph("Manager : "+event.getCoordinator().getFirstname()+event.getCoordinator().getLastname()));
			    //eventDetail.add(new Paragraph("Client/Host : "+event.getHostname()));
			    String s = "Admin : "+event.getOwner().getFirstname()+" "+event.getOwner().getLastname()+", "+" Manager : "+event.getCoordinator().getFirstname()+" "+event.getCoordinator().getLastname()+", "+" Client/Host : "+event.getHostname();
			    eventDetail.add(new Paragraph(s));
			    eventDetail.add(new Paragraph("Notes : "+ event.getNotes()));
			    eventDetail.add(new Paragraph("Description : "+event.getDescription()));
			    eventDetail.add(new Paragraph("DressCode : "+event.getDressCode()));
			    eventDetail.add(new Paragraph("Guest Count : "+event.getGuestCount()));
			    eventDetail.add(new Paragraph(""));
			    
			    eventDetail.add(new Paragraph(tenant.getPolicyNotes(),new Font(Font.TIMES_ROMAN, 8,Font.NORMAL)));
	            	            
			    pdf.add(eventDetail);
			    List<EventPosition> exportEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
			    pdf.add(exportPDFTable(exportEventPositions));
			    pdf.add( Chunk.NEWLINE );
			    EventPositionPDFExporter eventPositionPDFExporter = new EventPositionPDFExporter();
			    eventPositionPDFExporter.exportEventPositionPDFTable(pdf,exportEventPositions);
			    //pdf.add(employeeScheduleTable);
			    pdf.add(new Paragraph(""));
			    
            }
            pdf.newPage();
            EventPositionPDFExporter eventPositionPDFExporter = new EventPositionPDFExporter();
            eventPositionPDFExporter.getExtraStaffRpt(pdf);
            pdf.add( Chunk.NEWLINE );
            
			pdf.close();
			
            writePDFToResponse(FacesContext.getCurrentInstance().getExternalContext(), baos, "Roster_Schedule");

		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
        
	protected PdfPTable exportPDFTable(List<EventPosition> exportEventPositions) {
    	
    	String[] headers = new String[] {"Position Name", "Start Time", "End Time", "Required", "Staffed", "Short"};
    	PdfPTable pdfTable = new PdfPTable(headers.length);
    	pdfTable.setWidthPercentage(100);
        
    	
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            PdfPCell cell = new PdfPCell();
            cell.setGrayFill(0.6f);
            cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.HELVETICA, 10, Font.BOLD)));
            pdfTable.addCell(cell);
        }
        pdfTable.completeRow();
     
        for(EventPosition eventPosition : exportEventPositions) { 
        	
        	String positionName = eventPosition.getPosition().getName();
            PdfPCell positionCell = new PdfPCell();
            positionCell.setPhrase(new Phrase(positionName.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(positionCell);
            
            SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
			
            String startTime = timeformat.format(eventPosition.getStartTime());
            PdfPCell stcell = new PdfPCell();
            stcell.setPhrase(new Phrase(startTime.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(stcell);
            
            String endTime = timeformat.format(eventPosition.getEndTime());
            PdfPCell etcell = new PdfPCell();
            etcell.setPhrase(new Phrase(endTime.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(etcell);
            
            String reqdCount = eventPosition.getReqdNumber().toString();
            PdfPCell reqdCntcell = new PdfPCell();
            reqdCntcell.setPhrase(new Phrase(reqdCount.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(reqdCntcell);
            
            Integer staffed = eventPosition.getPosition().getAcceptedEmps().size();
            PdfPCell staffedCntcell = new PdfPCell();
            staffedCntcell.setPhrase(new Phrase(staffed.toString().toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(staffedCntcell);
            
            Integer toFill = eventPosition.getReqdNumber()- staffed;
            PdfPCell toFillcell = new PdfPCell();
            toFillcell.setPhrase(new Phrase(toFill.toString().toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(toFillcell);
            
            pdfTable.completeRow();
            
        }	
        pdfTable.completeRow();
    	return pdfTable;
	}
    
	/**
	 * add image to pdf document
	 * @param pdf
	 * @throws BadElementException
	 * @throws MalformedURLException
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void addHeader(Document pdf) throws BadElementException, MalformedURLException, DocumentException, IOException {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
	    String logo = servletContext.getRealPath("") + File.separator + "images" + File.separator + "small_logo.jpg";  
	    Image logo_iamge = Image.getInstance(logo);
	    logo_iamge.setAlignment(Image.MIDDLE);
	    logo_iamge.scaleAbsoluteHeight(30);
	    logo_iamge.scaleAbsoluteWidth(20);
	    logo_iamge.scalePercent(80);

	    /*float height =  logo_iamge.getScaledHeight()+40f;
	    float width = logo_iamge.getScaledWidth()+40f;
	    logo_iamge.setAbsolutePosition(PageSize.A4.getWidth() - width, PageSize.A4.getHeight() - height);
	    logo_iamge.scaleAbsolute(100, 30);*/
	    Chunk chunk = new Chunk(logo_iamge, 0, 0);
	    HeaderFooter header = new HeaderFooter(new Phrase(chunk), false);
	    header.setBorder(Rectangle.NO_BORDER); 
	    header.setAlignment(Element.ALIGN_CENTER);
	    pdf.setHeader(header);
	   // pdf.add(logo_iamge);  
	}
	private void addImage(Document pdf) throws BadElementException, MalformedURLException, DocumentException, IOException {
		HeaderFooter footer = new HeaderFooter(new Phrase("Completed sheets must be emailed to "+tenantContext.getTenant().getCompanyEmail()+" or faxed to "+tenantContext.getTenant().getFax(), new Font()), false);
		footer.setBorder(Rectangle.NO_BORDER); 
	    footer.setAlignment(Element.ALIGN_CENTER);
	    pdf.setFooter(footer);
	   
	    ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
	    String logo = servletContext.getRealPath("") + File.separator + "images" + File.separator + "small_logo.jpg";  
	    Image logo_iamge = Image.getInstance(logo);
	    logo_iamge.setAlignment(Image.MIDDLE);
	    logo_iamge.scaleAbsoluteHeight(30);
	    logo_iamge.scaleAbsoluteWidth(20);
	    logo_iamge.scalePercent(80);

	    /*float height =  logo_iamge.getScaledHeight()+40f;
	    float width = logo_iamge.getScaledWidth()+40f;
	    logo_iamge.setAbsolutePosition(PageSize.A4.getWidth() - width, PageSize.A4.getHeight() - height);
	    logo_iamge.scaleAbsolute(100, 30);*/
	    Chunk chunk = new Chunk(logo_iamge, 0, 0);
	    HeaderFooter header = new HeaderFooter(new Phrase(chunk), false);
	    header.setBorder(Rectangle.NO_BORDER); 
	    header.setAlignment(Element.ALIGN_CENTER);
	    pdf.setHeader(header);
	   // pdf.add(logo_iamge);  
	}
	private static void addEmptyLine(Paragraph paragraph, int number) {
	    for (int i = 0; i < number; i++) {
	      paragraph.add(new Paragraph(" "));
	    }
	}
	
	protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName)
    throws IOException, DocumentException {
	externalContext.setResponseContentType("application/pdf");
	externalContext.setResponseHeader("Expires", "0");
	externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	externalContext.setResponseHeader("Pragma", "public");
	externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
	externalContext.setResponseContentLength(baos.size());
	externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
	OutputStream out = externalContext.getResponseOutputStream();
	baos.writeTo(out);
	externalContext.responseFlushBuffer();
	FacesContext.getCurrentInstance().responseComplete();
	}
	public List<OneTouchReport> getStaffReqtListRpt() {
		return staffReqtListRpt;
	}
	public void setStaffReqtListRpt(List<OneTouchReport> staffReqtListRpt) {
		this.staffReqtListRpt = staffReqtListRpt;
	}
	public Tenant getTenant() {
		return tenant;
	}


	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}



	public Event getRosterEvent() {
		return rosterEvent;
	}

	public void setRosterEvent(Event rosterEvent) {
		this.rosterEvent = rosterEvent;
	}

	public List<Event> getEventList() {
		return eventList;
	}


	public void setEventList(List<Event> eventList) {
		this.eventList = eventList;
	}


	public List<Player> getPlayers() {
		return players;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	public List<EventPosition> getAllEventPositions() {
		return allEventPositions;
	}
	public void setAllEventPositions(List<EventPosition> allEventPositions) {
		this.allEventPositions = allEventPositions;
	}
	public Integer getSelectEventId() {
		return selectEventId;
	}
	public void setSelectEventId(Integer selectEventId) {
		this.selectEventId = selectEventId;
	}
	public Date getRosterStartDate() {
		return rosterStartDate;
	}
	public void setRosterStartDate(Date rosterStartDate) {
		this.rosterStartDate = rosterStartDate;
	}
	public Date getRosterEndDate() {
		return rosterEndDate;
	}
	public void setRosterEndDate(Date rosterEndDate) {
		this.rosterEndDate = rosterEndDate;
	}

	public Location getSelectedLocation() {
		return selectedLocation;
	}

	public void setSelectedLocation(Location selectedLocation) {
		this.selectedLocation = selectedLocation;
	}

	public Date getRosterDate() {
		return rosterDate;
	}

	public void setRosterDate(Date rosterDate) {
		this.rosterDate = rosterDate;
	}

	public MainMenuBean getMainMenuBean() {
		return mainMenuBean;
	}

	public void setMainMenuBean(MainMenuBean mainMenuBean) {
		this.mainMenuBean = mainMenuBean;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}
	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	public Event getTransferEvent() {
		return transferEvent;
	}
	public void setTransferEvent(Event transferEvent) {
		this.transferEvent = transferEvent;
	}
	public Employee getTransferEmployee() {
		return transferEmployee;
	}
	public void setTransferEmployee(Employee transferEmployee) {
		this.transferEmployee = transferEmployee;
	}
	public EventPosition getTransferEventPosition() {
		return transferEventPosition;
	}
	public void setTransferEventPosition(EventPosition transferEventPosition) {
		this.transferEventPosition = transferEventPosition;
	}
	public String getActiveTabIndex() {
		return activeTabIndex;
	}
	public void setActiveTabIndex(String activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}
	public Integer getEventId() {
		return eventId;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	
}
