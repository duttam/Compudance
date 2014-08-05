package com.onetouch.view.bean.admin.schedule;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

import com.onetouch.view.bean.BaseBean;

//@ManagedBean(name="scheduleController")
//@ViewScoped
public class ScheduleController extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String adminHomeView ;
	@ManagedProperty(value="#{scheduleHome}")
	private ScheduleHome scheduleHome;
	@PostConstruct
	public void initScheduleController(){
		adminHomeView = "calender";
	}
	public void changeAdminHomeView(){
		if(adminHomeView.equalsIgnoreCase("timeline")){
			scheduleHome.selectLocationChange();
		}
			
	}
	public String getAdminHomeView() {
		return adminHomeView;
	}
	public void setAdminHomeView(String adminHomeView) {
		this.adminHomeView = adminHomeView;
	}
	public ScheduleHome getScheduleHome() {
		return scheduleHome;
	}
	public void setScheduleHome(ScheduleHome scheduleHome) {
		this.scheduleHome = scheduleHome;
	}
	
	
}
