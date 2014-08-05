package com.onetouch.view.bean;





import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;

import com.onetouch.model.domainobject.Event;
import com.onetouch.model.service.availability.IEmployeeAvailability;
import com.onetouch.model.service.email.IEmailStatusService;
import com.onetouch.model.service.email.IInviteEmployeeEmail;
import com.onetouch.model.service.email.IScheduleEmployeeEmail;
import com.onetouch.model.service.email.InviteEmployeeEmail;
import com.onetouch.model.service.email.ScheduleEmployeeEmail;
import com.onetouch.model.service.employee.IEmployeeService;
import com.onetouch.model.service.event.IEventService;
import com.onetouch.model.service.location.ILocationService;
import com.onetouch.model.service.position.IPositionService;

import com.onetouch.model.service.report.IReportService;
import com.onetouch.model.service.schedule.IScheduleService;

import com.onetouch.model.service.signinout.ISignInOutService;
import com.onetouch.model.service.tenant.ITenantService;
import com.onetouch.model.service.timeoff.ITimeoffService;
import com.onetouch.model.service.upload.IUploadService;
import com.onetouch.model.util.ApplicationData;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.util.SpringJSFUtil;
import com.onetouch.view.context.TenantContext;
import com.onetouch.view.email.Sender;
import com.onetouch.view.util.DateUtil;
import com.sun.jersey.server.impl.uri.rules.automata.AutomataMatchingUriTemplateRules;



