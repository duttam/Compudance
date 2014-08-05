package com.onetouch.view.bean.useremployee.request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.TimeOffRequest;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.StringUtilities;
//import com.onetouch.view.util.StringUtilities;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;


@ManagedBean(name="timeoffBean")
@ViewScoped
public class TimeOffBean extends BaseBean{
       
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
        private Employee employee;
	private CustomUserDetail user;

    private Tenant tenant;
	private TimeOffRequest timeOff;
	private Integer timeoffId;
	private String action;
	private List<String> requestTypes;
	private Event sickEvent;
	private List<Event> sickEventList;
	private boolean timeOffBtn = false;
	@PostConstruct
	public void initController() {
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		
		tenant = tenantContext.getTenant();
		employee = getEmployeeService().getEmplyeePositionById(user.getEmp_id(), tenant);
		employee.setTenant(tenant);
		if(FacesUtils.isMobileRequest()){
			timeOff = new TimeOffRequest();
			timeOff.setBeginDate(new Date());
		}else{
			requestTypes = new ArrayList<String>();
			requestTypes.add("Call Out");
			//requestTypes.add("Not Available");
			action = FacesUtils.getRequestParameter("action");
			if(action.equalsIgnoreCase("addTimeoff")){
				timeOff = new TimeOffRequest();
			}
	
			if(action.equalsIgnoreCase("editTimeoff")){
				if(timeoffId==null)
					timeoffId = Integer.parseInt(FacesUtils.getRequestParameter("timeoffId"));
				timeOff = getTimeoffService().getTimeOffRequest(timeoffId);
			}
		}
	}
	public String newSickRequest(){
		timeOff.setRequestType("Call Out");
		if(timeOff.getBeginDate()!=null)
			sickEventList = getEventService().getAllEventsByEmployee(employee,timeOff,tenant);
		return "pm:newsickreq";
	}
	/*public String newVacationRequest(){
		timeOff.setRequestType("Not Available");
		return "pm:newvacationreq";
	}*/
	public void timeOffReqChanged(AjaxBehaviorEvent ajaxBehaviorEvent){
		if(timeOff!=null && timeOff.getRequestType().equalsIgnoreCase("Call Out")){
			if(timeOff.getBeginDate()!=null)
				sickEventList = getEventService().getAllEventsByEmployee(employee,timeOff,tenant);
			if(sickEventList!=null && sickEventList.size()<1)
				FacesUtils.addInfoMessage("eventlistMsg","No events scheduled for today");
		}else{
			
		}
	}
	public void handleStartDateSelect(SelectEvent event) {  
        
		Date start_date = timeOff.getBeginDate();
		Date end_date = timeOff.getEndDate();
		if(timeOff!=null && (timeOff.getRequestType()!=null&&timeOff.getRequestType().equalsIgnoreCase("Call Out"))){
			if(timeOff.getBeginDate()!=null){
				if(DateUtil.compareDateOnly(timeOff.getBeginDate(), new Date())<0)
					FacesUtils.addErrorMessage("startDt","Enter a valid start date");
				else{
					sickEventList = getEventService().getAllEventsByEmployee(employee,timeOff,tenant);
					RequestContext.getCurrentInstance().update("eventlist");
				}
			}
		}else{
			if(end_date==null){
				timeOff.setEndDate(start_date);
			}else{
				if(start_date !=null && start_date.getTime()>end_date.getTime())
					FacesUtils.addErrorMessage("startDt","Enter a valid start date");
			}
		}
		
		
    } 
	public void handleEndDateSelect(SelectEvent event) {  
		Date start_date = timeOff.getBeginDate();
		Date end_date = timeOff.getEndDate();
		if(start_date !=null && end_date !=null && start_date.getTime()>end_date.getTime())
			FacesUtils.addErrorMessage("endDt","Enter a valid end date");
    }
	
