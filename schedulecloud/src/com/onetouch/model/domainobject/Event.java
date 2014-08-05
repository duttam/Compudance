package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.view.util.DateUtil;


public class Event implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private Employee owner;
	private Employee coordinator;
	private Integer createdBy; // employee that created the event
	private Employee salesPerson;
	private String hostname;
	//private String eventDate;
	private Date startDate;
	private Date endDate;
	private Date startTime;// the sql datatype is datetime not timestamp, as timestamp preserves timezone.
	private Date endTime;
	private String dressCode;
	private String notes;
	private String description;
	private String shiftType;
	private Location location = new Location(); // what location;
	private String status;
	private Tenant tenant;
	private String timezone;// same timezone as location
	
	private List<Position> reqdPositions = new ArrayList<Position>(); // positions required for this event
	
	private List<Employee> allEmployees;
	private boolean pendingSchedule;
	private List<EventPosition> assignedEventPosition;
	private boolean multidayevent;
	private String eventMonth;
	
	// for reporting purpose
	private List<Employee> allScheduledEmps;
	private BigDecimal totalForecastLaborCost;
	private List<Employee> allSignInOutEmps;
	private BigDecimal totalActualLaborCost;
	private String styleColor;
	private Integer guestCount;
	private String salesPersonNotes;
	private EventType eventType;
	private Region eventRegion;
	
	private Integer positionRequiredCount;
	private Integer positionStaffedCount;
	private Integer positionOpenCount;
	private Integer pendingNotificationCount;
	private String statusFlag; // red or yellow flag
	private String colorCode;
	public Event() {
		
	}
	
	public Event(Integer id) {
		
		this.id = id;
	}
	
	public Event(Tenant tenant) {
		this.tenant = tenant;
		DateFormat timingFormat = new SimpleDateFormat("hh:mm a", Locale.US);
		String sTime = 6+":00"+" AM";
		String eTime = 11+":30"+" PM";
		try {
			startTime = timingFormat.parse(sTime);
			endTime = timingFormat.parse(eTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Employee getOwner() {
		return owner;
	}
	public void setOwner(Employee owner) {
		this.owner = owner;
	}
	public Employee getCoordinator() {
		return coordinator;
	}
	public void setCoordinator(Employee coordinator) {
		this.coordinator = coordinator;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getDressCode() {
		return dressCode;
	}
	public void setDressCode(String dressCode) {
		this.dressCode = dressCode;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
		
	public List<Position> getReqdPositions() {
		return reqdPositions;
	}
	public void setReqdPositions(List<Position> reqdPositions) {
		this.reqdPositions = reqdPositions;
	}
		
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
		
	
	
	public String getShiftType() {
		return shiftType;
	}

	public void setShiftType(String shiftType) {
		this.shiftType = shiftType;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	/*public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}*/

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	public List<EventPosition> getAssignedEventPosition() {
		return assignedEventPosition;
	}

	public void setAssignedEventPosition(List<EventPosition> assignedEventPosition) {
		this.assignedEventPosition = assignedEventPosition;
	}
	
	public boolean isPendingSchedule() {
		return pendingSchedule;
	}

	public void setPendingSchedule(boolean pendingSchedule) {
		this.pendingSchedule = pendingSchedule;
	}
	
	public boolean isSpecialEvent(){
		if(startTime !=null && endTime !=null && startTime.getTime()>=endTime.getTime()){
    		int days = DateUtil.dateRange(startDate,endDate);
    		if(days==1){
    			return true;
    		}
    		else
    			return false;
		}else
			return false;
	}
	
	public boolean isMultidayevent() {
		return multidayevent;
	}

	public void setMultidayevent(boolean multidayevent) {
		this.multidayevent = multidayevent;
	}
	
	/*public List<String> getEventDates() {
		
		eventDates = new ArrayList<String>();
		
		DateTime  eventStartDate = this.getStartTime()==null?null:new DateTime(this.getStartTime());
		DateTime eventEndDate = this.getEndTime()==null?null:new DateTime(this.getEndTime());
		LocalDate startDate = eventStartDate.toLocalDate();
		LocalDate endDate = eventEndDate.toLocalDate();
		
		List<LocalDate> dates = new ArrayList<LocalDate>();
		int days = Days.daysBetween(startDate, endDate).getDays();
		for (int i=0; i < days; i++) {
		    LocalDate d = startDate.withFieldAdded(DurationFieldType.days(), i);
		    dates.add(d);
		}
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		for(LocalDate ld : dates){
			Date temp = ld.toDateTimeAtStartOfDay().toDate();
			eventDates.add(format.format(temp));
			
		}
		
		if(eventDates.size()==0){
			eventDates.add(format.format(this.startTime));
		}
		for(LocalDate ld : dates){
			eventDates.add(ld.toDateTimeAtStartOfDay().toDate());
		}
		
		if(eventDates.size()==0)			
			eventDates.add(this.startTime);
			
		return eventDates;
	}*/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	public String getEventMonth() {
		return eventMonth;
	}

	public void setEventMonth(String eventMonth) {
		this.eventMonth = eventMonth;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public BigDecimal getTotalForecastPay() {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getTotalActualPay() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Employee> getAllScheduledEmps() {
		return allScheduledEmps;
	}

	public void setAllScheduledEmps(List<Employee> allScheduledEmps) {
		this.allScheduledEmps = allScheduledEmps;
	}

	public List<Employee> getAllSignInOutEmps() {
		return allSignInOutEmps;
	}

	public void setAllSignInOutEmps(List<Employee> allSignInOutEmps) {
		this.allSignInOutEmps = allSignInOutEmps;
	}

	public BigDecimal getTotalForecastLaborCost() {
		return totalForecastLaborCost;
	}

	public void setTotalForecastLaborCost(BigDecimal totalForecastLaborCost) {
		this.totalForecastLaborCost = totalForecastLaborCost;
	}

	public BigDecimal getTotalActualLaborCost() {
		return totalActualLaborCost;
	}

	public void setTotalActualLaborCost(BigDecimal totalActualLaborCost) {
		this.totalActualLaborCost = totalActualLaborCost;
	}
	
	public BigDecimal getOverUnder(){
		return totalActualLaborCost.subtract(totalForecastLaborCost);
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public String getStyleColor() {
		if(pendingSchedule)
			return "pendingEvent";
		else
			return "";
		 
	}
	
	public boolean isDisplayConfidentialNote(){
		CustomUser customUser=null;
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken!=null && authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
			if(customUser.isSalesPerson())
				return true;
			else{
				if(this.salesPersonNotes!=null && this.salesPersonNotes.length()>0)
					return true;
				else
					return false;
			}
		}
		return false;
	}
	public void setStyleColor(String styleColor) {
		this.styleColor = styleColor;
	}

	public Employee getSalesPerson() {
		return salesPerson;
	}

	public void setSalesPerson(Employee salesPerson) {
		this.salesPerson = salesPerson;
	}

	public Integer getGuestCount() {
		return guestCount;
	}
	public void setGuestCount(Integer guestCount) {
		this.guestCount = guestCount;
	}

	public String getSalesPersonNotes() {
		return salesPersonNotes;
	}

	public void setSalesPersonNotes(String salesPersonNotes) {
		this.salesPersonNotes = salesPersonNotes;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Region getEventRegion() {
		return eventRegion;
	}

	public void setEventRegion(Region eventRegion) {
		this.eventRegion = eventRegion;
	}

	public Integer getPositionRequiredCount() {
		return positionRequiredCount;
	}

	public void setPositionRequiredCount(Integer positionRequiredCount) {
		this.positionRequiredCount = positionRequiredCount;
	}

	public Integer getPositionStaffedCount() {
		return positionStaffedCount;
	}

	public void setPositionStaffedCount(Integer positionStaffedCount) {
		this.positionStaffedCount = positionStaffedCount;
	}

	public Integer getPositionOpenCount() {
		return positionOpenCount;
	}

	public void setPositionOpenCount(Integer positionOpenCount) {
		this.positionOpenCount = positionOpenCount;
	}

	public String getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(String statusFlag) {
		this.statusFlag = statusFlag;
	}
	

	public Integer getPendingNotificationCount() {
		return pendingNotificationCount;
	}

	public void setPendingNotificationCount(Integer pendingNotificationCount) {
		this.pendingNotificationCount = pendingNotificationCount;
	}

	public String getEventDate() {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    	String eventDate = dateformat.format(this.startDate);
    	
    	return eventDate;
		
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	
	
		
}
