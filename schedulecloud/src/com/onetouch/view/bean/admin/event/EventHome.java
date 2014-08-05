package com.onetouch.view.bean.admin.event;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.util.SupportBean;

@ManagedBean(name="eventHome")
@ViewScoped
public class EventHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger("EventHome.class");
	private List<Event> eventList;
	private Location selectedLocation;
	private Tenant tenant;
	private CustomUser customUser;
	@ManagedProperty(value="#{supportBean}")
	private SupportBean supportBean;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	private List<Event> filteredEvents;
	
	// lazy loading events
	private LazyDataModel<Event> lazyEventModel;
	@PostConstruct
	public void initEventHome(){
		long startTime = System.currentTimeMillis();
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
			//Employee employee = supportBean.getCurrentLoggedEmployee();
			//customUser.setEmp_id(employee.getId());
		}
		tenant = tenantContext.getTenant();
		eventList = getEventService().getAllEvents(tenant,regionBean.getSelectedRegion(),customUser);
		long endTime = System.currentTimeMillis();
		System.out.println("That took " + (endTime - startTime) + " milliseconds");
	
		// lazy query
		//lazyEventModel = new LazyEventDataModel(getEventService());
		logger.warn("finish getting event data successfully");
	}
	@PreDestroy
	public void destroy()
	{
		System.out.println("Event Destroy");
	}
	public void selectLocationChange(){
		if(selectedLocation!=null){
			eventList = getEventService().getAllEventsByLocation(tenant, selectedLocation);
		}else{
			eventList = getEventService().getAllEvents(tenant,regionBean.getSelectedRegion(),customUser);
		}
	}
	public List<Event> getEventList() {
		return eventList;
	}

	public void setEventList(List<Event> eventList) {
		this.eventList = eventList;
	}

	public Location getSelectedLocation() {
		return selectedLocation;
	}

	public void setSelectedLocation(Location selectedLocation) {
		this.selectedLocation = selectedLocation;
	}

	public SupportBean getSupportBean() {
		return supportBean;
	}

	public void setSupportBean(SupportBean supportBean) {
		this.supportBean = supportBean;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}

	public List<Event> getFilteredEvents() {
		return filteredEvents;
	}

	public void setFilteredEvents(List<Event> filteredEvents) {
		this.filteredEvents = filteredEvents;
	}
	
	
}
