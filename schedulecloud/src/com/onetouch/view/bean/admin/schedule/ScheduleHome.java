package com.onetouch.view.bean.admin.schedule;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Region;

import com.onetouch.model.domainobject.Location;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.service.event.EventService;
import com.onetouch.model.service.event.IEventService;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.bean.menu.MainMenuBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.context.TenantContext;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="adminScheduleHome")
@ViewScoped
public class ScheduleHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger("ScheduleHome.class");
	private Location selectedLocation;
	private List<Event> eventList;
	private Event selectedEvent;
	private Tenant tenant;
	private ScheduleModel eventModel;
	private CustomUser customUser=null;
    private Map<String,Integer> eventCountMap;
    @ManagedProperty(value="#{regionBean}")
    protected RegionBean regionBean;
   
    public void keepSessionAlive(ActionEvent actionEvent){
    	FacesContext fc = FacesContext.getCurrentInstance();
    	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
    	request.getSession();
    }
    public RegionBean getRegionBean() {
		return regionBean;
	}
	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
    public ScheduleHome(){
    	System.out.println("Schedule Home Created");
    	SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
    		if(tenantContext!=null)
    			tenant = tenantContext.getTenant();
			if(tenant == null){
				UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
				if(authenticationToken.isAuthenticated()){
					customUser = (CustomUser)authenticationToken.getPrincipal();
					
				}
				tenant = customUser.getTenant();
				
				supportBean.setTenant(tenant);
				if(tenantContext!=null){
					tenantContext.setTenant(tenant);
					
				}
				
				//supportBean.setActiveTabIndex(0);
			}
			
    	
    	eventModel = new LazyScheduleModel() {  
            
            @Override  
            public void loadEvents(Date start, Date end) {  
                clear();  
                if(tenant==null)
                	tenant = tenantContext.getTenant();
                //eventList = getEventService().getAllPublishedEventsByDateRange(getTenant(),start,end,customUser);
                if(regionBean==null)
                	regionBean = (RegionBean)FacesUtils.getManagedBean("regionBean");
                Region currentRegion = regionBean.getSelectedRegion();
                eventList = getEventService().getAllPublishedEventsByDateRangeAndLocation(tenant, start, end, customUser, selectedLocation,currentRegion);// getAllPublishedEventsByDateRange(getTenant(),start,end,customUser);
                
                ScheduleHome.this.filterEventCount();
                //ScheduleHome.this.filterEventCountWithMore();
                
            } 
            
            
        }; 
        logger.warn("finish getting schedule data successfully");
    }
    /*@PostConstruct
    public void init(){
    	this.eventModel = new LazyScheduleModel() {  
            
            @Override  
            public void loadEvents(Date start, Date end) {  
                clear();  
                if(tenant==null)
                	tenant = tenantContext.getTenant();
                eventList = getEventService().getAllPublishedEventsByDateRangeAndLocation(tenant, start, end, customUser, selectedLocation);// getAllPublishedEventsByDateRange(getTenant(),start,end,customUser);
                
                ScheduleHome.this.filterEventCount();
                
            } 
            
            
        }; 
        logger.warn("finish getting schedule data successfully");
    }*/
    @PreDestroy
	public void destroy()
	{
		System.out.println("Schedule Home Destroy");
	}
    public Date getRandomDate(Date base) {  
        Calendar date = Calendar.getInstance();  
        date.setTime(base);  
        date.add(Calendar.DATE, ((int) (Math.random()*30)) + 1);    //set random day of month  
          
        return date.getTime();  
    }  
    @PostConstruct
	public void initScheduleHome(){
    	if(tenantContext!=null){
			tenantContext.setTenant(tenant);
			Employee employee = getEmployeeService().getEmplyeePositionById(customUser.getEmp_id(), tenant);
			SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
			supportBean.setCurrentLoggedEmployee(employee);
			
    	}
		
	}
	
	
	public void selectLocationChange(){
		this.eventModel = new LazyScheduleModel() {  
            
            @Override  
            public void loadEvents(Date start, Date end) {  
                clear();  
                eventList = getEventService().getAllPublishedEventsByDateRangeAndLocation(tenant, start, end, customUser, selectedLocation,regionBean.getSelectedRegion());// getAllEventsByLocation(tenant, selectedLocation);
        		
                ScheduleHome.this.filterEventCount();
            } 
            
            
        }; 
		
		
			
	}
	
	protected void filterEventCount(){
		//eventModel = new DefaultScheduleModel();
		
		
		for(Event scheduleEvent : eventList){
			String title = scheduleEvent.getName().replaceAll("'", "");;
			Date eventStartTime = DateUtil.getDateAndTime(scheduleEvent.getStartDate(),scheduleEvent.getStartTime());
			Date eventEndTime = DateUtil.getDateAndTime(scheduleEvent.getStartDate(),scheduleEvent.getEndTime());
			ScheduleEvent adminScheduleEvent = null;
			if(scheduleEvent.isPendingSchedule())
				adminScheduleEvent = new DefaultScheduleEvent(title,eventStartTime,eventEndTime,scheduleEvent.getId(),scheduleEvent.getColorCode());
			
			else
				adminScheduleEvent = new DefaultScheduleEvent(title,eventStartTime,eventEndTime,scheduleEvent.getId());
			
				
			eventModel.addEvent(adminScheduleEvent);
				
		}
	}
	
	private void filterEventCountWithMore(){
		//eventModel = new DefaultScheduleModel();
		eventCountMap = new LinkedHashMap<String, Integer>();
		for(Event scheduleEvent : eventList){
			String title = scheduleEvent.getName().replaceAll("'", "");
			Date eventStartTime = DateUtil.getDateAndTime(scheduleEvent.getStartDate(),scheduleEvent.getStartTime());
			Date eventEndTime = DateUtil.getDateAndTime(scheduleEvent.getEndDate(),scheduleEvent.getEndTime());
			ScheduleEvent adminScheduleEvent = null;
			if(scheduleEvent.isPendingSchedule())
				adminScheduleEvent = new DefaultScheduleEvent(title,eventStartTime,eventEndTime,scheduleEvent.getId(),"pending-schedule");
			
			else
				adminScheduleEvent = new DefaultScheduleEvent(title,eventStartTime,eventEndTime,scheduleEvent.getId());
			
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      
		    try {
				String startDate = sdf.format(scheduleEvent.getStartDate());
				if(eventCountMap.containsKey(startDate)){
					Integer currentEventCount = eventCountMap.get(startDate);
					if(currentEventCount.intValue()<5){
						Integer value = eventCountMap.get(startDate);
						value = value+1;
						eventCountMap.put(startDate, value);
						eventModel.addEvent(adminScheduleEvent);	
					}
					// if has more than 5 events go to daily view 
					if(currentEventCount.intValue()==5){
						// only show 5 events, rest click show more and go to weekly view, show more is actually an event just some css hacking
						title = "More Events ...";
						startDate = startDate+" 23:59";
						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						Date dummyStartTime = sdf1.parse(startDate);
						Date dummyEndTime = sdf1.parse(startDate);
						adminScheduleEvent = new DefaultScheduleEvent(title,dummyStartTime,dummyEndTime,"more-event",true);
						eventModel.addEvent(adminScheduleEvent);
						eventCountMap.put(startDate, 6);
						
					}
					if(currentEventCount.intValue()>5){
						return;
					}
					
				}else{
					//first time
					eventCountMap.put(startDate, 1);
					eventModel.addEvent(adminScheduleEvent);	
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				
		}
	}
	/**
	 * event select on schedule component
	 * @param selectEvent
	 */
	public void onScheduleEventSelect(SelectEvent selectEvent) {  
		String contextRoot = FacesUtils.getServletContext().getContextPath();
		
        ScheduleEvent scheduleEvent = (ScheduleEvent) selectEvent.getObject(); 
        if(scheduleEvent!=null){
	        String title  = scheduleEvent.getTitle();
	        if(title.equalsIgnoreCase("More Events ...")){
	        	Date date = scheduleEvent.getStartDate();
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        	String eventDate = sdf.format(date);
	        	
	        	try {
					FacesContext.getCurrentInstance().getExternalContext().redirect( contextRoot  + "/ui/admin/event/roster.jsf?faces-redirect=true&startDate="+eventDate+"&endDate="+eventDate);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
	        }
	        else{
	        	
		        Integer id = (Integer)scheduleEvent.getData();
		        //Integer id = Integer.parseInt(scheduleEvent.getId());
		        
		        try {
		        	if(customUser.isSalesPerson()){
		        		FacesContext.getCurrentInstance().getExternalContext().redirect( contextRoot  + "/ui/admin/schedule/scheduleDetailSales.jsf?eventId="+id);
		        	}        	
		        	else{
		        		Date eventDate = scheduleEvent.getStartDate();
		        		boolean b = DateUtil.isPreviousEvent(eventDate,new Date());
		        		if(b)
		        			FacesContext.getCurrentInstance().getExternalContext().redirect( contextRoot  + "/ui/admin/schedule/oldScheduleDetail.jsf?eventId="+id);
		        		else
		        			FacesContext.getCurrentInstance().getExternalContext().redirect( contextRoot  + "/ui/admin/schedule/scheduleDetail.jsf?eventId="+id);
		        	}
		        } catch (IOException e) {
		        	
		        	e.printStackTrace();
		        }
	        }
        }else{
        	System.out.print(customUser.getUsername()+" "+scheduleEvent);
        }

    }
	/*public void onScheduleEventSelect(SelectEvent selectEvent) throws SQLException {
        throw new RuntimeException("Runtime error on ajax request");
	}*/
	public Location getSelectedLocation() {
		return selectedLocation;
	}
	public void setSelectedLocation(Location selectedLocation) {
		this.selectedLocation = selectedLocation;
	}
	public List<Event> getEventList() {
		return eventList;
	}
	public void setEventList(List<Event> eventList) {
		this.eventList = eventList;
	}
	public Event getSelectedEvent() {
		return selectedEvent;
	}

	public void setSelectedEvent(Event selectedEvent) {
		this.selectedEvent = selectedEvent;
	}

	
	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}


	public Tenant getTenant() {
		return tenant;
	}


	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	
	
}
 