package com.onetouch.view.bean.useremployee.notification;



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

@ManagedBean(name="notificationHome")
@ViewScoped
public class NotificationHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<Schedule> notifiedSchedules;
	private Employee employee;
	private CustomUser user;
	private Tenant tenant;
	private Schedule selectedNotification;
	@PostConstruct
	public void init(){
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		tenant = tenantContext.getTenant();
		employee = getEmployeeService().getEmplyeePositionById(user.getEmp_id(), tenant);
		notifiedSchedules = getScheduleService().getAllNotifiedSchedules(employee, tenant);
	}
	public List<Schedule> getNotifiedSchedules() {
		return notifiedSchedules;
	}
	public void setNotifiedSchedules(List<Schedule> notifiedSchedules) {
		this.notifiedSchedules = notifiedSchedules;
	}
	public CustomUser getUser() {
		return user;
	}
	public void setUser(CustomUser user) {
		this.user = user;
	}
	
	public void acceptNotification(ActionEvent actionEvent){
		String renderKitId = FacesContext.getCurrentInstance().getViewRoot().getRenderKitId();       
        if(renderKitId.equalsIgnoreCase("PRIMEFACES_MOBILE")){
        	getScheduleService().updateScheduleStatus(selectedNotification.getId(),employee.getId(),tenant.getId(),selectedNotification.getEventPosition().getId(),2);
        	notifiedSchedules = getScheduleService().getAllNotifiedSchedules(employee, tenant);
    		FacesUtils.addInfoMessage("Scheduled Accepted Successfully");
    		//return "pm:notificationlist";
        }else{
        	String eventPosId = FacesUtils.getRequestParameter("eventPosId");
    		String scheduleId = FacesUtils.getRequestParameter("scheduleId");
    		Integer epid = Integer.parseInt(eventPosId);
    		Integer scid = Integer.parseInt(scheduleId);
    		getScheduleService().updateScheduleStatus(scid,employee.getId(),tenant.getId(),epid,2);
    		notifiedSchedules = getScheduleService().getAllNotifiedSchedules(employee, tenant);
    		FacesUtils.addInfoMessage("Scheduled Accepted Successfully");
    		
        }
		 
	}
	public void declineNotification(ActionEvent actionEvent){
		String renderKitId = FacesContext.getCurrentInstance().getViewRoot().getRenderKitId();       
        if(renderKitId.equalsIgnoreCase("PRIMEFACES_MOBILE")){
        	getScheduleService().updateScheduleStatus(selectedNotification.getId(),employee.getId(),tenant.getId(),selectedNotification.getEventPosition().getId(),3);
        	notifiedSchedules = getScheduleService().getAllNotifiedSchedules(employee, tenant);
    		FacesUtils.addInfoMessage("Scheduled Declined Successfully");
    		//return "pm:notificationlist";
        }else{
			String eventPosId = FacesUtils.getRequestParameter("eventPosId");
			String scheduleId = FacesUtils.getRequestParameter("scheduleId");
			Integer epid = Integer.parseInt(eventPosId);
			Integer scid = Integer.parseInt(scheduleId);
			getScheduleService().updateScheduleStatus(scid,employee.getId(),tenant.getId(),epid,3);
			notifiedSchedules = getScheduleService().getAllNotifiedSchedules(employee, tenant);
			FacesUtils.addInfoMessage("Scheduled Declined Successfully");
			
        }
	}
	public Schedule getSelectedNotification() {
		return selectedNotification;
	}
	public void setSelectedNotification(Schedule selectedNotification) {
		this.selectedNotification = selectedNotification;
	}
	
}
