package com.onetouch.model.service.signinout;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.signinout.SignInOutDAO;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.Tenant;
@Service
public class SignInOutService implements ISignInOutService{

	@Autowired
	private SignInOutDAO signInOutDAO;
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public SignInOut saveSignIn(SignInOut signInOut){
		Integer signInOutId = signInOutDAO.saveSignInTime(signInOut);
		signInOut.setId(signInOutId);
		
		return signInOut;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateSignOut(SignInOut signInOut) {
		// TODO Auto-generated method stub
		signInOutDAO.updateSignOutTime(signInOut);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateBreakSignIn(SignInOut signInOut) {
		// TODO Auto-generated method stub
		signInOutDAO.updateBreakSignInTime(signInOut);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateBreakSignOut(SignInOut signInOut) {
		// TODO Auto-generated method stub
		signInOutDAO.UpdateBreakSignOutTime(signInOut);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateRemarks(SignInOut signInOut) {
		signInOutDAO.updateRemark(signInOut);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Event> getAllPublishedEvents(Tenant tenant,CustomUser customUser) {
		Integer companyId = (tenant!=null) ? tenant.getId() : null;
		List<Event> eventList = signInOutDAO.findAllSignInOutEvents(companyId,customUser);
		
		return eventList;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Employee> getAllSignInOutByEmployee(Event event, Tenant tenant) {
		List<Employee> employeeSignInOutList = signInOutDAO.findAllByEmployee(event.getId(), tenant.getId());
		
		return employeeSignInOutList;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Integer getSignInOutIdIfExist(Event event, Tenant tenant) {
		return signInOutDAO.findIfExist(event.getId(),tenant.getId());
	}
	
}
