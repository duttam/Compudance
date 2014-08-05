package com.onetouch.view.bean.admin.location;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.exception.EmployeeExistException;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.context.TenantContext;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="locationController")
@ViewScoped
public class LocationController extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String action;
	private Location location;
	private Tenant tenant;
	private List<String> USTIMEZONES = new ArrayList<String>();
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	@PostConstruct
	public void init(){
		tenant = tenantContext.getTenant();
		
		action = FacesUtils.getRequestParameter("action");
		if(action.equalsIgnoreCase("addlocation")){
			location = new Location(tenant);
			UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
			if(authenticationToken!=null && authenticationToken.isAuthenticated()){
				CustomUser customUser = (CustomUser)authenticationToken.getPrincipal();
				if(customUser.isSalesPerson())
					location.setCode(getApplicationData().getCustomLocationCode());

			}
		}
		if(action.equalsIgnoreCase("editlocation")){
			Integer id = Integer.parseInt(FacesUtils.getRequestParameter("id"));
			location = getLocationService().getLocation(id);
		}
		
		USTIMEZONES.add("PST");
		 USTIMEZONES.add("EST");
		 USTIMEZONES.add("CST");
		 USTIMEZONES.add("MST");
	}
	public String clickAddLocation(){
		String action = FacesUtils.getRequestParameter("action");
		if(action.equalsIgnoreCase("addlocation"))
			location = new Location(tenant);
		
		return "addLocation?faces-redirect=true";
	}
	public String clickEditLocation(){
		String action = FacesUtils.getRequestParameter("action");
		if(action.equalsIgnoreCase("editlocation")){
			Integer id = Integer.parseInt(FacesUtils.getRequestParameter("id"));
			location = getLocationService().getLocation(id);
		}
		
		return "editLocation?faces-redirect=true";
	}
	public void changeLocationType(){
		if(this.location.getLocationType().equalsIgnoreCase("custom")){
			if(this.location.getCode()!=null && !this.location.getCode().equalsIgnoreCase(""))
				this.location.setCacheCode(this.location.getCode());
			this.location.setCode(getApplicationData().getCustomLocationCode());
		}
		else
			this.location.setCode(this.location.getCacheCode());
	}
	public String addLocation(){
		try{
			Region selectedRegion = regionBean.getSelectedRegion();
			location.setRegion(selectedRegion);
			getLocationService().addLocation(location);
			//locationHome.getLocationList().add(location);
			location = new Location(tenant);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesUtils.addInfoMessage("Location Saved Successfully");
		}catch (DataAccessException dae) {
			dae.printStackTrace();
		}
		return "locationHome?faces-redirect=true";
	}
	
	public String editLocation(){
		Region selectedRegion = regionBean.getSelectedRegion();
		location.setRegion(selectedRegion);
		getLocationService().editLocation(location);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("Location Updated Successfully");
		return "locationHome?faces-redirect=true";
	}
	public String deleteLocation(){
		Region selectedRegion = regionBean.getSelectedRegion();
		location.setRegion(selectedRegion);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		try{
		getLocationService().deleteLocation(location);
		FacesUtils.addInfoMessage("Location Deleted Successfully");
		}catch(DataIntegrityViolationException eee){
			FacesUtils.addErrorMessage("Employee exist for Location, can't delete");
		}
		
		
		return "locationHome?faces-redirect=true";
	}
	public Location getLocation() {
		
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	

	public List<String> getUSTIMEZONES() {
		return USTIMEZONES;
	}

	public void setUSTIMEZONES(List<String> uSTIMEZONES) {
		USTIMEZONES = uSTIMEZONES;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	public RegionBean getRegionBean() {
		return regionBean;
	}
	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}

	
	
}
