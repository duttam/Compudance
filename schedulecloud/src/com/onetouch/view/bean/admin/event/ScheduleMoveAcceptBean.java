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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.Location;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Position;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Schedule;



import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.position.PositionController;
import com.onetouch.view.bean.admin.position.PositionSupport;

import com.onetouch.view.bean.util.DateBean;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="scheduleMoveBean")
@RequestScoped
public class ScheduleMoveAcceptBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String notificationStatus;
	private Integer scheduleId;
	private Integer employeeId;
	private Integer companyId;
	private Integer eventPosId;
	private String status;
	private String notificationDesc;
	private Schedule schedule;
	public void init(){
		if(scheduleId!=null&&employeeId!=null&&companyId!=null&&eventPosId!=null&&status!=null){
			schedule = getScheduleService().getSchedule(scheduleId,eventPosId,employeeId,companyId);
			if(schedule!=null && schedule.isActive()){
				if(DateUtil.compareDateAndTime(schedule.getExpiretime(), new Date())<0){
					notificationStatus = "Schedule Expired";
					notificationDesc = "The specified schedule offer has expired. You didn't respond in time.";
				}
				else{
					if(schedule!=null && schedule.getSchedulestatus()==1){
						if(status.equalsIgnoreCase("accept")){
							getScheduleService().updateScheduleStatus(scheduleId,employeeId,companyId,eventPosId,2);
							notificationStatus = "Schedule Confirmed";
							notificationDesc = "Thank you for your confirmation. Below is your full schedule detail";
						}
						
					}else if(schedule!=null && schedule.getSchedulestatus()==2){
						notificationStatus = "Already Accepted";
						notificationDesc = "You have previously accepted the schedule, for any changes please contact the administrator";
						
						
					}else{
						notificationStatus = "Invalid link, please contact the administrator";
					}
				}
			}else{
				notificationStatus = "Schedule Expired";
				notificationDesc = "The specified schedule offer has expired. You didn't respond in time.";
			}
		}
			
		
	}
	public String getNotificationStatus() {
		return notificationStatus;
	}
	public void setNotificationStatus(String notificationStatus) {
		this.notificationStatus = notificationStatus;
	}
	public Integer getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public Integer getEventPosId() {
		return eventPosId;
	}
	public void setEventPosId(Integer eventPosId) {
		this.eventPosId = eventPosId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNotificationDesc() {
		return notificationDesc;
	}
	public void setNotificationDesc(String notificationDesc) {
		this.notificationDesc = notificationDesc;
	}
	public Integer getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}
	public Schedule getSchedule() {
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
		
	
}
