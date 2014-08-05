package com.onetouch.view.bean.admin.employee;



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.mail.SimpleMailMessage;
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
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.onetouch.model.domainobject.TimeOffRequest;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.util.PopulateEmailAttributes;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.StringUtilities;
//import com.onetouch.view.util.StringUtilities;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.TimeOffRequestModel;


@ManagedBean(name="adminTimeoffBean")
@ViewScoped
public class AdminTimeOffBean extends BaseBean{
       
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private Employee employee;
	private CustomUser user;
    private Tenant tenant;
	private TimeOffRequest timeOff;
	private String action;
	private List<TimeOffRequest> timeoffList;
	private List<Event> sickEventList;
	private TimeOffRequestModel requestModel;
	private Event sickEvent;
	private List<Event> selectedSickEvents;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	
	@PostConstruct
	public void initController() {
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		
		tenant = tenantContext.getTenant();
		Integer empId = Integer.parseInt(FacesUtils.getRequestParameter("empId"));
		
		employee = getEmployeeService().getEmplyeeById(empId,tenant);
		employee.setTenant(tenant);
		timeoffList = getTimeoffService().getAllCallOutRequests(empId);
		requestModel = new TimeOffRequestModel(timeoffList);
		//timeOff = new TimeOffRequest("Call Out");
	}
	
	public void addNewSickTimeoff(ActionEvent actionEvent){
		timeOff = new TimeOffRequest("Call Out");
		//RequestContext.getCurrentInstance().execute("timeoffdlg.show();");
	}
	public String editSickTimeoff(){
		if(timeOff!=null){
			
			sickEventList = new ArrayList<Event>();
			selectedSickEvents = new ArrayList<Event>();
			Event sickevent = timeOff.getSickEvent();
			selectedSickEvents.add(sickevent);
			sickEventList.add(sickevent);
			RequestContext.getCurrentInstance().execute("timeoffdlg.show();");
		}else{
			FacesUtils.addErrorMessage("","Please select a Call Out request first");
		}
		return null;
	}
	public String deleteSickTimeoff(){
		getTimeoffService().deleteSickTimeOff(timeOff);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("","Call Out Request Deleted");
		return "sickTimeoff.jsf?faces-redirect=true&empId="+employee.getId();
	}
	public void handleDateSelect(SelectEvent event) {  
        
		Date start_date = timeOff.getBeginDate();
		Date end_date = timeOff.getEndDate();
		if(timeOff!=null && (timeOff.getRequestType()!=null&&timeOff.getRequestType().equalsIgnoreCase("Call Out"))){
			if(timeOff.getBeginDate()!=null && timeOff.getEndDate()!=null){
				if(DateUtil.compareDateOnly(timeOff.getBeginDate(), timeOff.getEndDate())>0)
					FacesUtils.addErrorMessage("startDt","Enter a valid  date");
				else{
					sickEventList =getEventService().getAllSchedulesByEmployee(start_date, end_date, employee, tenant, regionBean.getSelectedRegion()) ;
						//getEventService().getAllEventsByEmployee(employee,timeOff,tenant);
					RequestContext.getCurrentInstance().update("eventlist");
				}
			}
		}
		
    }
	
