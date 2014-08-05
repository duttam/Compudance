package com.onetouch.view.bean.admin.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.validator.ValidatorException;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.Location;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Position;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.SalesPersonLocation;
import com.onetouch.model.domainobject.Schedule;



import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.LocationHome;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.admin.position.PositionController;
import com.onetouch.view.bean.admin.position.PositionSupport;

import com.onetouch.view.bean.util.DateBean;
import com.onetouch.view.bean.util.PopulateEmailAttributes;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="addEventBean")
@ViewScoped
public class AddEventBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Tenant tenant;
	private Event event;
	private Integer eventId;
	private String action;

	private List<Position> selectedPositions; // preselect positions for edit event

    private List<Employee> allAvailableEmployee;
    private List<Employee> ownerList;
    private List<Employee> coordinatorList;
    private List<Employee> salesList;
    @ManagedProperty(value="#{eventSupport}")
    private EventSupport eventSupport;
    private CustomUser customUser;
    private List<EventPosition> availableEventPosition;
	private EventPosition eventPosition;
	private String editEventView;
	private boolean eventPropertyChanged;
	
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	
	@PostConstruct
	public void initAddEventBean(){
		tenant = tenantContext.getTenant();
		action = FacesUtils.getRequestParameter("action");
		ownerList = getEmployeeService().getOwners(tenant);
		coordinatorList = getEmployeeService().getCoordinators(tenant);
		
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
			if(action!=null){
				if(action.equalsIgnoreCase("addevent")){
					event = new Event(tenant);
					event.setEventRegion(regionBean.getSelectedRegion());
					Integer coordinatorId = customUser.getEmp_id();
					int index = coordinatorList.indexOf(new Employee(coordinatorId));
					if(index!=-1)
					{
						Employee coordinator = coordinatorList.get(index);
						event.setCoordinator(coordinator);
					}
					//event.setCreatedBy(coordinatorId);
					eventSupport.setAction(action);
					LocationHome locationHome = (LocationHome)FacesUtils.getManagedBean("locationHome");
					Location location = locationHome.getLocationList().get(0);
					salesList = getEmployeeService().getSalesPersons(location,tenant);
				}
			
			
				if(action.equalsIgnoreCase("editevent")){
		
					if(eventId==null)
						eventId = Integer.parseInt(FacesUtils.getRequestParameter("eventid"));
					event = getEventService().getEvent(eventId,tenant,regionBean.getSelectedRegion());
					if(event.getAssignedEventPosition()!=null && event.getAssignedEventPosition().size()>0)
						availableEventPosition = event.getAssignedEventPosition();
					else{
						List<Position> positionList = getPositionService().getAllPosition(tenant);
						availableEventPosition =  new ArrayList<EventPosition>();
						for(int i=0;i<positionList.size();i++){
							Date shiftStartDate = this.createShiftDateTime(event.getStartDate(),event.getStartTime());
							Date shiftEndDate = this.createShiftDateTime(event.getEndDate(),event.getEndTime());
							availableEventPosition.add(new EventPosition(event,tenant,shiftStartDate,shiftEndDate));
						}
					}
					salesList = getEmployeeService().getSalesPersons(event.getLocation(),tenant);
					eventSupport.setAction(action);
					eventSupport.setEvent(event);
					DateBean dateBean = (DateBean)FacesUtils.getManagedBean("dateBean");
					dateBean.createShiftDateAndTime(event.getStartDate(),event.getEndDate());
					//availableEventPosition = event.getAssignedEventPosition();
					editEventView = FacesUtils.getRequestParameter("editview");
				}	
			}
		}else{
			System.out.print(customUser.getUsername()+" "+action);	
		}
			
	}
	
	public void filterSalesList(){
		Location selectedLocation = event.getLocation();
		salesList = getEmployeeService().getSalesPersons(selectedLocation,tenant);
	}
	public void saveEvent(ActionEvent actionEvent){
		
		if(event!=null){
			Date stime = event.getStartTime();
	    	Date etime = event.getEndTime();
	    	DateTime eventEnddate = event.getEndDate()==null?null:new DateTime(event.getEndDate());
			if(action.equalsIgnoreCase("addevent")){
				//this.selectEventStartTime();
				//this.selectEventStartDate();
				Employee salesPerson = event.getSalesPerson();
				if(salesPerson!=null)
					event.setCreatedBy(salesPerson.getId());
				else
					event.setCreatedBy(customUser.getEmp_id());
				
				if(eventEnddate == null){
					if(stime !=null && etime !=null && DateUtil.compareTimeOnly(stime,etime)>0){
						Date endDate = DateUtil.incrementByDay(event.getStartDate(),1);
						event.setEndDate(endDate);
					}else{
						event.setEndDate(event.getStartDate());
					}
				}
				event = getEventService().addEvent(event);
				DateBean dateBean = (DateBean)FacesUtils.getManagedBean("dateBean");
				dateBean.createShiftDateAndTime(event.getStartDate(),event.getEndDate());
				List<Position> positionList = getPositionService().getAllPosition(tenant);
				availableEventPosition =  new ArrayList<EventPosition>();
				for(int i=0;i<positionList.size();i++){
					Date shiftStartDate = this.createShiftDateTime(event.getStartDate(),event.getStartTime());
					Date shiftEndDate = this.createShiftDateTime(event.getEndDate(),event.getEndTime());
					availableEventPosition.add(new EventPosition(event,tenant,shiftStartDate,shiftEndDate));
				}
			}
			
		}
		eventSupport.setEvent(event);
		// remove validation success messages
		Iterator<FacesMessage> messageList =  FacesContext.getCurrentInstance().getMessages();
		
		while(messageList.hasNext()){
			FacesMessage facesMessage = messageList.next();
			messageList.remove();
		}
		FacesUtils.addInfoMessage("Event Saved Successfully");
	}
	/*
	 * populate dates in date bean depending on event dates
	 */
	/*private void populateDateBean(){
		
		Date stime = this.event.getStartTime();
    	Date etime = this.event.getEndTime();
    	DateBean dateBean = (DateBean)FacesUtils.getManagedBean("dateBean");
    	if(stime !=null && etime !=null && stime.getTime()>=etime.getTime()){
    		int days = DateUtil.dateRange(event.getStartDate(),event.getEndDate());
    		
    		if(days==1){
    			dateBean.createShiftDateAndTime(event.getStartDate(),event.getEndDate());
    		}
    	}else{
    		dateBean.createShiftTime();
    	}
	}*/
	public String updateEvent(){
		//this.selectEventStartTime();
		//this.selectEventStartDate();
		String websiteUrl = getApplicationData().getServer_url();
		Employee salesPerson = event.getSalesPerson();
		if(salesPerson!=null)
			event.setCreatedBy(salesPerson.getId());
		else
			event.setCreatedBy(customUser.getEmp_id());
		event.setEventRegion(regionBean.getSelectedRegion());
		getEventService().editEvent(event);
		if(eventPropertyChanged){
			List<EventPosition> allEventPositions = getScheduleService().getNotifiedScheduleByEventPosition(event, tenant);
			List<Schedule> updateSchedules = new ArrayList<Schedule>();
			
				for(EventPosition eventPosition : allEventPositions){
					List<Employee> employees = eventPosition.getPosition().getScheduledEmployees();
					for(Employee employee : employees){
						if(employee!=null){
								
							Schedule schedule = new Schedule();
							schedule.setActive(true);
							Integer schId = employee.getSchedule().getId();
							schedule.setId(schId);
							schedule.setEventPositionId(eventPosition.getId());
							schedule.setEmployee(employee);
							schedule.setPosition(eventPosition.getPosition());
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
							List<String> receipients = new ArrayList<String>();
							Map<Object,Object> attributes = PopulateEmailAttributes.populateUpdatedLocationEmailAttributes(websiteUrl, tenant, event, eventPosition, employee, eventPosition.getPosition());//populateEmailAttributes(event,eventPosition,employee,eventPosition.getPosition());
							receipients.add(employee.getEmail());
							schedule.setEmailAttributes(attributes);
							attributes.put("scheduleId",schedule.getId());
							updateSchedules.add(schedule);
							getScheduleService().updateSchedules(event, eventPosition, new ArrayList<Schedule>(),new ArrayList<Schedule>(),updateSchedules);
							getSheduleEmployeeSender().scheduleEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(), 
									tenant.getEmailSender(), "resend schedule");
							//getSender().resendScheduleEmployee(resendScheduleMessage, attributes, receipients,tenant.getEmailSender());
						}
						else
							continue;
					}
				}
		
		}
		
		Iterator<FacesMessage> messageList =  FacesContext.getCurrentInstance().getMessages();
		
		while(messageList.hasNext()){
			FacesMessage facesMessage = messageList.next();
			messageList.remove();
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("Event Updated Successfully");
		
		if(editEventView!=null && editEventView.equalsIgnoreCase("oldScheduleDetail")){
			return "/ui/admin/schedule/oldScheduleDetail?faces-redirect=true&eventId="+event.getId();
		}else if(editEventView!=null && editEventView.equalsIgnoreCase("scheduleDetail")){
			return "/ui/admin/schedule/scheduleDetail?faces-redirect=true&eventId="+event.getId();
		}
		else if(editEventView!=null && editEventView.equalsIgnoreCase("roster")){
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			String date = dateformat.format(event.getStartDate());
			// remove validation success messages
			
			return "/ui/admin/event/roster?faces-redirect=true&includeViewParams=true&startDate="+date+"&endDate="+date;
		}
		else
			return "/ui/admin/event/eventHome?faces-redirect=true";
	}
	
	public void eventLocationChange(ValueChangeEvent valueChangeEvent){
		Location oldLocation = (Location)valueChangeEvent.getOldValue();
		Location newLocation = (Location)valueChangeEvent.getNewValue();
		eventPropertyChanged = !newLocation.equals(oldLocation);
		
		
	}
	/*public void selectEventStartDate(){
		Date stime = this.event.getStartDate();
		
		if(customUser.isSalesPerson()){
    		if(stime !=null && DateUtil.dateRange(new Date(),stime)<2)
    			
    			FacesUtils.addErrorMessage("addEventForm:startdate","Please select a future date");
    	}
	}
	public void selectEventStartDate(SelectEvent event){
		Date stime = this.event.getStartDate();
		if(customUser.isSalesPerson()){
    		if(stime !=null && DateUtil.dateRange(new Date(),stime)<2)
    			
    			FacesUtils.addErrorMessage("addEventForm:startdate","Please select a future date");
    	}
	}
	*/
	
	/*public void selectEventEndDate(SelectEvent event){
		if(this.event.getStartDate().getTime()>this.event.getEndDate().getTime())
			FacesUtils.addErrorMessage("addEventForm:enddate","Enter a valid end date");
	}*/
	/*public void selectEventStartTime(){
		Date stime = this.event.getStartTime();
    	Date etime = this.event.getEndTime();
    	
    	if(stime !=null && etime !=null && DateUtil.compareTimeOnly(stime,etime)==0){
    		
    		FacesUtils.addErrorMessage("addEventForm:starttime","Start Time and End Time can't be same");
    	}else{
    		List<String> updateIds = new ArrayList<String>();
    		updateIds.add("addEventForm:startTimeMsg");
    		updateIds.add("addEventForm:endTimeMsg");
    		RequestContext.getCurrentInstance().update(updateIds);
    	}
			
    	
	}*/
	public void dressCodeChange(ValueChangeEvent  valueChangeEvent){
		String oldValue = (String)valueChangeEvent.getOldValue();
		String newValue = (String)valueChangeEvent.getNewValue();
		if(event.getStatus()!=null && event.getStatus().equalsIgnoreCase("published"))
			eventPropertyChanged = !oldValue.equalsIgnoreCase(newValue);
		 
	}
	
	/*public void selectEventEndTime(){
		Date stime = this.event.getStartTime();
    	Date etime = this.event.getEndTime();
    	if(stime !=null && etime !=null){
    		if(DateUtil.compareTimeOnly(stime,etime)==0){
    			FacesUtils.addErrorMessage("addEventForm:endtime","Start Time and End Time can't be same");
    		}else{
    			String time = DateUtil.getTimeOnly(etime,"hh:mm a");
    			if(DateUtil.compareTimeOnly(stime,etime)>0){
    				Date endDate = DateUtil.incrementByDay(event.getStartDate(),1);
    				event.setEndDate(endDate);
    				
    				FacesUtils.addInfoMessage("addEventForm:growl","Event ends on next day at "+time);
    			}else{
    				Date endDate = event.getStartDate();
    				event.setEndDate(endDate);
    				FacesUtils.addInfoMessage("addEventForm:growl","Event ends on same day at "+time);
    			}
    		}
    	}
    	else{
    		List<String> updateIds = new ArrayList<String>();
    		updateIds.add("addEventForm:startTimeMsg");
    		updateIds.add("addEventForm:endTimeMsg");
    		RequestContext.getCurrentInstance().update(updateIds);
    	}
		
    		
	}*/
	public void selectEventPositionStartTime(){
		Integer rowIndexId = Integer.parseInt(FacesUtils.getRequestParameter("currentRowIndex"));
		EventPosition currentEventPosition = availableEventPosition.get(rowIndexId) ;
		this.verifyEventPositionStartTime(currentEventPosition);
			
	}
	private boolean verifyEventPositionStartTime(EventPosition currentEventPosition){
			Date stime = currentEventPosition.getStartTime();
	    	Date etime = currentEventPosition.getEndTime();
			if(stime !=null && etime !=null && DateUtil.compareDateAndTime(stime,etime)>=0){
				FacesUtils.addErrorMessage(":growl","please choose correct Start Time and End Time, to enter midnight shifts choose an end time on the next day");
				return false;
			}
			
		return true;
	}
	public void selectEventPositionEndTime(){
		Integer rowIndexId = Integer.parseInt(FacesUtils.getRequestParameter("currentRowIndex"));
		EventPosition currentEventPosition = availableEventPosition.get(rowIndexId) ;
		this.verifyEventPositionEndTime(currentEventPosition);
			
			
	}
	private void verifyEventPositionEndTime(EventPosition currentEventPosition){
		Date stime = currentEventPosition.getStartTime();
    	Date etime = currentEventPosition.getEndTime();
		if(stime !=null && etime !=null && DateUtil.compareDateAndTime(stime,etime)>=0)
			FacesUtils.addErrorMessage(":growl","Error, please choose correct Start Time and End Time, to enter midnight shifts choose an end time on the next day");
	
	}
	
	public List<Position> getSelectedPositions() {
		return selectedPositions;
	}
	public void setSelectedPositions(List<Position> selectedPositions) {
		this.selectedPositions = selectedPositions;
	}
	public EventSupport getEventSupport() {
		return eventSupport;
	}
	public void setEventSupport(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public List<Employee> getOwnerList() {
		return ownerList;
	}
	public void setOwnerList(List<Employee> ownerList) {
		this.ownerList = ownerList;
	}
	public List<Employee> getCoordinatorList() {
		return coordinatorList;
	}
	public void setCoordinatorList(List<Employee> coordinatorList) {
		this.coordinatorList = coordinatorList;
	}

	
	
	/*****************************************************************************************************************************************************************************
	 * **************************************************************************************************************************************************************************
	 * new code for schedule cloud
	 */
	
	
	private Date createShiftDateTime(Date date, Date time){
		DateFormat timingFormat = new SimpleDateFormat("hh:mm a", Locale.US);
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		String event_date = dateFormat.format(date);
		String event_time = timingFormat.format(time);
		DateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
		Date datetime = null;
		try {
			datetime = dateTimeFormat.parse(event_date+" "+event_time);
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return datetime;
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
	/*public String editEventPositionNotes(Integer eventPositionId){
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
	}*/
	public String addEventPosition(){
		// TO do addEventPosition validation and display error in case of no position selected
		if(event!=null){
			
			event.setEventRegion(regionBean.getSelectedRegion());
			if(availableEventPosition!=null){
				List<EventPosition> assignEventPositions = new ArrayList<EventPosition>();
				for(int i=0;i<availableEventPosition.size();i++)
				{	EventPosition eventPosition = availableEventPosition.get(i);
					if(eventPosition.getReqdNumber()!=null && eventPosition.getReqdNumber()>0){
						if(!this.verifyEventPositionStartTime(eventPosition))
							return null;
						else{
						
							eventPosition.setEvent(this.event);
							eventPosition.setCompanyId(this.tenant.getId());
							eventPosition.setDisplayOrder(i);
							eventPosition.setActive(true);
							eventPosition.setRegion(regionBean.getSelectedRegion());
							assignEventPositions.add(eventPosition);
							
						}
					}
				}
				event.setAssignedEventPosition(assignEventPositions);
			}
			Employee salesPerson = event.getSalesPerson();
			if(salesPerson!=null)
				event.setCreatedBy(salesPerson.getId());
			else
				event.setCreatedBy(customUser.getEmp_id());
			if(action.equalsIgnoreCase("editevent")&&event.getId()!=null){
				
				getEventService().editEvent(event);
				
			}
			else if(action.equalsIgnoreCase("addevent")){
				if(event.getId()!=null){
					getEventService().editEvent(event);
					
				}else{
					event = getEventService().addEvent(event);
					//event.setId(eventId);
				}
				
			}
			
		}
		eventSupport.setEvent(event);
		
		if(customUser.isSalesPerson()){
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesUtils.addInfoMessage("Event and Positions Saved Successfully");
			return "eventHome?faces-redirect=true";
		}
		else
			return "scheduleEmployee?faces-redirect=true";
	}
	public String deleteEvent(){
		if(event.getStatus().equalsIgnoreCase("active")){
			getEventService().deleteEventAndPositions(this.event,this.tenant);
			Iterator<FacesMessage> messageList =  FacesContext.getCurrentInstance().getMessages();
			
			while(messageList.hasNext()){
				FacesMessage facesMessage = messageList.next();
				messageList.remove();
			}
			FacesUtils.addInfoMessage("event deleted Successfully");
			return "eventHome?faces-redirect=true";
		}else if(event.getStatus().equalsIgnoreCase("published")){
			
			List<Schedule> deletedSchedules = getEventService().deletePublishedEvent(this.event,this.tenant);
			if(deletedSchedules!=null && deletedSchedules.size()>0){
				
				this.sendDeleteEmail(deletedSchedules);
				Iterator<FacesMessage> messageList =  FacesContext.getCurrentInstance().getMessages();
				
				while(messageList.hasNext()){
					FacesMessage facesMessage = messageList.next();
					messageList.remove();
				}
				
			}
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesUtils.addInfoMessage("event deleted Successfully");
			return "eventHome?faces-redirect=true";
		}else{
			
		}
		return "eventHome?faces-redirect=true";
		
	}
	
	private void sendDeleteEmail(List<Schedule> schedules){
		String websiteUrl = getApplicationData().getServer_url();
		for(Schedule schedule: schedules){
			EventPosition scheduledEventPosition = schedule.getEventPosition();
			Position scheduledPosition = scheduledEventPosition.getPosition();
			Employee employee = schedule.getEmployee();
			Map<Object,Object> attributes = PopulateEmailAttributes.populateEmailAttributes(websiteUrl, tenant, event, scheduledEventPosition, employee, scheduledPosition);//populateEmailAttributes(event,scheduledEventPosition,employee,scheduledPosition);
			attributes.put("emailsubject","YOU ARE NO LONGER SCHEDULED FOR " + tenant.getName()+", "+event.getLocation().getName()+", "+event.getName());
			attributes.put("priority", "HIGH");
			attributes.put("scheduleId",schedule.getId());
			List<String> receipients = new ArrayList<String>();
			receipients.add(employee.getEmail());
			getSheduleEmployeeSender().scheduleEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(), 
					tenant.getEmailSender(), "delete schedule");
			//getSender().deleteScheduleEmployee(scheduleMessage, attributes, receipients,tenant.getEmailSender());
		}
	}
	public void displayAddEventPositionDialog(ActionEvent actionEvent){
		Date shiftStartDate = this.createShiftDateTime(event.getStartDate(),event.getStartTime());
		Date shiftEndDate = this.createShiftDateTime(event.getEndDate(),event.getEndTime());
		eventPosition = new EventPosition(event,tenant,shiftStartDate,shiftEndDate);
		eventPosition.setCompanyId(tenant.getId());
		if(event.getAssignedEventPosition()!=null){
			Integer displayOrder = event.getAssignedEventPosition().size()+1;
			this.eventPosition.setDisplayOrder(displayOrder);
		}
		
	}
	public void cancelNewEventPositionDialog(ActionEvent actionEvent){
		eventPosition = null;
		
	}
	public void closeNewEventPositionDialog(){
		eventPosition = null;
	}
	public void addNewEventPosition(ActionEvent actionEvent){
		eventPosition.setRegion(regionBean.getSelectedRegion());
		Integer evePosId = getEventService().saveEventPosition(eventPosition);
		this.eventPosition.setId(evePosId);
		
		event.getAssignedEventPosition().add(eventPosition);
	}
	public List<EventPosition> getAvailableEventPosition() {
		return availableEventPosition;
	}

	public void setAvailableEventPosition(List<EventPosition> availableEventPosition) {
		this.availableEventPosition = availableEventPosition;
	}



	public EventPosition getEventPosition() {
		return eventPosition;
	}



	public void setEventPosition(EventPosition eventPosition) {
		this.eventPosition = eventPosition;
	}



	public List<Employee> getAllAvailableEmployee() {
		return allAvailableEmployee;
	}



	public void setAllAvailableEmployee(List<Employee> allAvailableEmployee) {
		this.allAvailableEmployee = allAvailableEmployee;
	}
	
	private List<Employee> selectedEmployees;

	public List<Employee> getSelectedEmployees() {
		return selectedEmployees;
	}



	public void setSelectedEmployees(List<Employee> selectedEmployees) {
		this.selectedEmployees = selectedEmployees;
	}



	public Tenant getTenant() {
		return tenant;
	}



	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}

	public List<Employee> getSalesList() {
		return salesList;
	}

	public void setSalesList(List<Employee> salesList) {
		this.salesList = salesList;
	}
	
	
}
