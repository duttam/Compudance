package com.onetouch.view.bean.useremployee.employee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="userEmployeeImage") 
@SessionScoped
public class UserEmployeeImageBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*public String acceptPolicy(){
		CustomUser user = null;
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		Tenant tenant = tenantContext.getTenant();//get Tenant from session scoped bean
		//getTenantService().saveAcceptPolicy(user.getUsername(), tenant.getId(), true);
		//user.setAccept_policy(true);
		return "employee/employeeHome?faces-redirect=true";
	}*/
	
	public StreamedContent getLoadImage() {
	    InputStream dbStream;
	    StreamedContent dbImage=null;
	    CustomUserDetail user = null;
	    FacesContext context = FacesContext.getCurrentInstance();
	    Tenant tenant = null;
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }else{
        	try {
        		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
        		if(authenticationToken.isAuthenticated())
        			user = (CustomUser)authenticationToken.getPrincipal();
        		
        		tenant = user.getTenant();
        		Employee  employee = getEmployeeService().getEmplyeePositionById(user.getEmp_id(), tenant);
        		
        	    dbStream = employee.getInputStream();
        	    //dbStream = new ByteArrayInputStream(this.imageBytes);

        	    dbImage = new DefaultStreamedContent(dbStream,employee.getImageType(),employee.getImageName());
        	    }catch (Exception e) {
        			// TODO: handle exception
        		}
        	    return dbImage;
        }
	    
	}
	
}
