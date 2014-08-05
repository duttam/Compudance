package com.onetouch.model.service.availability;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.availability.AvailabilityDAO;
import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.util.DateUtil;
@Service

public class EmployeeAvailability implements IEmployeeAvailability{

	@Autowired
	private AvailabilityDAO availabilityDAO;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Availability> getAllAvailability(Tenant tenant,
			Employee employee) {
		
		List<Availability> allAvailabilities = availabilityDAO.findAll(tenant.getId(),employee.getId());
		
		if(allAvailabilities!=null && allAvailabilities.size()>0)
			return allAvailabilities;
		else
			return new ArrayList<Availability>();
	}

	

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateAvailability(Availability availability) {
		availabilityDAO.update(availability);
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Availability> addAvailability(List<Availability> availabilities) {
		
		Iterator<Availability> availIte = availabilities.iterator();
		while(availIte.hasNext()){
			Availability availability = availIte.next();
			Integer availId = availabilityDAO.save(availability);
			availability.setId(availId);
		}
		return availabilities;
	}



	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Availability getAvailability(Integer availId, Employee employee,
			Tenant tenant) {
		return availabilityDAO.find(availId, employee.getId(), tenant.getId());
	}



	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteAvailability(Integer availId, Employee employee,
			Tenant tenant) {
		availabilityDAO.delete(availId, employee.getId(),tenant.getId());
	}



	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public boolean overlapAvailability(Availability availability,
			Employee employee, Tenant tenant) {
		Date startDate = availability.getStartDate();
		Date endDate = availability.getEndDate();
		if(startDate==null)
			startDate = new Date();
		if(endDate == null)
			endDate = new Date();
		List<Availability> allAvailabilityByDate = availabilityDAO.findAllAvailableTime(startDate, endDate, employee.getId(), tenant.getId());
		boolean avail_overlap=false;
		for(Availability avail : allAvailabilityByDate){
			Date ast = availability.getStartTime();
			Date aet = availability.getEndTime();
			if(ast!=null && aet!=null){
				
				Date epst = avail.getStartTime();
				Date epet = avail.getEndTime();
				int a = DateUtil.compareTimeOnly(epst,ast);
				int b = DateUtil.compareTimeOnly(ast,epet);
				int c = DateUtil.compareTimeOnly(epst,aet);
				int d = DateUtil.compareTimeOnly(aet,epet);
				
				if((a<=0 && b <=0)||(c<=0 && d<=0))
				{
					avail_overlap = true;
					break;
				}
				if(a>=0 && d>=0)
					avail_overlap = true;
					break;
			}
		}
		
		return avail_overlap;
	}



	@Override
	public List<Availability> getAllAvailabilityByDate(Tenant tenant,
			Employee employee, Date date) {
		return availabilityDAO.findAllAvailableTime(date,date,employee.getId(),tenant.getId());
	}


	

}
