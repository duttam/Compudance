package com.onetouch.view.bean.admin.location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.OneTouchReport;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.util.FacesUtils;

 
@ManagedBean(name="locationHome")
@ViewScoped
public class LocationHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Location> locationList;
	private List<SelectItem> locationListByType;
	private String locationType;
	private Tenant tenant;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	CustomUser customUser=null;
	@PostConstruct
	public void initLocationHome(){

		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			customUser = (CustomUser)authenticationToken.getPrincipal();
		tenant = customUser.getTenant();
		SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
		supportBean.setTenant(tenant);
		if(tenantContext.getTenant()==null)
			tenantContext.setTenant(tenant);

		Region region = regionBean.getSelectedRegion();
		
		locationList = new ArrayList<Location>();
		populateGroupByLocation();
		System.out.print("");
	}
	@PreDestroy
	public void destroy()
	{
		System.out.println("Location Destroy");
	}
	private void populateGroupByLocation() {
		locationListByType = new ArrayList<SelectItem>();
            
		
        
		List<Location> standardLocationList = getLocationService().getAllLocation(tenant,"standard",regionBean.getSelectedRegion(),customUser);
        SelectItemGroup sig = new SelectItemGroup("standard");
        SelectItem[] selectItems = new SelectItem[standardLocationList.size()]; 
        	for(int i = 0;i<standardLocationList.size();i++){
        		Location location = standardLocationList.get(i);
        		SelectItem item = new SelectItem(location,location.getLocationTitle()+" "+location.getName());
        		selectItems[i] = item;
        	}
        sig.setSelectItems(selectItems);
        locationListByType.add(sig);
        
        List<Location> customLocationList = getLocationService().getAllLocation(tenant,"custom",regionBean.getSelectedRegion(),customUser);
        SelectItemGroup csig = new SelectItemGroup("custom");
        SelectItem[] cselectItems = new SelectItem[customLocationList.size()]; 
        	for(int i = 0;i<customLocationList.size();i++){
        		Location location = customLocationList.get(i);
        		SelectItem item = new SelectItem(location,location.getName());
        		cselectItems[i] = item;
        	}
        csig.setSelectItems(cselectItems);
        locationListByType.add(csig);
        locationList.addAll(standardLocationList);
        locationList.addAll(customLocationList);
        
	}
	public void selectLocationTypeChange(){
		if(locationType!=null && !locationType.equals(""))
			locationList = getLocationService().getAllLocation(tenant,locationType,regionBean.getSelectedRegion(),customUser);
	}
	
	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}
	public String getLocationType() {
		return locationType;
	}
	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
	public List<SelectItem> getLocationListByType() {
		return locationListByType;
	}
	public void setLocationListByType(List<SelectItem> locationListByType) {
		this.locationListByType = locationListByType;
	}
	
	
	
	
	
}
