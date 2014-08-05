package com.onetouch.model.service.location;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.location.LocationDAO;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.util.ApplicationData;
@Service(value="locationService")
public class LocationService implements ILocationService{

	@Autowired
	private LocationDAO locationDAO;
	
	@Autowired
	private ApplicationData applicationData;
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Location> getAllLocation(Tenant tenant,String locationType,Region region,CustomUser customUser) {
		Integer companyId = (tenant!=null) ? tenant.getId() : null ;
		return locationDAO.findAll(companyId,locationType,region.getId(),customUser);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void addLocation(Location location) {
		String statename = location.getState().getStateName();
		String statecode = applicationData.getStateCode(statename);
		location.getState().setStateCode(statecode);
		/*if(location.getContactPhone()!=null){
			String contactphone = ApplicationData.getPhoneNumber(location.getContactPhone());
			location.setContactPhone(contactphone);
		}
		if(location.getContactCellphone()!=null){
			String cellphone = ApplicationData.getPhoneNumber(location.getContactCellphone());
			location.setContactCellphone(cellphone);
		}
		if(location.getContactFax()!=null){
			String fax = ApplicationData.getPhoneNumber(location.getContactFax());
			location.setContactFax(fax);
		}*/
		location = formatPhoneNumber(location);
		locationDAO.save(location);
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void editLocation(Location location) {
		String statename = location.getState().getStateName();
		String statecode = applicationData.getStateCode(statename);
		location.getState().setStateCode(statecode);
		location = formatPhoneNumber(location);
		locationDAO.update(location);
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Location getLocation(Integer id) {
		
		return locationDAO.findById(id);
	}
	 private Location formatPhoneNumber(Location location){
		 if(location.getContactPhone()!=null){
				String contactphone = ApplicationData.getPhoneNumber(location.getContactPhone());
				location.setContactPhone(contactphone);
			}
			if(location.getContactCellphone()!=null){
				String cellphone = ApplicationData.getPhoneNumber(location.getContactCellphone());
				location.setContactCellphone(cellphone);
			}
			if(location.getContactFax()!=null){
				String fax = ApplicationData.getPhoneNumber(location.getContactFax());
				location.setContactFax(fax);
			}
			
			return location;
	 }

	@Override
	public List<Region> getAllRegions(Tenant tenant) {
		return locationDAO.findAllRegions(tenant.getId());
	}

	@Override
	public List<Location> getAllLocationByRegion(Region region) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteLocation(Location location) {
		locationDAO.deleteById(location.getId(),location.getRegion().getId(),location.getTenant().getId());
	}

	
}
