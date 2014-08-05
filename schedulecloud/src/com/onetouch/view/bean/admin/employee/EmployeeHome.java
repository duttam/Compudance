package com.onetouch.view.bean.admin.employee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.event.EventSupport;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.menu.MainMenuBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.comparator.EmployeeNameComparator;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="employeeHome") 
@ViewScoped
public class EmployeeHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger("EmployeeHome.class");
	private List<Employee> employeeList;
	private List<Employee> filteredEmployeeList;
	private String employeeName;
	private Employee selectedEmployee; 
	private String employeeStatus;
	private Tenant tenant;
	private Position selectedPosition;
	private Integer employeeId;
	//private SelectItem[] empStatus;  
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	
	
	public EmployeeHome(){
		
	}
	
	@PostConstruct
	public void initEmployeeHome(){
		long startTime = System.currentTimeMillis();
		tenant = tenantContext.getTenant();
		employeeStatus = "active";
		if(FacesUtils.isMobileRequest()){
			employeeList = getEmployeeService().getAllEmployee(tenant, new String[]{employeeStatus},regionBean.getSelectedRegion());
			loadImagesToDisk(employeeList);
			Collections.sort(employeeList,new EmployeeNameComparator());
		}
		else{
			Region currentRegion = regionBean.getSelectedRegion();
			employeeList = getEmployeeService().getAllEmployee(tenant, new String[]{employeeStatus},currentRegion);
			Collections.sort(employeeList,new EmployeeNameComparator());
		}
		long endTime = System.currentTimeMillis();
		//System.out.println("That took " + (endTime - startTime) + " milliseconds");
		
		logger.warn("finish getting employee data successfully");
		
		
		
	}
	@PreDestroy
	public void destroy()
	{
		System.out.println("Employee Destroy");
	}
	/*private SelectItem[] createFilterOptions()  {  
        SelectItem[] options = new SelectItem[4];  
  
        options[0] = new SelectItem("", "Select");  
        options[1] = new SelectItem("Active","active");
        options[2] = new SelectItem("Inactive","inactive");
        options[3] = new SelectItem("Invite Sent","invitesent");
  
        return options;  
    }  
	
	public SelectItem[] getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(SelectItem[] empStatus) {
		this.empStatus = empStatus;
	}*/

	public void selectEmployeeStatusChange(AjaxBehaviorEvent ajaxBehaviorEvent){
		if(employeeStatus!=null && !employeeStatus.equals("")){
			employeeList = getEmployeeService().getAllEmployee(tenant,new String[]{employeeStatus},regionBean.getSelectedRegion());
			//RequestContext.getCurrentInstance().update("employeeForm:employeetable");
		}
		
	}
	public void selectPositionChange(){
		if(selectedPosition!=null){
			employeeList = getEmployeeService().getAllEmployeeByPosition(selectedPosition.getId(), regionBean.getSelectedRegion(),tenant.getId());
		}
		else{
			employeeList = getEmployeeService().getAllEmployee(tenant, new String[]{employeeStatus},regionBean.getSelectedRegion());
		}
	}
	/**
	 * search employee functionality
	 * @return
	 */
	/*public List<Employee> showEmployeeHint( String searchWord ) {
		
		List<Employee> suggestedEmployee = new ArrayList<Employee>();
		for(Employee employee : employeeList){
			if(employee.getFirstname().startsWith(searchWord))
				suggestedEmployee.add(employee);
		}
        return suggestedEmployee;
    }*/
	public void loadImagesToDisk(List<Employee> allEmployee){
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
		for(Employee employee : allEmployee){
			
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
	}
	
	public String selectEmployee(){
		selectedEmployee = getEmployeeService().getEmplyeeById(employeeId,tenant);
		return "pm:employeedetails";
	}
	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Position getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(Position selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public List<Employee> getFilteredEmployeeList() {
		return filteredEmployeeList;
	}

	public void setFilteredEmployeeList(List<Employee> filteredEmployeeList) {
		this.filteredEmployeeList = filteredEmployeeList;
	}
	
	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
	
	/*
	 * filter employee list for mobile admin employee module
	 */
	public void filterEmployeeList(String status){
		if(status!=null)
			employeeList = getEmployeeService().getAllEmployee(tenant,new String[]{status},regionBean.getSelectedRegion());
		else
			employeeList = getEmployeeService().getAllEmployee(tenant,new String[]{"active"},regionBean.getSelectedRegion());
	}
	
	public String employeeSelected(){
		if(selectedEmployee.getEmployeeStatus().equalsIgnoreCase("active"))
			return "editEmployee?faces-redirect=true&action=editemployee&id="+selectedEmployee.getId();
		else
			return "inviteEmployee?faces-redirect=true&action=resendinviteemloyee&id="+selectedEmployee.getId();
			
	}
	
	
}
