package com.onetouch.model.service.signinout;

import java.util.List;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.Tenant;

public interface ISignInOutService {
	public List<Event> getAllPublishedEvents(Tenant tenant, CustomUser customUser);
	public SignInOut saveSignIn(SignInOut signInOut);
	public void updateSignOut(SignInOut signInOut);
	public void updateBreakSignIn(SignInOut signInOut);
	public void updateBreakSignOut(SignInOut signInOut);
	public void updateRemarks(SignInOut signInOut);
	public List<Employee> getAllSignInOutByEmployee(Event event, Tenant tenant);
	public Integer getSignInOutIdIfExist(Event event, Tenant tenant);
	
}