	public String addTimeOffRequest() {
            if(timeOff != null) {
            	List<String> receipients = new ArrayList<String>();
                timeOff.setCompanyId(tenant.getId());
                timeOff.setEmployeeId(user.getEmp_id());
                int offDays = DateUtil.dateRange(timeOff.getBeginDate(),timeOff.getEndDate()); 
                timeOff.setNumDays(offDays+1);
                if(FacesUtils.isMobileRequest()){
                	if(timeOff.getRequestType().equalsIgnoreCase("Call Out")){
                		timeOff.setEndDate(timeOff.getBeginDate());
                		timeOff.setSickEvent(sickEvent);
                		String adminEmail = sickEvent.getOwner().getEmail();
                    	receipients.add(adminEmail);
                	}else{
                		String adminEmail = tenant.getCompanyEmail();
                    	receipients.add(adminEmail);
                	}
                    getTimeoffService().addTimeOffRequest(timeOff);
                    //TimeOffHome timeOffHome = (TimeOffHome)FacesUtils.getManagedBean("timeoffHome");
                    //timeOffHome.getTimeoffList().add(timeOff);
                }else{
	                if(action.equalsIgnoreCase("editTimeoff") && timeOff.getEmployeeId() != null) {
	                    getTimeoffService().updateTimeOffRequest(timeOff);
	                }
	                if(action.equalsIgnoreCase("addTimeoff")) {
	                	if(timeOff.getRequestType().equalsIgnoreCase("Call Out")){
	                		timeOff.setEndDate(timeOff.getBeginDate());
	                		timeOff.setSickEvent(sickEvent);
	                		String adminEmail = sickEvent.getOwner().getEmail();
	                    	receipients.add(adminEmail);
	                	}else{
	                		String adminEmail = tenant.getCompanyEmail();
	                    	receipients.add(adminEmail);
	                	}
	                	
	                    getTimeoffService().addTimeOffRequest(timeOff);
	                }
                
                }
    			SimpleDateFormat dateformat = new SimpleDateFormat("EEEEEEEEE, MMMMMMMM d, yyyy");
    			Map<Object,Object> attributes = new HashMap<Object, Object>();
    			if(timeOff.getRequestType().equalsIgnoreCase("Call Out")){
    				
    				attributes.put("emailsubject", employee.getFirstname()+" "+employee.getLastname()+" Call Out  ");
    				attributes.put("timeoffDate", dateformat.format(timeOff.getBeginDate()));
    				attributes.put("eventName", this.getSickEvent().getName());
    				attributes.put("reason", timeOff.getReason());
    				attributes.put("priority", "HIGH");
    				//attributes.put("smtphost", tenant.getEmailSender());
    				getSender().timeOffNotification(requestTimeOff, attributes, receipients,timeOff.getRequestType(),employee.getEmail());
    			}
    			/*else if(timeOff.getRequestType().equalsIgnoreCase("Not Available")){
    				//Map<Object,Object> attributes = new HashMap<Object, Object>();
    				attributes.put("emailsubject", employee.getFirstname()+" "+employee.getLastname()+" Time-Off/Not Available  ");
    				attributes.put("timeoffStartDate", dateformat.format(timeOff.getBeginDate()));
    				attributes.put("timeoffEndDate", dateformat.format(timeOff.getEndDate()));
    				
    				
    			}*/
    			//getSender().timeOffNotification(requestTimeOff, attributes, receipients,timeOff.getRequestType(),employee.getEmail());
            }
            
            FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			if(FacesUtils.isMobileRequest()){
				if(timeOff.getRequestType().equalsIgnoreCase("Call Out"))
					FacesUtils.addInfoMessage("Request Submitted");
				else
					FacesUtils.addInfoMessage("Request Submitted");
				
				return "/ui/mobile/employee/empRequest?faces-redirect=true";
			}
			else{
				FacesUtils.addInfoMessage("Request Submitted Successfully");
				return "requestHome?faces-redirect=true";
			}
            
	}
	
	
	public TimeOffRequest getTimeOff() {
            return this.timeOff;
        }

	public void setTimeOff(TimeOffRequest req) {
            this.timeOff = req;
        }

        public List<String> getRequestTypes() {
            return this.requestTypes;
        }

        public void setRequestTypes(List<String> types) {
            this.requestTypes = types;
        }

		

		public List<Event> getSickEventList() {
			return sickEventList;
		}

		public void setSickEventList(List<Event> sickEventList) {
			this.sickEventList = sickEventList;
		}

		public Event getSickEvent() {
			return sickEvent;
		}

		public void setSickEvent(Event sickEvent) {
			this.sickEvent = sickEvent;
		}
		public boolean isTimeOffBtn() {
			if(timeOff.getRequestType()!=null && this.sickEventList!=null){
	        	if((timeOff.getRequestType().equalsIgnoreCase("Call Out"))&&(this.sickEventList.size()<=0)){
	        		timeOffBtn = true;
	        	}
        	}
        	
        	
			return timeOffBtn;
		}
		public void setTimeOffBtn(boolean timeOffBtn) {
			this.timeOffBtn = timeOffBtn;
		}
		
        /*public boolean isTimeOffBtn(){
        	if(timeOff.getRequestType()!=null && this.sickEventList!=null){
	        	if((timeOff.getRequestType().equalsIgnoreCase("Sick"))&&(this.sickEventList.size()>0)){
	        		return true;
	        	}
        	}
        	
        	return false;
        }*/
        /*public void notifyViaEmail(ActionEvent event) {
            List<String> receipients = new ArrayList<String>();
            Map<Object,Object> attributes = null;

            try{                
                attributes = populateEmailAttributes(employee);
                receipients.add(employee.getEmail());
                receipients.add(tenant.getCompanyEmail());
                
                getSender().timeOffNotification( requestTimeOffMessage, attributes, receipients);
                employee = new Employee(tenant,"invitesent");
                FacesUtils.addInfoMessage("Employee Saved Successfully");
            } catch (DataAccessException dae) {
                    dae.printStackTrace();
            } catch (MailException me) {
                    me.printStackTrace();
                    getSender().inviteEmployee(requestTimeOffMessage, attributes, receipients);
            } catch(Exception exception) {
                    FacesUtils.addErrorMessage("Error!");
            }
	}

	private Map<Object, Object> populateEmailAttributes(Employee employee) {
            Map<Object, Object> attributes = new HashMap<Object, Object>();
            attributes.put("companyName", tenantContext.getTenant().getName());
            attributes.put("firstname",employee.getFirstname());
            attributes.put("lastname", employee.getLastname());
            attributes.put("requestType", timeOff.getRequestType());
            attributes.put("startDate", StringUtilities.formatDate(timeOff.getBeginDate()));
            attributes.put("endDate", StringUtilities.formatDate(timeOff.getEndDate()));
            return attributes;
	}
        
	public SimpleMailMessage getRequestTimeOffMessage() {
            return requestTimeOffMessage;
	}
        
	public void setRequestTimeOffMessage(SimpleMailMessage inviteMessage) {
            this.requestTimeOffMessage = inviteMessage;
	}
*/}
