package com.onetouch.view.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import com.onetouch.model.domainobject.Availability;


public class DateUtil {

	public static Date getDateAndTime(Date date, Date time){
		LocalDate localDate = new LocalDate(date.getTime());
		LocalTime localTime = new LocalTime(time.getTime());
		DateTime dateTime = localDate.toDateTime(localTime); // end date+time
		Calendar dateC = dateTime.toGregorianCalendar();
		return dateC.getTime();
	}
	
	public static int dateRange(Date date1, Date date2){
		DateTime StartdateTime = date1==null?null:new DateTime(date1);
		DateTime EnddateTime = date2==null?null:new DateTime(date2);
		if(StartdateTime!=null && EnddateTime!=null){
			int days = Days.daysBetween(StartdateTime.toDateMidnight() , EnddateTime.toDateMidnight() ).getDays() ;
			return days;
		}

		return 0;
	}
	
	public static int getHours(Date date1, Date date2){
		DateTime startTime = (date1==null)?null:new DateTime(date1);
		DateTime endTime = (date2==null)?null:new DateTime(date2);
		if(startTime!=null && endTime!=null){
			Period p = new Period(startTime, endTime);
			return  p.getHours();
		}
		
		return 0;
		
		
	}
	public static int getMinutes(Date date1, Date date2){
		DateTime startTime = (date1==null)?null:new DateTime(date1);
		DateTime endTime = (date2==null)?null:new DateTime(date2);
		if(startTime!=null && endTime!=null){
			Period p = new Period(startTime, endTime);
			return p.getMinutes();
		}
		
		return 0;
	}
	public static boolean isPreviousEvent(Date date1, Date date2){
		DateTime startTime = (date1==null)?null:new DateTime(date1);
		DateTime endTime = (date2==null)?null:new DateTime(date2);
		LocalDate firstDate = startTime.toLocalDate();
		LocalDate secondDate = endTime.toLocalDate();

		return (secondDate.compareTo(firstDate)>0)?true:false;
	}
	
	public static int compareDateOnly(Date date1, Date date2){
		DateTime first = (date1==null)?null:new DateTime(date1);
		DateTime second = (date2==null)?null:new DateTime(date2);

		LocalDate firstDate = first.toLocalDate();
		LocalDate secondDate = second.toLocalDate();

		return firstDate.compareTo(secondDate);
	}
	public static int compareTimeOnly(Date date1, Date date2){
		
		DateTime first = (date1==null)?null:new DateTime(date1);
		DateTime second = (date2==null)?null:new DateTime(date2);

		LocalTime firstTime = first.toLocalTime();
		LocalTime secondTime = second.toLocalTime();

		return firstTime.compareTo(secondTime);
	}
	
	public static int compareHourMinuteOnly(String time1, String time2){
		Pattern p = Pattern.compile("(\\d{2}):(\\d{2})");
		Matcher m = p.matcher(time1);
		int hour1=0,minute1=0,hour2=0,minute2=0;
		if (m.matches() ) {
		    String hourString1 = m.group(1);
		    String minuteString1 = m.group(2);
		    hour1 = Integer.parseInt(hourString1);
		    minute1 = Integer.parseInt(minuteString1);

		}
		m = p.matcher(time2);
		if (m.matches() ) {
		    String hourString2 = m.group(1);
		    String minuteString2 = m.group(2);
		    hour2 = Integer.parseInt(hourString2);
		    minute2 = Integer.parseInt(minuteString2);

		}
		if(hour1>hour2)
			return 1;
		else if(hour1<hour2)
			return -1;
		else{
			if(minute1>minute2)
				return 1;
			else
				return -1;
		}
	}
	public static int compareDateAndTime(Date date1,Date date2){
		DateTime first = (date1==null)?null:new DateTime(date1);
		DateTime second = (date2==null)?null:new DateTime(date2);
		
		int compare = first.compareTo(second);
		return compare;
	}
	public static int compareDateAndTime(Date date1,Date date2,int hours){
		DateTime first = (date1==null)?null:new DateTime(date1);
		DateTime second = (date2==null)?null:new DateTime(date2);
		first = first.plusHours(hours).plusMinutes(1);
		int compare = first.compareTo(second);
		return compare;
	}
	public static Date incrementByDay(Date date, int days){
		LocalDate localDate = new DateTime(date).toLocalDate();
		return localDate.plusDays(days).toDate();
	}
	public static Date decrementByDay(Date date, int days){
		LocalDate localDate = new DateTime(date).toLocalDate();
		return localDate.minusDays(days).toDate();
	}
	public static int compareTimeOnly(Date date1, Date date2, int i) {
		DateTime first = (date1==null)?null:new DateTime(date1);
		DateTime second = (date2==null)?null:new DateTime(date2);

		LocalTime firstTime = first.toLocalTime().plusHours(i);
		LocalTime secondTime = second.toLocalTime();

		return firstTime.compareTo(secondTime);
	}

	public static int compareDateAndTime(Date date1, Date time1,
			Date date2, Date time2) {
		
		LocalDate startdate = new LocalDate(date1);
		LocalTime starttime = new LocalTime(time1);
		LocalDateTime startdateTime= startdate.toLocalDateTime(starttime);
		
		LocalDate enddate = new LocalDate(date2);
		LocalTime endtime = new LocalTime(time2);
		LocalDateTime enddateTime= enddate.toLocalDateTime(endtime);
		
		return startdateTime.compareTo(enddateTime);
	}
	
	public static Date getDate(String date, String pattern){
		Date d = null;
		try{
			SimpleDateFormat timeformat = new SimpleDateFormat(pattern);
			d = timeformat.parse(date);
		}catch(ParseException exception){
			exception.printStackTrace();
		}
		return d;
	}
	public static String getTimeOnly(Date date, String pattern){
		String d = null;
		try{
			SimpleDateFormat timeformat = new SimpleDateFormat(pattern);
			d = timeformat.format(date);
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return d;
	}

	public static boolean afterMidnight(Date startDate, Date time){

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateAndtimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			String d = dateformat.format(startDate);
			String d2 = dateformat.format(time);
			Date sd = dateAndtimeformat.parse(d+" 23:59");
			Date ed = dateAndtimeformat.parse(d2+" 02:00");
			if( time.compareTo(sd)>0 && time.compareTo(ed)<=0)
				return true;
			else
				return false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	public static Date getMidnight(Date date) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateAndtimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			String d = dateformat.format(date);
			return dateAndtimeformat.parse(d+" 23:59");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static boolean isMidnight(Date time) {
		LocalTime starttime = new LocalTime(time);
		LocalTime endtime = new LocalTime(23,59);
		if( starttime.compareTo(endtime)==0)
			return true;
		else
			return false;
		
	}
	
	public static String getDateAsStr(Date date) {
        if (date != null) {
            SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd");
            return dtformat.format(date);
        } else {
            return null;
        }
    }
	
}
