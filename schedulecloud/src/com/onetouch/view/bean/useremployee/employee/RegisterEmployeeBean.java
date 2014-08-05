package com.onetouch.view.bean.useremployee.employee;



import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.servlet.http.HttpServletRequest;


import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;



import com.onetouch.model.domainobject.Employee;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.exception.UserAlreadyFoundException;
import com.onetouch.view.bean.BaseBean;

import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.ValidationMessages;


@ManagedBean(name="registerEmployeeBean") 
@ViewScoped
public class RegisterEmployeeBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Employee employee;
	private Tenant tenant;
	private String inviteCode;
	private String username;
	private String password;
	private Integer employeeId;
	private Integer tenantId;
	
	
	//@PostConstruct
	public void init(){
		if (!FacesContext.getCurrentInstance().isPostback()){ 
			String employeeName = getEmployeeService().userExist(new Employee(employeeId,new Tenant(tenantId)));
			if(employeeName!=null && !employeeName.equalsIgnoreCase(""))
				FacesUtils.addErrorMessage("Employee already exist in the system. Please contact administrator for user credential");
		//return null;
		}
	}
	
	public boolean userAlreadyExist(String username){
		return  getEmployeeService().isUsernameAlreadyExist(username);
		
	}
	public String registerEmployee()throws RuntimeException{
		try{
			tenant = getTenantService().findTenant(tenantId);
			//employee = getEmployeeService().getEmplyeeById(employeeId, tenant);
			employee = getEmployeeService().getEmplyeePositionById(employeeId, tenant);
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			HttpServletRequest servletRequest = FacesUtils.getServletRequest();
			getEmployeeService().createUser(employee, username, password, tenant);
			/*if(employee.getInvitecode()!=null && employee.getInvitecode().equals(inviteCode)){
				if(employee.isAdmin())
					getEmployeeService().createUser(employeeId, username, password, "admin", tenant);
				else if(employee.isManager())
					getEmployeeService().createUser(employeeId, username, password, "manager", tenant);
				else
					getEmployeeService().createUser(employeeId, username, password, "employee", tenant);
			}*/
			/*RequestDispatcher dispatcher = (servletRequest).getRequestDispatcher("/j_spring_security_check");
   
		    try {
				dispatcher.forward((ServletRequest) context.getRequest(),(ServletResponse) context.getResponse());
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		
		}catch (DuplicateKeyException dke) {
			String message = ValidationMessages.getString(ValidationMessages.ERROR_MESSAGE_RESOURCES, "duplicateUsername", new Object[]{username});
			throw new UserAlreadyFoundException(message);
			
			
		}
		catch (DataAccessException dae) {
			throw dae;
			
		}
		return "/ui/registrationConfirm?faces-redirect=true";
		/*FacesContext.getCurrentInstance().responseComplete();
        return null;*/
	}
	
	
	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public Integer getEmployeeId() {
		return employeeId;
	}


	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}
	
	
    
}