	public String addSickTimeOffRequest() {
		if(timeOff != null) {
			List<TimeOffRequest> sickRequests = new ArrayList<TimeOffRequest>();
			
			TimeOffRequest sickRequest = new TimeOffRequest("Call Out");
			sickRequest.setBeginDate(sickEvent.getStartDate());
			sickRequest.setEndDate(sickEvent.getEndDate());
			sickRequest.setCompanyId(tenant.getId());
			sickRequest.setEmployeeId(employee.getId());
			sickRequest.setNumDays(1);
			sickRequest.setCreateDate(new Date());
			sickRequest.setReason(timeOff.getReason());
			sickRequest.setSickEvent(sickEvent);
			sickRequests.add(sickRequest);
			List<Schedule> deletedSchedules = getTimeoffService().addSickTimeOffRequests(sickRequests);
			if(deletedSchedules!=null){
				String websiteUrl = getApplicationData().getServer_url();
				for(Schedule deleteSchedule: deletedSchedules){
				Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, sickEvent, deleteSchedule.getEventPosition(), employee, deleteSchedule.getPosition());//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
				attributes.put("emailsubject","YOU ARE NO LONGER SCHEDULED FOR " + tenant.getName()+", "+sickEvent.getLocation().getName()+", "+sickEvent.getName());
				attributes.put("priority", "HIGH");
				attributes.put("scheduleId", deleteSchedule.getId());
				deleteSchedule.setEmailAttributes(attributes);
				}
				this.sendScheduleDeleteEmail(deletedSchedules);
			}
			
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("","Call Out Request Saved, Email will be sent shortly");
		return "sickTimeoff.jsf?faces-redirect=true&empId="+employee.getId();
		//timeoffList = getTimeoffService().getAllCallOutRequests(employee.getId());
		//requestModel = new TimeOffRequestModel(timeoffList);
	}
	private void sendScheduleDeleteEmail(List<Schedule> schedules){
		for(Schedule schedule: schedules){
			Map<Object,Object> attributes = schedule.getEmailAttributes();
			List<String> receipients = new ArrayList<String>();
			receipients.add(employee.getEmail());
			getSheduleEmployeeSender().scheduleEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(), 
					tenant.getEmailSender(), "delete schedule");
			//getSender().deleteScheduleEmployee(scheduleMessage, attributes, receipients,tenant.getEmailSender());
		}
	}
	public String updateSickTimeOffRequest() {
		if(timeOff != null) {
			
			getTimeoffService().updateSickTimeOffRequest(timeOff);
			
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("","Call Out Request Saved");
		return "sickTimeoff.jsf?faces-redirect=true&empId="+employee.getId();

	}
	public void preProcessorPDF(Object document){
		Document pdf = (Document) document;  
	    
	    pdf.setPageSize(PageSize.A4.rotate());  
	    
	    try {
			this.addImage(pdf);
	    }catch (Exception e) {
			// TODO: handle exception
		}
	}
	private void addImage(Document pdf) throws BadElementException, MalformedURLException, DocumentException, IOException {
		
	    ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
	    String logo = servletContext.getRealPath("") + File.separator + "images" + File.separator + "small_logo.jpg";  
	    Image logo_iamge = Image.getInstance(logo);
	    logo_iamge.setAlignment(Image.MIDDLE);
	    logo_iamge.scaleAbsoluteHeight(30);
	    logo_iamge.scaleAbsoluteWidth(20);
	    logo_iamge.scalePercent(80);

	    
	    Chunk chunk = new Chunk(logo_iamge, 0, 0);
	    HeaderFooter header = new HeaderFooter(new Phrase(chunk), false);
	    header.setBorder(Rectangle.NO_BORDER); 
	    header.setAlignment(Element.ALIGN_CENTER);
	    pdf.setHeader(header);
	   
	}
	public Employee getEmployee() {
		return employee;
	}

	public TimeOffRequestModel getRequestModel() {
		return requestModel;
	}

	public void setRequestModel(TimeOffRequestModel requestModel) {
		this.requestModel = requestModel;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	

	public TimeOffRequest getTimeOff() {
		return this.timeOff;
	}

	public void setTimeOff(TimeOffRequest req) {
		this.timeOff = req;
	}

	public List<Event> getSickEventList() {
		return sickEventList;
	}

	public void setSickEventList(List<Event> sickEventList) {
		this.sickEventList = sickEventList;
	}

	public List<Event> getSelectedSickEvents() {
		return selectedSickEvents;
	}

	public void setSelectedSickEvents(List<Event> selectedSickEvents) {
		this.selectedSickEvents = selectedSickEvents;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}

	public List<TimeOffRequest> getTimeoffList() {
		return timeoffList;
	}

	public void setTimeoffList(List<TimeOffRequest> timeoffList) {
		this.timeoffList = timeoffList;
	}

	public Event getSickEvent() {
		return sickEvent;
	}

	public void setSickEvent(Event sickEvent) {
		this.sickEvent = sickEvent;
	}
	
}
