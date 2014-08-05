package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.onetouch.view.util.DateUtil;

public class EventPosition implements Serializable{

	private Integer id;
	private Integer event_id;
	private Integer position_id;
	private Integer reqdNumber=null;
	private Integer newReqdNumber;
	private Date startTime;
	private Date endTime;
	private Integer companyId;
	private Integer displayOrder;
	private boolean active;
	private Event event;
	private Position position;
	private String offerNotes;
	private Tenant tenant;
	private Date startDate;
	private Date endDate;
	private List<Employee> reqdAvailabilityEmployees;// employees linked to position which in turn linked to eventposition , get availability for these employees in case of special event
	private boolean shiftTimeChanged;
	private Date oldStartTime;
	private Date oldEndTime;
	private String notes = "";
	private Region region;
	private String scheduleStatus; // report purspose only
	private Integer offerCount;
	
	public EventPosition() {
		
	}
	public EventPosition(Event event) {
		
		this.event = event;
	}
	public EventPosition(Event event, Tenant tenant) {
		
		this.event = event;
		this.tenant = tenant;
		DateFormat timingFormat = new SimpleDateFormat("hh:mm a", Locale.US);
		Date eventStartDate = event.getStartDate();
		Date eventEndDate = event.getEndDate();
		String sTime = 6+":00"+" AM";
		String eTime = 11+":30"+" PM";
		try {
			Date shiftStartTime = timingFormat.parse(sTime);
			startTime = DateUtil.getDateAndTime(eventStartDate, shiftStartTime);
			Date shiftEndTime = timingFormat.parse(eTime);
			endTime = DateUtil.getDateAndTime(eventEndDate, shiftEndTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public EventPosition(Event event, Tenant tenant,Date startTime,Date endTime) {
		
		this.event = event;
		this.tenant = tenant;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getEvent_id() {
		return event_id;
	}
	public void setEvent_id(Integer event_id) {
		this.event_id = event_id;
	}
	public Integer getPosition_id() {
		return position_id;
	}
	public void setPosition_id(Integer position_id) {
		this.position_id = position_id;
	}
	public Integer getReqdNumber() {
		return reqdNumber;
	}
	public void setReqdNumber(Integer reqdNumber) {
		this.reqdNumber = reqdNumber;
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
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public Integer getNewReqdNumber() {
		return newReqdNumber;
	}
	public void setNewReqdNumber(Integer newReqdNumber) {
		this.newReqdNumber = newReqdNumber;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
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
	public List<Employee> getReqdAvailabilityEmployees() {
		return reqdAvailabilityEmployees;
	}
	public void setReqdAvailabilityEmployees(
			List<Employee> reqdAvailabilityEmployees) {
		this.reqdAvailabilityEmployees = reqdAvailabilityEmployees;
	}
	public Date getOldStartTime() {
		return oldStartTime;
	}
	public void setOldStartTime(Date oldStartTime) {
		this.oldStartTime = oldStartTime;
	}
	public Date getOldEndTime() {
		return oldEndTime;
	}
	public void setOldEndTime(Date oldEndTime) {
		this.oldEndTime = oldEndTime;
	}
	public boolean isShiftTimeChanged() {
		if(startTime!=null && endTime!=null)
			return (DateUtil.compareDateAndTime(startTime, oldStartTime)!=0) || (DateUtil.compareDateAndTime(endTime,oldEndTime)!=0);
		
		return false;
	}
	public void setShiftTimeChanged(boolean shiftTimeChanged) {
		this.shiftTimeChanged = shiftTimeChanged;
	}
	
	public String getNotes() {
		if(notes==null)
			return "";
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	
	public String getScheduleStatus() {
		return scheduleStatus;
	}
	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}
	public String getEventPositionNotes(){
		if(notes!=null && notes.length()>0){
			return " ["+notes+"]";
		}
		return "";
	}
	
	public Integer getOfferCount() {
		return offerCount;
	}
	public void setOfferCount(Integer offerCount) {
		this.offerCount = offerCount;
	}
	public String getOfferNotes() {
		return offerNotes;
	}
	public void setOfferNotes(String offerNotes) {
		this.offerNotes = offerNotes;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventPosition other = (EventPosition) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
		
}
