package com.onetouch.view.bean.useremployee.request;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.view.bean.BaseBean;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TimeOffRequest;

@ManagedBean(name="timeoffHome")
@ViewScoped
public class TimeOffHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private Employee employee;
	private CustomUserDetail user;
	private Tenant tenant;

	private List<TimeOffRequest> timeoffList;
	
	
	public TimeOffHome(){
		
	}
	
	@PostConstruct
	public void initTimeOffListHome(){
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		
		tenant = tenantContext.getTenant();
			employee = getEmployeeService().getEmplyeePositionById(user.getEmp_id(), tenant);
			employee.setTenant(tenant);
	
		timeoffList = getTimeoffService().getAllTimeOffRequests(user.getEmp_id());
	}

	public List<TimeOffRequest> getTimeoffList() {
		return timeoffList;
	}

	public void setTimeoffList(List<TimeOffRequest> timeoffList) {
		this.timeoffList = timeoffList;
	}

	
	
	
	
	
}
