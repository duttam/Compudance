package com.onetouch.view.bean.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
 
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.ws.RequestWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jni.User;
import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.util.ApplicationContextProvider;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.menu.MainMenuBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.filter.TenantAwareHttpServletRequest;
import com.onetouch.view.filter.TenantFilter;
import com.onetouch.view.util.FacesUtils;


@ManagedBean(name="loginBean") 
@RequestScoped
public class LoginBean extends BaseBean{
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
    private String password;
    private String oldPassword;
    private Tenant tenant;
    private String resetPasswdEmail;
    
    public LoginBean() {
    }
    @PostConstruct
    public void init(){
    	UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken!=null && authenticationToken.isAuthenticated()){
			CustomUser customUser = (CustomUser)authenticationToken.getPrincipal();
			this.username = customUser.getUsername();
			tenant = customUser.getTenant();
		}
		
    }
    public String doLogin() throws IOException, ServletException {
    	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
    	 
        RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
                 .getRequestDispatcher("/j_spring_security_check");
 
        dispatcher.forward((ServletRequest) context.getRequest(),
                (ServletResponse) context.getResponse());
 
        FacesContext.getCurrentInstance().responseComplete();
        //RequestContext.getCurrentInstance().update("headerForm:loginMenu");
        // It's OK to return null here because Faces is just going to exit.
        return null;
    }
    
    public void doLogin(ActionEvent actionEvent)  throws IOException, ServletException {
    	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
    	 
        RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
                 .getRequestDispatcher("/j_spring_security_check");
 
        dispatcher.forward((ServletRequest) context.getRequest(),
                (ServletResponse) context.getResponse());
 
        FacesContext.getCurrentInstance().responseComplete();
        
    }

    public String logoff() throws java.io.IOException {
    	
    	String contextRoot = FacesUtils.getServletContext().getContextPath();
        FacesContext.getCurrentInstance().getExternalContext().redirect(contextRoot+"/j_spring_security_logout");
        
        return null;
    }
    public String loadEmployeeTemplate()throws IOException, ServletException{
    	//SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
    	//supportBean.setActiveTabIndex(0);
    	//supportBean.setHomeUrlTemplate("employeecontenttemplate.xhtml");
    	MainMenuBean mainMenuBean = (MainMenuBean)FacesUtils.getManagedBean("mainMenuBean");
    	mainMenuBean.setActiveMenuIndex(0);
	    String path = "/j_spring_security_switch_user" + "?j_username=" + this.username;
	     
	        // Forward the original request with username as query params          
	    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	    RequestDispatcher dispatcher = ((ServletRequest) context.getRequest()).getRequestDispatcher(path);
	    dispatcher.forward( (ServletRequest) context.getRequest(), (ServletResponse) context.getResponse() );
	    FacesContext.getCurrentInstance().responseComplete();
    return null;
    
    	
    }
    public String policyAccept(){
    	return null;
    }
    private String getTenantIdFromHttpRequest(RequestWrapper requestWrapper){
    	//TenantAwareHttpServletRequest tenantRequest = requestWrapper.
    	
    	return null;
    }
    public String changePassword(){
    	return "/ui/login/changePassword.jsf?faces-redirect=true";
    	
    }
    public void updatePassword(ActionEvent actionEvent){
    	getEmployeeService().updatePassword(username, oldPassword, password);
    	FacesUtils.addInfoMessage("Password Updated Successfully");
    }
    
    public String resetPasswordAndNotify(){
    	List<String> receipients = new ArrayList<String>();
    	try{
    		UUID uuid = UUID.randomUUID();
    		String tempPassword = uuid.toString().split("-")[0];
    		String username = getEmployeeService().getEmployeeUsernameByEmail(resetPasswdEmail);
        	String encrypted_passwd = getEncodedPassword(username,tempPassword);
    		getEmployeeService().resetPassword(username,encrypted_passwd);
    		Map<Object, Object> attributes = new HashMap<Object, Object>();
    		attributes.put("emailsubject", "Schedule-Cloud Reset Password ");
    		attributes.put("temp_password", tempPassword);
    		receipients.add(this.resetPasswdEmail);
    		getSender().sendTempPasswordEmployee(resetPasswordMessage, attributes, receipients);
    		
			FacesUtils.addInfoMessage("A new password has been sent to the email address mentioned below");
			//return "eventHome?faces-redirect=true";
    	}catch(MailException exception){
    		exception.printStackTrace();
    	}catch(EmptyResultDataAccessException emptyResultDataAccessException){
    		FacesUtils.addInfoMessage("Youe email address is not associated with any user account, please contact administrator");
    	}catch(IncorrectResultSizeDataAccessException incorrectResultSizeDataAccessException ){
    		FacesUtils.addInfoMessage("Youe email address is associated with multiple user accounts, please contact administrator");
    	}
	    
    	return null;//"login.jsf?faces-redirect=true";
    }

    public boolean oldPasswdExist(String username, String oldPasswd,Integer companyId ){
        return getEmployeeService().verifyOldPassword(username, oldPasswd,companyId );
    }
    
    public void createEncryptedPassword(ActionEvent event){
    	//ShaPasswordEncoder encoder = (ShaPasswordEncoder)ApplicationContextProvider.getSpringManagedBean("passwordEncoder");
    	String encrypted_passwd = getEncodedPassword(this.username,this.password); 
    		//encoder.encodePassword(this.password,new String(this.username));
    	FacesUtils.addInfoMessage("Encrypted Password " + encrypted_passwd);
    }
    public String getEncodedPassword(String username,String rawPassword){
    	ShaPasswordEncoder encoder = (ShaPasswordEncoder)ApplicationContextProvider.getSpringManagedBean("passwordEncoder");
    	String encrypted_passwd = encoder.encodePassword(rawPassword,new String(username));
    	return encrypted_passwd;
    }
    public void update(ActionEvent actionEvent){
		try{
			
			getTenantService().updateTenant(tenant);
			
		}catch (DataAccessException dae) {
			dae.printStackTrace();
		} 
		
		
	}
    public String getPassword() {
        return password;
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public String getResetPasswdEmail() {
		return resetPasswdEmail;
	}
	public void setResetPasswdEmail(String resetPasswdEmail) {
		this.resetPasswdEmail = resetPasswdEmail;
	}
	
	
    
}