package com.onetouch.view.bean.useremployee.availability;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.schedule.ScheduleHome;
import com.onetouch.view.bean.util.RepeatAvailabilityBean;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="availHome")
@ViewScoped
public class AvailabilityHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Tenant tenant;
	private ScheduleModel eventModel;  
	private ScheduleEvent event ;
	private Availability availability; // this will always be in sync with event via id
	private CustomUserDetail user = null;
	private Employee employee = null;

	@ManagedProperty(value="#{repeatAvailability}")
	private RepeatAvailabilityBean repeatAvailabilityBean;
	private Date currentAvailDate;
	private List<Availability> allAvailability;
	private String fromview;
	private Integer empId;
	private String availmode = "week";
	@PostConstruct
	public void initAvailHome(){
		eventModel = new DefaultScheduleModel();  
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		
		tenant = tenantContext.getTenant();
		fromview = FacesUtils.getRequestParameter("fromview");
		if(fromview!=null && fromview.equalsIgnoreCase("admin")){
			empId = Integer.parseInt(FacesUtils.getRequestParameter("empId"));
			employee = getEmployeeService().getEmplyeePositionById(empId, tenant);
		}
		else{
			employee = getEmployeeService().getEmplyeePositionById(user.getEmp_id(), tenant);
		}
		allAvailability = getEmployeeAvailability().getAllAvailability(tenant, employee);
        
        if(FacesUtils.isMobileRequest())
			filterAvailability();
		else
			populateAvailabilityCalender(allAvailability,fromview);
		availability = new Availability(tenant, employee);
		/*eventModel = new LazyScheduleModel() {  
            
            @Override  
            public void loadEvents(Date start, Date end) {  
                clear();  
                if(tenant==null)
                	tenant = tenantContext.getTenant();
                //Date end1 =	DateUtil.decrementByDay(end,1);
                allAvailability = getEmployeeAvailability().getAllAvailability(tenant, employee,start,end);
                
                if(FacesUtils.isMobileRequest())
        			filterAvailability();
        		else
        			populateAvailabilityCalender(allAvailability,fromview);
        		availability = new Availability(tenant, employee);
                
            } 
            
            
        }; */
		
		this.currentAvailDate = new Date();
	}
	/*@PostConstruct
	public void avail(){
		long startTime = System.currentTimeMillis();
		allAvailability = getEmployeeAvailability().getAllAvailability(new Tenant(3),new Employee(52));
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.print(totalTime);
		
		
	}*/
	public String editAvailability(){
		Date availDate = availability.getAvailDate();
		Date startTime = availability.getStartTime();
		Date endTime = availability.getEndTime();
		startTime = DateUtil.getDateAndTime(availDate, startTime);
		endTime = DateUtil.getDateAndTime(availDate, endTime);
		availability.setStartDate(availDate);
		availability.setEndDate(availDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		return "pm:newempavl?transition=slide";
	}
	public String createNewAvailability(){
		availability = new Availability(tenant, employee);
		String renderKitId = FacesContext.getCurrentInstance().getViewRoot().getRenderKitId();
		if(renderKitId.equalsIgnoreCase("PRIMEFACES_MOBILE"))
			return "pm:newempavl";
		else
			return null;
	}
	private void populateAvailabilityCalender(List<Availability> allAvailability,String fromview) {
		for(Availability availability : allAvailability){
			
			String title = availability.getTitle();
			Date availDate = availability.getAvailDate();
			Date startTime = availability.getStartTime();
			Date endTime = availability.getEndTime();
			startTime = DateUtil.getDateAndTime(availDate, startTime);
			endTime = DateUtil.getDateAndTime(availDate, endTime);
			
			
			ScheduleEvent tempevent = null;
			if(availability.isAllday())
				tempevent = new DefaultScheduleEvent(title,startTime,endTime,availability.getId(),true);
			else{
				if(fromview!=null && fromview.equalsIgnoreCase("admin"))
					tempevent = new DefaultScheduleEvent("",startTime,endTime,availability.getId());
				else
					tempevent = new DefaultScheduleEvent(title,startTime,endTime,availability.getId());
			}
			eventModel.addEvent(tempevent);
				
			
		}
		
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}
	public void handleStartDateSelect(SelectEvent event) {  
          
		Date start_date = availability.getStartDate();
		Date end_date = availability.getEndDate();
		if(end_date==null)
			availability.setEndDate(start_date);
		/*		else if(start_date !=null && start_date.getTime()>end_date.getTime())
			FacesUtils.addErrorMessage("sdate","Enter a valid Start date");*/
		
		if(start_date !=null && end_date !=null && DateUtil.compareDateOnly(start_date,end_date)>0)
			FacesUtils.addErrorMessage("sdate","Enter a valid start date");
		else{
			List<String> updateIds = new ArrayList<String>();
				updateIds.add("sdateMsg");
				updateIds.add("edateMsg");
				RequestContext.getCurrentInstance().update(updateIds);
			
    	}
		
    } 
	public void handleEndDateSelect(SelectEvent event) {  
        Date start_date = availability.getStartDate();
		Date end_date = availability.getEndDate();
		if(start_date !=null && end_date !=null && DateUtil.compareDateOnly(start_date,end_date)>0)
			FacesUtils.addErrorMessage("edate","Enter a valid End date");
		else{
			List<String> updateIds = new ArrayList<String>();
				updateIds.add("sdateMsg");
				updateIds.add("edateMsg");
				RequestContext.getCurrentInstance().update(updateIds);
			
    	}
		/*if(start_date !=null && end_date !=null && start_date.getTime()>end_date.getTime())
			FacesUtils.addErrorMessage("edate","Enter a valid end date");*/
    } 
	public void addAvailability(ActionEvent actionEvent) { 
		Date stime = this.availability.isAllday()?DateUtil.getDate("00:00 AM","hh:mm a"): this.availability.getStartTime();
    	Date etime = this.availability.isAllday()?DateUtil.getDate("11:59 PM","hh:mm a"):this.availability.getEndTime();
		this.availability.setStartTime(stime);
		this.availability.setEndTime(etime);
		
		if(FacesUtils.isMobileRequest()){
			
			if(stime !=null && etime !=null && DateUtil.compareTimeOnly(stime,etime)>=0){
				FacesContext context = FacesContext.getCurrentInstance();
				context.getExternalContext().getFlash().setKeepMessages(true);
				FacesUtils.addErrorMessage("Start time can't be after end time");
			}
			else
			{
				if(getEmployeeAvailability().overlapAvailability(this.availability,employee,tenant))
				{
					FacesUtils.addErrorMessage("availgrowl","Invalid Time, Availability already exist, Overlap with existing availability not allowed, please enter a different availability");
					RequestContext context = RequestContext.getCurrentInstance(); 
					context.update("availgrowl");
			    }
				else{
					List<Availability> availabilities = new ArrayList<Availability>();
		        	int days = DateUtil.dateRange(availability.getStartDate(), availability.getEndDate()); 
		        	if(days==0){
		        		Date avail_date = availability.getStartDate();
		        		availability.setAvailDate(avail_date);
		        		availabilities.add(availability);
		        		availabilities = getEmployeeAvailability().addAvailability(availabilities);
		        		
		        	}else{
		        		availabilities = repeatAvailabilityBean.getDailyAvailability(days, availability, employee, tenant);
			        	availabilities = getEmployeeAvailability().addAvailability(availabilities);
		        	}
			        	if(allAvailability==null)
			        		allAvailability = new ArrayList<Availability>();
			        	if(availabilities!=null && availabilities.size()>0)
			        		allAvailability.addAll(availabilities);
			        	FacesContext context = FacesContext.getCurrentInstance();
						context.getExternalContext().getFlash().setKeepMessages(true);
		        		FacesUtils.addInfoMessage("New Availability Added");
		        		//return "pm:avails";
					}
			}
			//return null;
		}else{
			if(stime !=null && etime !=null && DateUtil.compareTimeOnly(stime,etime)>=0){
				FacesUtils.addErrorMessage("Enter a valid start time");
				RequestContext.getCurrentInstance().update("availgrowl");
			}else{
				if(getEmployeeAvailability().overlapAvailability(this.availability,employee,tenant)){
					{
						FacesUtils.addErrorMessage("availgrowl","Invalid Time, Availability already exist, Overlap with existing availability not allowed, please enter a different availability");
						RequestContext context = RequestContext.getCurrentInstance(); 
						
			            context.update("availgrowl");
			        }
					//FacesUtils.addErrorMessage("etime","Overlap with existing availability not allowed");
				}else{
			        //if(availability.getId() == null) {
			        	List<Availability> availabilities = new ArrayList<Availability>();
			        	int days = DateUtil.dateRange(availability.getStartDate(), availability.getEndDate()); 
			        	if(days==0){
			        		Date avail_date = availability.getStartDate();
			        		availability.setAvailDate(avail_date);
			        		availabilities.add(availability);
			        		availabilities = getEmployeeAvailability().addAvailability(availabilities);
			        		this.currentAvailDate = avail_date;
			        	}else{
			        		if(availability.isSelected()){
			        			availabilities = repeatAvailabilityBean.getDailyRepeatAvailability(days, availability, employee, tenant);
				        		availabilities = getEmployeeAvailability().addAvailability(availabilities);
			        		}else{
				        		availabilities = repeatAvailabilityBean.getDailyAvailability(days, availability, employee, tenant);
				        		availabilities = getEmployeeAvailability().addAvailability(availabilities);
			        		}
			        		this.currentAvailDate = new Date(availability.getStartDate().getTime());
			        	}
			        	populateAvailabilityCalender(availabilities,"");
			        	
			        	availability = new Availability(tenant, employee);
			        //}
			        
				}
			}
			//return null;
		}
       
    }
	
	public void updateAvailability(ActionEvent actionEvent){
		if(availability.getId() != null) {
			Date stime = this.availability.isAllday()?DateUtil.getDate("00:00 AM","hh:mm a"): this.availability.getStartTime();
	    	Date etime = this.availability.isAllday()?DateUtil.getDate("11:59 PM","hh:mm a"):this.availability.getEndTime();
			
			
			if(stime !=null && etime !=null && DateUtil.compareTimeOnly(stime,etime)>0){
				FacesUtils.addErrorMessage("availgrowl","Enter a valid time");
				RequestContext context = RequestContext.getCurrentInstance(); 
				
	            context.update("availgrowl");
				
			}else{
				this.availability.setStartTime(stime);
				this.availability.setEndTime(etime);
				getEmployeeAvailability().updateAvailability(availability);
				if(FacesUtils.isMobileRequest()){
					FacesContext context = FacesContext.getCurrentInstance();
					context.getExternalContext().getFlash().setKeepMessages(true);
	        		FacesUtils.addInfoMessage("Availability Updated");
				}else{
		        	String title = availability.getTitle();
		        	Date availDate = availability.getAvailDate();
					Date startTime = availability.getStartTime();
					Date endTime = availability.getEndTime();
					
					startTime = DateUtil.getDateAndTime(availDate, startTime);
					endTime = DateUtil.getDateAndTime(availDate, endTime);
					
					
					List<ScheduleEvent> events = eventModel.getEvents();
					int index = -1;
					
					for(int i = 0 ; i < events.size(); i++) {
						if(events.get(i).getId().equals(event.getId())) {
							index = i;
							
							break;
						}
					}
					
					if(index >= 0) {
						ScheduleEvent replaceEvent = null;
						if(availability.isAllday())
							replaceEvent = new DefaultScheduleEvent(title,startTime,endTime,availability.getId(),true);
						else
							replaceEvent = new DefaultScheduleEvent(title,startTime,endTime,availability.getId());
						
						
						replaceEvent.setId(event.getId());
						events.set(index, replaceEvent);
					}
					this.currentAvailDate = availability.getAvailDate();
					}
				}
			}
		
		//return null;
	}
	public void onEventSelect(SelectEvent selectEvent) {  
        event = (ScheduleEvent) selectEvent.getObject();  
        Integer availId = (Integer)event.getData(); ;
        availability = getEmployeeAvailability().getAvailability(availId,employee,tenant);
        
        RequestContext.getCurrentInstance().update("availDialog");
    }  
    
    public void closeAvailDialog(CloseEvent event){
    	availability = new Availability(tenant, employee);
		this.currentAvailDate = new Date();
    }
    public void selectAvailStartTime(){
    	Date stime = this.availability.getStartTime();
    	Date etime = this.availability.getEndTime();
		if(stime !=null && etime !=null && DateUtil.compareTimeOnly(stime,etime)>=0)
			FacesUtils.addErrorMessage("stime","Enter a valid start time");
		else{
			if(getEmployeeAvailability().overlapAvailability(this.availability,employee,tenant)){
				FacesUtils.addErrorMessage("availgrowl","Availability already found, Overlap with existing availability not allowed, please enter a different availability");
				RequestContext context = RequestContext.getCurrentInstance(); 
				
	            context.update("availgrowl");
				
			}
			else{
				List<String> updateIds = new ArrayList<String>();
				updateIds.add("stimeMsg");
				updateIds.add("etimeMsg");
				RequestContext.getCurrentInstance().update(updateIds);
			}
    	}
	}
    public void selectAvailEndTime(){
    	Date stime = this.availability.getStartTime();
    	Date etime = this.availability.getEndTime();
		if(stime !=null && etime !=null && DateUtil.compareTimeOnly(stime,etime)>=0)
			FacesUtils.addErrorMessage("etime","Enter a valid end time");
		else{
			if(getEmployeeAvailability().overlapAvailability(this.availability,employee,tenant)){
				{
					FacesUtils.addErrorMessage("availgrowl","Availability already found, Overlap with existing availability not allowed, please enter a different availability");
					RequestContext context = RequestContext.getCurrentInstance(); 
					
		            context.update("availgrowl");
		        }
				//FacesUtils.addErrorMessage("etime","Overlap with existing availability not allowed");
			}
			else{
				List<String> updateIds = new ArrayList<String>();
				updateIds.add("stimeMsg");
				updateIds.add("etimeMsg");
				RequestContext.getCurrentInstance().update(updateIds);
			}
    	}
	}
   public void deleteAvailability(ActionEvent actionEvent){
	   getEmployeeAvailability().deleteAvailability(availability.getId(), employee, tenant);
	   allAvailability.remove(availability);
	   if(!FacesUtils.isMobileRequest())
		   eventModel.deleteEvent(event);
	   
   }
   
   public void filterAvailability(){
	   Date today = new Date();
	   Iterator<Availability> availIte = allAvailability.iterator();
	   while(availIte.hasNext()){
		   Availability availability = availIte.next();
		   if(DateUtil.compareDateOnly(availability.getAvailDate(),today)<0)
			   availIte.remove();
	   }
	   
   }
	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}
    
	

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	public RepeatAvailabilityBean getRepeatAvailabilityBean() {
		return repeatAvailabilityBean;
	}

	public void setRepeatAvailabilityBean(
			RepeatAvailabilityBean repeatAvailabilityBean) {
		this.repeatAvailabilityBean = repeatAvailabilityBean;
	}

	public Date getCurrentAvailDate() {
		return currentAvailDate;
	}

	public void setCurrentAvailDate(Date currentAvailDate) {
		this.currentAvailDate = currentAvailDate;
	}

	public List<Availability> getAllAvailability() {
		return allAvailability;
	}

	public void setAllAvailability(List<Availability> allAvailability) {
		this.allAvailability = allAvailability;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public String getFromview() {
		return fromview;
	}
	public void setFromview(String fromview) {
		this.fromview = fromview;
	}
	public Integer getEmpId() {
		return empId;
	}
	public void setEmpId(Integer empId) {
		this.empId = empId;
	}
	public String getAvailmode() {
		return availmode;
	}
	public void setAvailmode(String availmode) {
		this.availmode = availmode;
	}
	
	

}
