package com.onetouch.view.bean.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.springframework.beans.BeanUtils;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
@ManagedBean(name="repeatAvailability")
@ApplicationScoped
public class RepeatAvailabilityBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<Availability> getDailyRepeatAvailability(int days, Availability availability,Employee employee,Tenant tenant){
		List<Integer> daysList = this.selectedDaysList(availability);
		List<Availability> selectedAvailabilities = new ArrayList<Availability>();
		DateTime availStartdate = availability.getStartDate()==null?null:new DateTime(availability.getStartDate());
		
		for(int i=0;i<=days;i++){
			Integer selectedDay = availStartdate.getDayOfWeek();
			if(daysList.contains(selectedDay)){
				Availability tempAvailability = new Availability();
				if(availability.isAllday())
					tempAvailability.setAllday(true);
				tempAvailability.setTitle(availability.getTitle());
				tempAvailability.setAvailDate(availStartdate.toDate());
				tempAvailability.setStartTime(availability.getStartTime());
				tempAvailability.setEndTime(availability.getEndTime());
				tempAvailability.setEmployee(employee);
				tempAvailability.setTenant(tenant);
				selectedAvailabilities.add(tempAvailability);
			}
			availStartdate = availStartdate.plus(Period.days(1));
			
		}
		
		return selectedAvailabilities;
	}
	private List<Integer> selectedDaysList(Availability availability) {
		List<Integer> daysList = new ArrayList<Integer>();
		if(availability.isRepeatWeekMonday())
			daysList.add(1);
		if(availability.isRepeatWeekTuesday())
			daysList.add(2);
		if(availability.isRepeatWeekWednesday())
			daysList.add(3);
		if(availability.isRepeatWeekThursday())
			daysList.add(4);
		
		
		if(availability.isRepeatWeekFriday())
			daysList.add(5);
		if(availability.isRepeatWeekSaturday())
			daysList.add(6);
		if(availability.isRepeatWeekSunday())
			daysList.add(7);
		
		return daysList;
	}
	public List<Availability> getDailyAvailability(int days, Availability availability,Employee employee,Tenant tenant){
			
			DateTime availStartdate = availability.getStartDate()==null?null:new DateTime(availability.getStartDate());
			List<Availability> allRepeatAvail = new ArrayList<Availability>();
			
			for(int i=0;i<=days;i++){
				Availability tempAvailability = new Availability();
				
				tempAvailability.setTitle(availability.getTitle());
				tempAvailability.setAvailDate(availStartdate.toDate());
				if(availability.isAllday())
					tempAvailability.setAllday(true);
				tempAvailability.setStartTime(availability.getStartTime());
				tempAvailability.setEndTime(availability.getEndTime());
				tempAvailability.setEmployee(employee);
				tempAvailability.setTenant(tenant);
				allRepeatAvail.add(tempAvailability);
				//increment by one for daily
				availStartdate = availStartdate.plus(Period.days(1));
				
			}
			return allRepeatAvail;
		
		
	}
	
	
}
