package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlRootElement;


import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.primefaces.model.DefaultScheduleEvent;

import com.onetouch.view.util.DateUtil;
@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Availability  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String title="Available";
	private Date availDate; // actual available date
	private boolean newAvail;
	private Date startDate; // view purpose 
	private Date endDate; // view purpose
	private Date startTime;// actual start time
	private Date endTime;// actual end time
	
	
	private Tenant tenant;
	private Employee employee;
	
	private boolean repeatWeekMonday;
	private boolean repeatWeekTuesday;
	private boolean repeatWeekWednesday;
	private boolean repeatWeekThursday;
	private boolean repeatWeekFriday;
	private boolean repeatWeekSaturday;
	private boolean repeatWeekSunday;
	private boolean allday=false;
	private boolean selected;
	public Availability() {
		
	}
	public Availability(Tenant tenant, Employee employee) {
		
		this.tenant = tenant;
		this.employee = employee;
		DateFormat timingFormat = new SimpleDateFormat("hh:mm a", Locale.US);
		String sTime = 6+":00"+" AM";
		String eTime = 11+":59"+" PM";
		try {
			startTime = timingFormat.parse(sTime);
			endTime = timingFormat.parse(eTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public boolean isSelected() {
		return repeatWeekMonday||repeatWeekTuesday||repeatWeekWednesday||repeatWeekThursday||repeatWeekFriday||repeatWeekSaturday||repeatWeekSunday;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getTitle() {
		/*Date testStartTime = DateUtil.getDate("00:00 AM","hh:mm a");
		Date testEndTime = DateUtil.getDate("11:59 PM","hh:mm a");
		int a = DateUtil.compareTimeOnly(this.getStartTime(),testStartTime);
		int b = DateUtil.compareTimeOnly(this.getEndTime(),testEndTime);
		
		if(a==0 && b==0)			
			this.title = "Available All Day";
		else
			this.title = "Available";*/
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getStartTime() {
		/*SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
	    try {
	    	if(sTime!=null)
	    		startTime = df.parse(sTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		/*SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
	    try {
	    	if(eTime!=null)
	    		endTime = df.parse(eTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return endTime;
		
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public boolean isRepeatWeekMonday() {
		return repeatWeekMonday;
	}
	public void setRepeatWeekMonday(boolean repeatWeekMonday) {
		this.repeatWeekMonday = repeatWeekMonday;
	}
	public boolean isRepeatWeekTuesday() {
		return repeatWeekTuesday;
	}
	public void setRepeatWeekTuesday(boolean repeatWeekTuesday) {
		this.repeatWeekTuesday = repeatWeekTuesday;
	}
	public boolean isRepeatWeekWednesday() {
		return repeatWeekWednesday;
	}
	public void setRepeatWeekWednesday(boolean repeatWeekWednesday) {
		this.repeatWeekWednesday = repeatWeekWednesday;
	}
	public boolean isRepeatWeekThursday() {
		return repeatWeekThursday;
	}
	public void setRepeatWeekThursday(boolean repeatWeekThursday) {
		this.repeatWeekThursday = repeatWeekThursday;
	}
	public boolean isRepeatWeekFriday() {
		return repeatWeekFriday;
	}
	public void setRepeatWeekFriday(boolean repeatWeekFriday) {
		this.repeatWeekFriday = repeatWeekFriday;
	}
	public boolean isRepeatWeekSaturday() {
		return repeatWeekSaturday;
	}
	public void setRepeatWeekSaturday(boolean repeatWeekSaturday) {
		this.repeatWeekSaturday = repeatWeekSaturday;
	}
	public boolean isRepeatWeekSunday() {
		return repeatWeekSunday;
	}
	public void setRepeatWeekSunday(boolean repeatWeekSunday) {
		this.repeatWeekSunday = repeatWeekSunday;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Date getAvailDate() {
		return availDate;
	}
	public void setAvailDate(Date availDate) {
		this.availDate = availDate;
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
	public boolean isNewAvail() {
		return newAvail;
	}
	public void setNewAvail(boolean newAvail) {
		this.newAvail = newAvail;
	}
	public boolean isAllday() {
		return allday;
	}
	public void setAllday(boolean allday) {
		this.allday = allday;
	}
	
	
}
