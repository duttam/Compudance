package com.onetouch.model.service.location;

import java.util.List;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;

public interface ILocationService {

	public List<Location> getAllLocation(Tenant tenant,String locationType, Region region, CustomUser customUser);
	public void addLocation(Location location);
	public void editLocation(Location location);
	public Location getLocation(Integer id);
	public List<Region> getAllRegions(Tenant tenant);
	public List<Location> getAllLocationByRegion(Region region);
	public void deleteLocation(Location location);
}
