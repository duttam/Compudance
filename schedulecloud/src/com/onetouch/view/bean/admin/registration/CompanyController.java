package com.onetouch.view.bean.admin.registration;



import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.util.EmployeeUtil;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="companyController")
@ViewScoped
public class CompanyController extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Tenant tenant;
	private Tenant company; // company being added or modified
	private String action;
	private Region region;
	@PostConstruct
	public void initTenantHome(){
		tenant = tenantContext.getTenant();
		CustomUser customUser=null;
		if(tenant == null){
			UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
			if(authenticationToken.isAuthenticated())
				customUser = (CustomUser)authenticationToken.getPrincipal();
			
		}
		action = FacesUtils.getRequestParameter("action");
		if(action!=null && action.equalsIgnoreCase("addcompany")){
			company = new Tenant();
			Employee adminEmployee = new Employee();
			adminEmployee.setScAdmin(true);
			adminEmployee.setScManager(true);
			EmployeeRate employeeRate = new EmployeeRate();
			adminEmployee.setEmpRate(employeeRate);
			company.setAdminEmployee(adminEmployee);
			region = new Region();
			
			//userCredentials = new LinkedHashMap<String,String>();
			
		}
		if(action!=null && action.equalsIgnoreCase("editcompany")){
			Integer id = Integer.parseInt(FacesUtils.getRequestParameter("id"));
			
			company = getTenantService().findTenant(id);
		}
		
	}
	
	/*public void addAdminCredential(ActionEvent actionEvent){
		
		userCredentials.put(company.getUsername(),company.getPassword());
		company.setUsername("");
		company.setPassword("");
	}*/
	public void updateServerEmail(){
		String code = company.getCode();
		String emailPrefix = code+"-NoReply";
		company.setEmailSenderPrefix(emailPrefix);
	}
	public String registercompany(){
		try{
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
			FileInputStream inputStream = new FileInputStream(path+"/inviteimage.jpg");
            byte[] img_content = IOUtils.toByteArray(inputStream);
			company.getAdminEmployee().setImageType("image/jpeg");
			company.getAdminEmployee().setImageName(tenant.getCode()+"_"+"inviteimage.jpg");
			company.getAdminEmployee().setImageBytes(img_content);
			String email = company.getEmailSenderPrefix()+"@schedule-cloud.com";
			company.setEmailSender(email);
			getTenantService().addTenant(company,region);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesUtils.addInfoMessage("Company has been registered Successfully");
			
		}catch (DataAccessException dae) {
			dae.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "companyHome?faces-redirect=true";
	}
	
	
	public String UpdateCompany(){
		String email = company.getEmailSenderPrefix()+"@schedule-cloud.com";
		company.setEmailSender(email);
		getTenantService().updateTenant(company);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("Updated Successfully");
		return "companyHome?faces-redirect=true";
	}
	public String DisableCompany(){
		getTenantService().disableTenant(company);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("Company is disabled");
		return "companyHome?faces-redirect=true";
	}
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public Tenant getCompany() {
		return company;
	}

	public void setCompany(Tenant company) {
		this.company = company;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}
	
	/*public List<Entry<String, String>> getUserCredentials() {
		List<Entry<String, String>> users = new ArrayList<Entry<String, String>>(userCredentials.entrySet());
		
		return users;
	}
	public void setUserCredentials(Map<String, String> userCredentials) {
		this.userCredentials = userCredentials;
	}*/
	
	
}
