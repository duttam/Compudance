package com.onetouch.view.bean.admin.employee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PreValidateEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.joda.time.Hours;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TenantDocument;
import com.onetouch.model.util.ApplicationContextProvider;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.util.DateBean;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.EmployeeUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.ValidationMessages;

@ManagedBean(name="employeeController") 
@ViewScoped
public class EmployeeController extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    
      
    
    
    @ManagedProperty(value="#{employeeSupport}")
    private EmployeeSupport employeeSupport;
    
    
    
    private Employee employee;
    
    private Tenant tenant;
    private List<Position> selectedPositions;
    public EmployeeController(){}
	private String action;
	private Integer id;
	private UserDetails user;
	private List<Position> deletedPositions;
	private List<Position> newSelectedPositions;
	
	private List<Location> deleteSalesPersonLocation;
	private List<Location> newSalespersonLocation;
	
	private List<Location> newRestrictEmpLoc;
	private List<Location> deleteRestrictEmpLoc;
	private boolean adminRoleChange;
	private boolean managerRoleChange;
	private boolean salesPersonRoleChange;
	
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	@PostConstruct
	public void initEmployeController(){
		
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
		user = (CustomUser)authenticationToken.getPrincipal();
		
		tenant = tenantContext.getTenant();// get company detail from security context
		action = FacesUtils.getRequestParameter("action");
		if(action.equalsIgnoreCase("inviteemployee")){
			employee = new Employee(tenant,"invitesent");
			employee.setEmpRate(new EmployeeRate());
			deletedPositions = new ArrayList<Position>();
			newSelectedPositions = new ArrayList<Position>();
		}
		if(action.equalsIgnoreCase("editemployee")){
			Integer id = Integer.parseInt(FacesUtils.getRequestParameter("id"));
			
			employee = getEmployeeService().getEmplyeeById(id,tenant);
			this.loadImagesToDisk();
			employee.setTenant(tenant);
			/*if(employee!=null){
				this.loadImagesToDisk();
				employee.setTenant(tenant);
			}else{
				employee = new Employee(tenant,"invitesent");
				employee.setEmpRate(new EmployeeRate());
			}*/
			deletedPositions = new ArrayList<Position>();
			newSelectedPositions = new ArrayList<Position>();
			
			deleteSalesPersonLocation = new ArrayList<Location>();
			newSalespersonLocation = new ArrayList<Location>();
			
			newRestrictEmpLoc = new ArrayList<Location>();
			deleteRestrictEmpLoc = new ArrayList<Location>();
		}
		if(action.equalsIgnoreCase("resendinviteemloyee")){
			Integer id = Integer.parseInt(FacesUtils.getRequestParameter("id"));
			
			employee = getEmployeeService().getEmplyeeById(id,tenant);
			List<Position> assignedPositions = employee.getAssignedPositions();
			List<Position> preSelectedPositions = (this.selectedPositions==null)?new ArrayList<Position>():this.selectedPositions;
			for(Position position : assignedPositions){
				preSelectedPositions.add(position);
			}
			this.selectedPositions = preSelectedPositions;
			this.loadImagesToDisk();
			employee.setTenant(tenant);
			
			deletedPositions = new ArrayList<Position>();
			newSelectedPositions = new ArrayList<Position>();
			
			deleteSalesPersonLocation = new ArrayList<Location>();
			newSalespersonLocation = new ArrayList<Location>();
		}
		if(action.equalsIgnoreCase("saveemployee")){
			employee = new Employee(tenant,"active");
			employee.setRegion(regionBean.getSelectedRegion());
			employee.setEmpRate(new EmployeeRate());
			deletedPositions = new ArrayList<Position>();
			newSelectedPositions = new ArrayList<Position>();
		}
	}
	
	public String saveEmployee(){
		EmployeeRate employeeRate = employee.getEmpRate();
		Long millis = employeeRate.getRateStartDate().getTime()-24*60*60*1000;
		Date endDate = new Date(millis);
		employeeRate.setRateEnddate(endDate);
		getEmployeeService().saveEmployee(employee);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("Employee created Successfully");
		return "employeeHome?faces-redirect=true";
	}

	/**
	 * save employee to DB
	 * send invitation to employee
	 * show Message
	 * add new employee to session
	 * create a new employee for tenant
	 * @param event
	 */
	public void inviteEmployee(ActionEvent event){
		UUID uuid = UUID.randomUUID();
		String invitecode = uuid.toString().split("-")[0];
		employee.setInvitecode(invitecode);
		List<String> receipients = new ArrayList<String>();
		Map<Object,Object> attributes = null;
		List<Position> assignedPositions = selectedPositions;//new ArrayList<Integer>(positions.values());
		employee.setAssignedPositions(assignedPositions);//Ids(assignedPositionIds);
		
		try{
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
			FileInputStream inputStream = new FileInputStream(path+"/inviteimage.jpg");
            byte[] img_content = IOUtils.toByteArray(inputStream);
			employee.setImageType("image/jpeg");
			employee.setImageName(tenant.getCode()+"_"+"inviteimage.jpg");
			employee.setImageBytes(img_content);
			EmployeeRate inviteEmpRate = employee.getEmpRate();
			inviteEmpRate.setRateStartDate(new Date());
			Date empRateEndDate = EmployeeUtil.getEmployeeRateEndDate();
			inviteEmpRate.setRateEnddate(empRateEndDate);// set the date after 100 years
			Region currentRegion = regionBean.getSelectedRegion();
			employee.setRegion(currentRegion);
			
			employee = getEmployeeService().inviteEmployee(employee);
			
			attributes = populateEmailAttributes(employee);
			receipients.add(employee.getEmail());
			String sender = tenant.getEmailSender();// this is actually sender address which is companycode-noreply@schedule-cloud.com
			getInviteEmployeeSender().inviteEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(),sender,"invite employee");
			//getSender().inviteEmployee(inviteMessage, attributes, receipients,sender);
			//FacesUtils.addInfoMessage("sendInvBtn", " save sucessful");
			employee = new Employee(tenant,"invitesent");
			employee.setEmpRate(new EmployeeRate());
			FacesUtils.addInfoMessage("Information Saved,Invitation will be sent shortly");
		}catch (DataAccessException dae) {
			FacesUtils.addErrorMessage("Information couldn't be Saved, Data Error!");
			dae.printStackTrace();
		}catch (MailException me){
			me.printStackTrace();
			FacesUtils.addErrorMessage("Information Saved, Invitation Email not sent!");
			//getSender().inviteEmployee(inviteMessage, attributes, receipients,tenant.getCompanyEmail());
		}catch(Exception exception){
			exception.printStackTrace();
			FacesUtils.addErrorMessage("Error!");
		}
		
	}
	
	private Map<Object, Object> populateEmailAttributes(Employee employee) {
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put("emailsubject", "Schedule-Cloud INVITE for "+tenant.getName());
		attributes.put("companyName", tenantContext.getTenant().getName());
		String contextRoot = FacesUtils.getServletContext().getContextPath().substring(1);
		//attributes.put("smtphost", tenant.getEmailServer());
		attributes.put("contextRoot", contextRoot);
		attributes.put("firstname",employee.getFirstname());
		attributes.put("lastname", employee.getLastname());
		attributes.put("invitecode", employee.getInvitecode());
		attributes.put("empid",employee.getId());
		attributes.put("tenantId",tenant.getId());
		return attributes;
	}

	public void resendInviteEmployee(ActionEvent event){
		List<String> receipients = new ArrayList<String>();
		Map<Object,Object> attributes = null;
		List<Position> assignedPositions = selectedPositions;//new ArrayList<Integer>(positions.values());
		employee.setAssignedPositions(assignedPositions);//Ids(assignedPositionIds);
		try{
			EmployeeRate inviteEmpRate = employee.getEmpRate();
			inviteEmpRate.setRateStartDate(new Date());
			Date empRateEndDate = EmployeeUtil.getEmployeeRateEndDate();
			inviteEmpRate.setRateEnddate(empRateEndDate);// set the date after 100 years
			Region currentRegion = regionBean.getSelectedRegion();
			employee.setRegion(currentRegion);
			
			getEmployeeService().updateInviteEmployee(employee,newSelectedPositions,deletedPositions);
			
			attributes = populateEmailAttributes(employee);
			receipients.add(employee.getEmail());
			String sender = tenant.getEmailSender();
			getInviteEmployeeSender().inviteEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(),sender,"resend invite");
			//getSender().inviteEmployee(inviteMessage, attributes, receipients,sender);
			//FacesUtils.addInfoMessage("sendInvBtn", " save sucessful");
			employee = new Employee(tenant,"invitesent");
			employee.setEmpRate(new EmployeeRate());
			FacesUtils.addInfoMessage("Invitation Sent");
		}catch (MailException me){
			me.printStackTrace();
			FacesUtils.addErrorMessage("Information Saved, Email invite not sent!");
			//getSender().inviteEmployee(inviteMessage, attributes, receipients,tenant.getCompanyEmail());
		}catch(Exception exception){
			FacesUtils.addErrorMessage("Error!");
		}
		
	}
	public void selectedItemsChanged(ValueChangeEvent event) {
	    List<Position> oldPos = (event.getOldValue()==null) ? new ArrayList<Position>() : (List<Position>) event.getOldValue();
	    List<Position> newPos = (event.getNewValue()==null) ? new ArrayList<Position>() : (List<Position>) event.getNewValue();
	    List<Position> originalPositions = employee.getAssignedPositions();
	    
	    	for(Position oldPosition: oldPos){
	    		if(!newPos.contains(oldPosition))
	    			deletedPositions.add(oldPosition);
	    	}
	    
	    
	    	for(Position  newPosition: newPos){
	    		if(!oldPos.contains(newPosition))
	    			newSelectedPositions.add(newPosition);
	    	}
	    
	    
	}
	public void verifyRateDate(){
		Date stime = this.employee.getEmpRate().getRateStartDate();
    	Date etime = this.employee.getEmpRate().getOldRateStartDate();
    	if(stime !=null && etime !=null && DateUtil.compareDateOnly(stime,etime)<0){
    		
    		FacesUtils.addErrorMessage("effdate","can't change to ealier dates");
    	}else{
    		
    		RequestContext.getCurrentInstance().update("effDateMsg");
    	}
			
	}
	public void adminValueChanged(ValueChangeEvent valueChangeEvent){
		boolean oldAdminValue = (Boolean)valueChangeEvent.getOldValue();
		employee.setOldSCAdmin(oldAdminValue);
	}
	public void managerValueChanged(ValueChangeEvent valueChangeEvent){
		boolean oldManagerValue = (Boolean)valueChangeEvent.getOldValue();
		employee.setOldSCManager(oldManagerValue);
	}
	public void salesPersonValueChanged(ValueChangeEvent valueChangeEvent){
		boolean oldManagerValue = (Boolean)valueChangeEvent.getOldValue();
		employee.setOldSCManager(oldManagerValue);
	}
	public void filterSalesPersonLocations(ValueChangeEvent valueChangeEvent){
		
		List<Location> oldlocations = (valueChangeEvent.getOldValue()==null) ? new ArrayList<Location>() : (List<Location>) valueChangeEvent.getOldValue();
		List<Location> newlocations = (valueChangeEvent.getNewValue()==null) ? new ArrayList<Location>() : (List<Location>) valueChangeEvent.getNewValue();
	    List<Location> salesPersonLocations = employee.getAssignedSalesPersonLocations();
	    
	    	for(Location oldLocation: oldlocations){
	    		if(!newlocations.contains(oldLocation))
	    			deleteSalesPersonLocation.add(oldLocation);
	    	}
	    
	    
	    	for(Location  newLocation: newlocations){
	    		if(!oldlocations.contains(newLocation))
	    			newSalespersonLocation.add(newLocation);
	    	}
	    
	    
	}
	
	public void filterRestrictEmployeeLocations(ValueChangeEvent valueChangeEvent){
		
		List<Location> oldlocations = (valueChangeEvent.getOldValue()==null) ? new ArrayList<Location>() : (List<Location>) valueChangeEvent.getOldValue();
		List<Location> newlocations = (valueChangeEvent.getNewValue()==null) ? new ArrayList<Location>() : (List<Location>) valueChangeEvent.getNewValue();
	    
	    
	    	for(Location oldLocation: oldlocations){
	    		if(!newlocations.contains(oldLocation))
	    			deleteRestrictEmpLoc.add(oldLocation);
	    	}
	    
	    
	    	for(Location  newLocation: newlocations){
	    		if(!oldlocations.contains(newLocation))
	    			newRestrictEmpLoc.add(newLocation);
	    	}
	    
	    
	}
	/*
	 * update rules are different for admin and manager
	 */
	public String updateEmployee(){
		//List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(user.getAuthorities());
		//if(authorities.contains(new SimpleGrantedAuthority("ROLE_ACCESS_ADMIN"))){
			EmployeeRate employeeRate = employee.getEmpRate();
			/*Long millis = employeeRate.getRateStartDate().getTime()-24*60*60*1000;
			Date endDate = new Date(millis);
			employeeRate.setRateEnddate(endDate);*/
			if(employeeRate.getRateStartDate()==null)
				employeeRate.setRateStartDate(employee.getHireDate());
			employeeRate.setRateEnddate(new Date());
			getEmployeeService().editEmployee(true, employee,newSelectedPositions,deletedPositions,deleteSalesPersonLocation,newSalespersonLocation,newRestrictEmpLoc,deleteRestrictEmpLoc);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesUtils.addInfoMessage("Employee details updated Successfully");
		//}
	
		
		return "employeeHome?faces-redirect=true";
	}

	public void loadImagesToDisk(){
		//String path = FacesUtils.getServletContext().getRealPath("/images");
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
		File imageDir = new File(path+"/"+tenant.getCode());
		if (!imageDir.exists()) {
			if (imageDir.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}			
				File file = new File(imageDir+"/"+employee.getImageName());
				OutputStream output = null;
				try {
					output = new FileOutputStream(file);
					IOUtils.copy(employee.getInputStream(), output);
				} catch (Exception e) {
					// TODO: handle exception
				}finally {
					IOUtils.closeQuietly(output);
				}
			
		
	}
	
	public String deleteEmployee(){
		getEmployeeService().deleteEmployee(this.employee,tenant);
		
		return "employeeHome?faces-redirect=true";
	}
	public String createPassword(){
		
		return "/ui/login/createPassword?faces-redirect=true&action=editemployee&id="+this.employee.getId();
	}
	public String updatePassword(){
		ShaPasswordEncoder encoder = (ShaPasswordEncoder)ApplicationContextProvider.getSpringManagedBean("passwordEncoder");
    	String encrypted_passwd = encoder.encodePassword(this.employee.getNewPassword(),new String(this.employee.getUserName()));
    	getEmployeeService().resetPassword(this.employee.getUserName(),encrypted_passwd);
    	FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("Employee password updated Successfully");
    	return "/ui/admin/employee/employeeHome?faces-redirect=true";
	}
	
	
	private Object getExcelCellValue(HSSFCell hssfCell){
		int type = hssfCell.getCellType();
		Object value = null;
        if (type == HSSFCell.CELL_TYPE_STRING) {
        	value = hssfCell.getRichStringCellValue().toString();
            System.out.println("[" + hssfCell.getRowIndex() + ", "
                    + hssfCell.getColumnIndex() + "] = STRING; Value = "
                    + hssfCell.getRichStringCellValue().toString());
        } else if (type == HSSFCell.CELL_TYPE_NUMERIC) {
        	value = hssfCell.getNumericCellValue();
            System.out.println("[" + hssfCell.getRowIndex() + ", "
                    + hssfCell.getColumnIndex() + "] = NUMERIC; Value = "
                    + hssfCell.getNumericCellValue());
        } else if (type == HSSFCell.CELL_TYPE_BOOLEAN) {
        	value = hssfCell.getBooleanCellValue();
            System.out.println("[" + hssfCell.getRowIndex() + ", "
                    + hssfCell.getColumnIndex() + "] = BOOLEAN; Value = "
                    + hssfCell.getBooleanCellValue());
        } else if (type == HSSFCell.CELL_TYPE_BLANK) {
        	value = "";
            System.out.println("[" + hssfCell.getRowIndex() + ", "
                    + hssfCell.getColumnIndex() + "] = BLANK CELL");
        } else if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
            System.out.println("The cell contains a date value: " + hssfCell.getDateCellValue());
        }
        
        return value;
	}
	
	
	public EmployeeSupport getEmployeeSupport() {
		return employeeSupport;
	}

	public void setEmployeeSupport(EmployeeSupport employeeSupport) {
		this.employeeSupport = employeeSupport;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	/*public Map<String, Integer> getPositions() {
		return positions;
	}
*/
	public List<Position> getSelectedPositions() {
		return selectedPositions;
	}

	public void setSelectedPositions(List<Position> selectedPositions) {
		this.selectedPositions = selectedPositions;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}


	public boolean isAdminRoleChange() {
		return adminRoleChange;
	}


	public boolean isManagerRoleChange() {
		return managerRoleChange;
	}


	public boolean isSalesPersonRoleChange() {
		return salesPersonRoleChange;
	}


	public RegionBean getRegionBean() {
		return regionBean;
	}


	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}

	
	

	
	
	
}