public class BaseBean
    implements 
        Serializable {

    private static final long serialVersionUID = 1L;


    //the logger for this class
    protected final Log logger = LogFactory.getLog(this.getClass());
	 
    /*@ManagedProperty(value="#{employeeService}")
    protected transient IEmployeeService employeeService;
    
    
    @ManagedProperty(value="#{tenantService}")
    protected ITenantService tenantService;
    
    
    @ManagedProperty(value="#{positionService}")
    protected IPositionService positionService;
    
    @ManagedProperty(value="#{locationService}")
    protected ILocationService locationService;
    
    
    @ManagedProperty(value="#{sender}")
    protected Sender sender ;*/
    

	@ManagedProperty(value="#{inviteMessage}")
    protected SimpleMailMessage inviteMessage;
	
    @ManagedProperty(value="#{scheduleNoteMessage}")
    protected SimpleMailMessage scheduleNoteMessage;
    
    @ManagedProperty(value="#{companyRegisterMessage}")
    protected SimpleMailMessage companyRegisterMessage;
    
    @ManagedProperty(value="#{scheduleMessage}")
    protected SimpleMailMessage scheduleMessage;
	
    @ManagedProperty(value="#{resendScheduleMessage}")
    protected SimpleMailMessage resendScheduleMessage;
    
    @ManagedProperty(value="#{changeScheduleMessage}")
    protected SimpleMailMessage changeScheduleMessage;
    
    @ManagedProperty(value="#{requestTimeOff}")
    protected SimpleMailMessage requestTimeOff;
    
    @ManagedProperty(value="#{resetPasswordMessage}")
    protected SimpleMailMessage resetPasswordMessage;
    
	@ManagedProperty(value="#{tenantContext}")
	protected TenantContext tenantContext;
	
	
	/*@ManagedProperty(value="#{applicationData}")
    protected transient ApplicationData applicationData;
	*/
    public BaseBean(){
    	
    }
    public static IEmailStatusService getEmailStatusService(){
    	return SpringJSFUtil.getBean("emailStatusService");
    }
    public static IInviteEmployeeEmail getInviteEmployeeSender() {
		return SpringJSFUtil.getBean("inviteEmployeeMailSender");
	}
    public static IScheduleEmployeeEmail getSheduleEmployeeSender() {
		return SpringJSFUtil.getBean("scheduleEmployeeMailSender");
	}
	public static IEmployeeService getEmployeeService() {
		return SpringJSFUtil.getBean("employeeService");
	}
	
	public static IPositionService getPositionService() {
		return SpringJSFUtil.getBean("positionService");
	}
	
	public static ILocationService getLocationService() {
		return SpringJSFUtil.getBean("locationService");
	}
	
	public static IEventService getEventService(){
		return SpringJSFUtil.getBean("eventService");
	}
	
	
	public IEmployeeAvailability getEmployeeAvailability() {
		return SpringJSFUtil.getBean("employeeAvailability");
	}
	public IScheduleService getScheduleService(){
		return SpringJSFUtil.getBean("scheduleService");
	}
	public ITenantService getTenantService() {
		return SpringJSFUtil.getBean("tenantService");
	}
	public IUploadService getUploadService(){
		return SpringJSFUtil.getBean("uploadService");
	}
	public ISignInOutService getSignInOutService(){
		return SpringJSFUtil.getBean("signInOutService");
	}
	public static IReportService getReportService() {
		return SpringJSFUtil.getBean("reportService");
	}
	public static ITimeoffService getTimeoffService() {
		return SpringJSFUtil.getBean("timeoffService");
	}
	public Sender getSender() {
		return SpringJSFUtil.getBean("sender");
	}
	
	public TenantContext getTenantContext() {
		return tenantContext;
	}
	public void setTenantContext(TenantContext tenantContext) {
		this.tenantContext = tenantContext;
	}
	public ApplicationData getApplicationData() {
		return SpringJSFUtil.getBean("applicationData");
	}
	
	public SimpleMailMessage getInviteMessage() {
		return inviteMessage;
	}
	public void setInviteMessage(SimpleMailMessage inviteMessage) {
		this.inviteMessage = inviteMessage;
	}
	public SimpleMailMessage getCompanyRegisterMessage() {
		return companyRegisterMessage;
	}
	public void setCompanyRegisterMessage(SimpleMailMessage companyRegisterMessage) {
		this.companyRegisterMessage = companyRegisterMessage;
	}
	public SimpleMailMessage getScheduleNoteMessage() {
		return scheduleNoteMessage;
	}
	public void setScheduleNoteMessage(SimpleMailMessage scheduleNoteMessage) {
		this.scheduleNoteMessage = scheduleNoteMessage;
	}
	public SimpleMailMessage getScheduleMessage() {
		return scheduleMessage;
	}
	public void setScheduleMessage(SimpleMailMessage scheduleMessage) {
		this.scheduleMessage = scheduleMessage;
	}
	
	public SimpleMailMessage getResendScheduleMessage() {
		return resendScheduleMessage;
	}
	public void setResendScheduleMessage(SimpleMailMessage resendScheduleMessage) {
		this.resendScheduleMessage = resendScheduleMessage;
	}
	public SimpleMailMessage getRequestTimeOff() {
		return requestTimeOff;
	}
	public void setRequestTimeOff(SimpleMailMessage requestTimeOff) {
		this.requestTimeOff = requestTimeOff;
	}
	public SimpleMailMessage getChangeScheduleMessage() {
		return changeScheduleMessage;
	}
	public void setChangeScheduleMessage(SimpleMailMessage changeScheduleMessage) {
		this.changeScheduleMessage = changeScheduleMessage;
	}
	public SimpleMailMessage getResetPasswordMessage() {
		return resetPasswordMessage;
	}
	public void setResetPasswordMessage(SimpleMailMessage resetPasswordMessage) {
		this.resetPasswordMessage = resetPasswordMessage;
	}
	
	protected Date getEventExpireTime(Event event){
		Date expDate = new Date();
		int days = DateUtil.dateRange(new Date(),event.getStartDate());
		if(days<3){
			expDate = DateUtil.getDateAndTime(event.getStartDate(),DateUtil.getDate("11:59 PM","hh:mm a"));
			
		}
		else{	
			expDate = DateUtil.getDateAndTime(DateUtil.incrementByDay(new Date(),3),DateUtil.getDate("11:59 PM","hh:mm a"));
			
		}
		return expDate;
	}
	   
	   
}
