package com.onetouch.view.bean.admin.event;

import java.util.List;



import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

import com.onetouch.model.domainobject.EventType;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.context.TenantContext;

@ManagedBean(name="eventTypeHome")
@ViewScoped
public class EventTypeBean extends BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<EventType> allEventTypes;
	private EventType eventType;
	private Tenant tenant;
	
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;

	@PostConstruct
	public void init(){
		tenant = tenantContext.getTenant();
		allEventTypes = getEventService().getAllEventTypes(tenant);//getAllEventTypesByRegion(tenant,regionBean.getSelectedRegion());
	}
	public void addNewEventType(ActionEvent actionEvent){
		eventType = new EventType();
		eventType.setTenant(tenant);
		eventType.setRegion(regionBean.getSelectedRegion());
	}
	public void saveEventType(ActionEvent actionEvent){
		
		eventType = getEventService().addEventType(eventType);
		allEventTypes.add(eventType);
		
	}
	
	public void editEventType(){
		
		System.out.print(eventType.getName());
		if(eventType!=null){
			RequestContext.getCurrentInstance().update("editeventtypeDialog");
		}
	}
	public void updateEventType(ActionEvent actionEvent){
		eventType.setTenant(tenant);
		
		getEventService().updateEventType(eventType);
		
		
	}
	public List<EventType> getAllEventTypes() {
		return allEventTypes;
	}

	public void setAllEventTypes(List<EventType> allEventTypes) {
		this.allEventTypes = allEventTypes;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
	
	

}
