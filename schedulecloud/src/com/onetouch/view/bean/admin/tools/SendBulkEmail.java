package com.onetouch.view.bean.admin.tools;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.EmailGroup;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.comparator.EmployeeNameComparator;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="bulkEmailBean") 
@ViewScoped
public class SendBulkEmail extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value="#{supportBean}")
	private SupportBean supportBean;
	private List<Employee> allActiveEmployees;
	private List<Employee> allSelectedEmployees;
	private DualListModel<Employee> employees;
	private String emailMsg;
	private String emailSubject;
	private Tenant tenant;
	
	private List<String> uploadAttachment = new LinkedList<String>();
	private CustomUser customUser;
	private List<EmailGroup> selectedEmailGroups;
	private List<Region> selectedRegions;
	private List<Position> selectedPositions;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	@PostConstruct
	public void initToolHome(){
		tenant = tenantContext.getTenant();
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
			
		}
		selectedRegions = new ArrayList<Region>();
		selectedRegions.add(regionBean.getSelectedRegion());
		allActiveEmployees = getEmployeeService().getAllEmployee(tenant,new String[]{"active"},regionBean.getSelectedRegion());//source
		Collections.sort(allActiveEmployees,new EmployeeNameComparator());
		employees = new DualListModel<Employee>(allActiveEmployees, new ArrayList<Employee>());
	}
	public void filteAvailableEmployeeList(ActionEvent actionEvent){
		allActiveEmployees = getEmployeeService().getAllEmployeesByRegionAndPosition(selectedPositions, selectedRegions, tenant);
		employees = new DualListModel<Employee>(allActiveEmployees, new ArrayList<Employee>());
	}
	public void onTabChange(TabChangeEvent event){
	    
	    
	        if ( event.getTab().getId().equalsIgnoreCase( "positionHome" ) ){
	        
	        }
	        if ( event.getTab().getId().equalsIgnoreCase( "shiftHome" ) ){
		        
	        }
	 }
	public void uploadEmployeeDetailFile(FileUploadEvent event){
		
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/upload/"+tenant.getCode());
		File uploadDir = new File(path);
		if (!uploadDir.exists()) {
			if (uploadDir.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		File targetFolder = new File(uploadDir+"/"+event.getFile().getFileName());

		InputStream inputStream;
		try {
			inputStream = event.getFile().getInputstream();

			OutputStream out = new FileOutputStream(targetFolder);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			inputStream.close();
			out.flush();
			out.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		uploadAttachment.add(event.getFile().getFileName());
		FacesUtils.addInfoMessage("File "+event.getFile().getFileName()+" uploaded Successfully");
		
	}
	public void sendEmailToEmployees(ActionEvent actionEvent){
		String renderKitId = FacesContext.getCurrentInstance().getViewRoot().getRenderKitId();       
        if(renderKitId.equalsIgnoreCase("PRIMEFACES_MOBILE")){
        	List<String> receipients = new ArrayList<String>();
			Map<Object,Object> attributes = new HashMap<Object, Object>();
        	for(Employee employee : allSelectedEmployees){
				receipients.add(employee.getEmail());
			}
        	attributes.put("smtphost",tenant.getEmailSender());
			attributes.put("scheduleNotes",emailMsg );
			attributes.put("emailsubject", this.emailSubject);
			Employee employee=null;
			try{
				employee=  supportBean.getCurrentLoggedEmployee();
				attributes.put("senderEmail", employee.getEmail());
				attributes.put("senderfirstname",employee.getFirstname() );
				attributes.put("senderlastname", employee.getLastname());
				employee=  supportBean.getCurrentLoggedEmployee();
				Employee ccEmployee = getEmployeeService().getEmplyeePositionById(customUser.getEmp_id(), tenant);
				getSender().sendScheduleNotes(scheduleNoteMessage, attributes, receipients,tenant.getEmailSender(),ccEmployee.getEmail(),tenant,new LinkedList<String>());
				//getSender().sendScheduleNotes(scheduleNoteMessage, attributes, receipients,tenant.getEmailSender());
				emailMsg = "";
				emailSubject="";
				allSelectedEmployees = null;
				
				FacesUtils.addInfoMessage("Message Sent Successfully");
			}catch (DataAccessException dae) {
				dae.printStackTrace();
			}catch (MailException me){
				me.printStackTrace();
				//getSender().sendScheduleNotes(scheduleNoteMessage, attributes, receipients,employee.getEmail());
			}catch(Exception exception){
				FacesUtils.addErrorMessage("Error!");
			}
        }else{
        	List<String> receipients = new ArrayList<String>();
			Map<Object,Object> attributes = new HashMap<Object, Object>();
			Employee employee=null;
        	try {
        		
				allSelectedEmployees = employees.getTarget();
				for(Employee selectedEmployee : allSelectedEmployees){
					receipients.add(selectedEmployee.getEmail());
				}
				attributes.put("smtphost",tenant.getEmailSender());
				attributes.put("scheduleNotes",emailMsg );
				attributes.put("emailsubject", this.emailSubject);

			
				employee=  supportBean.getCurrentLoggedEmployee();
				attributes.put("senderEmail", employee.getEmail());
				attributes.put("senderfirstname",employee.getFirstname() );
				attributes.put("senderlastname", employee.getLastname());
				Employee ccEmployee = getEmployeeService().getEmplyeePositionById(customUser.getEmp_id(), tenant);
				getSender().sendScheduleNotes(scheduleNoteMessage, attributes, receipients,tenant.getEmailSender(),ccEmployee.getEmail(),tenant,uploadAttachment);
				emailMsg = "";
				emailSubject="";
				allSelectedEmployees = null;
				//uploadAttachment = null;
				allActiveEmployees = getEmployeeService().getAllEmployee(tenant,new String[]{"active"},regionBean.getSelectedRegion());//source
				selectedPositions = null;
				uploadAttachment.clear();
				employees = new DualListModel<Employee>(allActiveEmployees, new ArrayList<Employee>());
				FacesUtils.addInfoMessage("Message Sent Successfully");
			}catch (DataAccessException dae) {
				dae.printStackTrace();
			} 
			catch (MailException me){
				me.printStackTrace();
				//getSender().sendScheduleNotes(scheduleNoteMessage, attributes, receipients,tenant.getEmailSender());
			}catch(Exception exception){
				exception.printStackTrace();
				FacesUtils.addErrorMessage("Error!");
			}
			
        }
	}
	public DualListModel<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(DualListModel<Employee> employees) {
		this.employees = employees;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getEmailMsg() {
		return emailMsg;
	}

	public void setEmailMsg(String emailMsg) {
		this.emailMsg = emailMsg;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public SupportBean getSupportBean() {
		return supportBean;
	}

	public void setSupportBean(SupportBean supportBean) {
		this.supportBean = supportBean;
	}

	public List<Employee> getAllActiveEmployees() {
		return allActiveEmployees;
	}

	public void setAllActiveEmployees(List<Employee> allActiveEmployees) {
		this.allActiveEmployees = allActiveEmployees;
	}

	public List<Employee> getAllSelectedEmployees() {
		return allSelectedEmployees;
	}

	public void setAllSelectedEmployees(List<Employee> allSelectedEmployees) {
		this.allSelectedEmployees = allSelectedEmployees;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}

	public List<String> getUploadAttachment() {
		return uploadAttachment;
	}

	public void setUploadAttachment(List<String> uploadAttachment) {
		this.uploadAttachment = uploadAttachment;
	}

	public List<Position> getSelectedPositions() {
		return selectedPositions;
	}

	public void setSelectedPositions(List<Position> selectedPositions) {
		this.selectedPositions = selectedPositions;
	}

	public List<EmailGroup> getSelectedEmailGroups() {
		return selectedEmailGroups;
	}

	public void setSelectedEmailGroups(List<EmailGroup> selectedEmailGroups) {
		this.selectedEmailGroups = selectedEmailGroups;
	}
	public List<Region> getSelectedRegions() {
		return selectedRegions;
	}
	public void setSelectedRegions(List<Region> selectedRegions) {
		this.selectedRegions = selectedRegions;
	}

	
		
}
