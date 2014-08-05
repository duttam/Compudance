package com.onetouch.view.bean.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

import com.onetouch.view.util.DateUtil;

@ManagedBean(name="dateBean")
@ViewScoped
public class DateBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Date> eventTimes;
	private List<Date> shiftTimes;
	private DateFormat dateFormat;
	private DateFormat timingFormat;
	private List<Date> allDates;
	private String[] months ;
	@PostConstruct
	public void init(){
		timingFormat = new SimpleDateFormat("hh:mm a", Locale.US);
		createAllDates(timingFormat); // for employee availability dropdown
		eventTimes = new ArrayList<Date>();
		try {
			String sTime = null;
			sTime = 12+":00"+" AM";
			Date sd = timingFormat.parse(sTime);
			eventTimes.add(sd);
			//System.out.println(sTime);
			sTime = 12+":30"+" AM";
			
			Date sd1 = timingFormat.parse(sTime);
			eventTimes.add(sd1); 
			//System.out.println(sTime);
			for(int i=1;i<12;i++){
				sTime = i+":00"+" AM";
				Date d = timingFormat.parse(sTime);
				eventTimes.add(d);
				//System.out.println(sTime);
				sTime = i+":30"+" AM";
				d = timingFormat.parse(sTime);
				eventTimes.add(d);
				//System.out.println(sTime);
			}
			//Date sd2 = timingFormat.parse(sTime);
			//eventTimes.add(sd2);
			sTime = 12+":00"+" PM";
			Date sd3 = timingFormat.parse(sTime);
			eventTimes.add(sd3);
			//System.out.println(sTime);
			sTime = 12+":30"+" PM";
			sd3 = timingFormat.parse(sTime);
			eventTimes.add(sd3);
			//System.out.println(sTime);
			for(int i=1;i<12;i++){
				sTime = i+":00"+" PM";
				Date d = timingFormat.parse(sTime);
				eventTimes.add(d);
				//System.out.println(sTime);
				sTime = i+":30"+" PM";
				d = timingFormat.parse(sTime);
				eventTimes.add(d);
				//System.out.println(sTime);
			}
			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
		
	}
	
	

	public String[] getMonths() {
		return months;
	}



	public void setMonths(String[] months) {
		this.months = months;
	}



	private void createAllDates(DateFormat timingFormat2) {
		allDates = new ArrayList<Date>();
		try {
			String sTime = null;
			sTime = 12+":00"+" AM";
			Date sd = timingFormat.parse(sTime);
			allDates.add(sd);
			sTime = 12+":30"+" AM";
			Date sd1 = timingFormat.parse(sTime);
			allDates.add(sd1); 
			for(int i=1;i<12;i++){
				sTime = i+":00"+" AM";
				Date d = timingFormat.parse(sTime);
				allDates.add(d);
				sTime = i+":30"+" AM";
				d = timingFormat.parse(sTime);
				allDates.add(d);
			}
			
			
			sTime = 12+":00"+" PM";
			Date sd3 = timingFormat.parse(sTime);
			allDates.add(sd3);
			sTime = 12+":30"+" PM";
			sd3 = timingFormat.parse(sTime);
			allDates.add(sd3);
			for(int i=1;i<12;i++){
				sTime = i+":00"+" PM";
				Date d = timingFormat.parse(sTime);
				allDates.add(d);
				sTime = i+":30"+" PM";
				d = timingFormat.parse(sTime);
				allDates.add(d);
			}
			
			sTime = 11+":59"+" PM";
			Date date = timingFormat.parse(sTime);
			allDates.add(date);
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/*public void createShiftTime(){
		shiftTimes = new ArrayList<Date>();
		try {
			String sTime = null;
			sTime = 12+":00"+" AM";
			Date sd = timingFormat.parse(sTime);
			shiftTimes.add(sd);
			sTime = 12+":30"+" AM";
			Date sd1 = timingFormat.parse(sTime);
			shiftTimes.add(sd1); 
			for(int i=1;i<12;i++){
				sTime = i+":00"+" AM";
				Date d = timingFormat.parse(sTime);
				shiftTimes.add(d);
				sTime = i+":30"+" AM";
				d = timingFormat.parse(sTime);
				shiftTimes.add(d);
			}
			Date sd2 = timingFormat.parse(sTime);
			shiftTimes.add(sd2);
			sTime = 12+":30"+" PM";
			Date sd3 = timingFormat.parse(sTime);
			shiftTimes.add(sd3);
			for(int i=1;i<12;i++){
				sTime = i+":00"+" PM";
				Date d = timingFormat.parse(sTime);
				shiftTimes.add(d);
				sTime = i+":30"+" PM";
				d = timingFormat.parse(sTime);
				shiftTimes.add(d);
			}
			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	public void createShiftDateAndTime(Date date1,Date date2){
		dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		shiftTimes = new ArrayList<Date>();
		if(date1!=null){
			String sdate = dateFormat.format(date1);
			
			this.createShiftDateAndTime(sdate);
			Date edate = DateUtil.incrementByDay(date1, 1);
			this.createShiftDateAndTime(dateFormat.format(edate));
		}
		/*if(DateUtil.compareDateAndTime(date2, date1)>0 && date2!=null){
			String edate = dateFormat.format(date2);
			
			this.createShiftDateAndTime(edate);
		}*/
	}
	
	public void createShiftDateAndTime(String date){
		DateFormat timingFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
		try {
			String sTime = null;
			sTime = date+" "+12+":00"+" AM";
			Date sd = timingFormat.parse(sTime);
			shiftTimes.add(sd);
			sTime = date+" "+12+":30"+" AM";
			Date sd1 = timingFormat.parse(sTime);
			shiftTimes.add(sd1); 
			for(int i=1;i<12;i++){
				sTime = date+" "+i+":00"+" AM";
				Date d = timingFormat.parse(sTime);
				shiftTimes.add(d);
				sTime = date+" "+i+":30"+" AM";
				d = timingFormat.parse(sTime);
				shiftTimes.add(d);
			}
			//Date sd2 = timingFormat.parse(sTime);
			//shiftTimes.add(sd2);
			sTime = date+" "+12+":00"+" PM";
			Date sd3 = timingFormat.parse(sTime);
			shiftTimes.add(sd3);
			sTime = date+" "+12+":30"+" PM";
			sd3 = timingFormat.parse(sTime);
			shiftTimes.add(sd3);
			for(int i=1;i<12;i++){
				sTime = date+" "+i+":00"+" PM";
				Date d = timingFormat.parse(sTime);
				shiftTimes.add(d);
				sTime = date+" "+i+":30"+" PM";
				d = timingFormat.parse(sTime);
				shiftTimes.add(d);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<Date> getEventTimes() {
		return eventTimes;
	}
	public void setEventTimes(List<Date> eventTimes) {
		this.eventTimes = eventTimes;
	}
	public List<Date> getShiftTimes() {
		return shiftTimes;
	}
	public void setShiftTimes(List<Date> shiftTimes) {
		this.shiftTimes = shiftTimes;
	}

	public List<Date> getAllDates() {
		return allDates;
	}

	public void setAllDates(List<Date> allDates) {
		this.allDates = allDates;
	}
	
	
}
