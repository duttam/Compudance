package com.onetouch.view.converter;


import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.Location;
import com.onetouch.model.util.ApplicationContextProvider;

import com.onetouch.view.bean.admin.event.EventHome;
import com.onetouch.view.bean.admin.event.EventSupport;
import com.onetouch.view.bean.admin.location.LocationSupport;
import com.onetouch.view.bean.admin.schedule.ScheduleHome;
import com.onetouch.view.bean.admin.signinout.SignInOutHome;
import com.onetouch.view.bean.menu.MainMenuBean;
import com.onetouch.view.bean.useremployee.request.TimeOffBean;
import com.onetouch.view.context.TenantContext;

import com.onetouch.view.util.FacesUtils;



@FacesConverter(value="eventconverter")
public class EventConverter
    implements 
        Converter
{

    public Object getAsObject( FacesContext facesContext, UIComponent component, String submittedValue )
    {
        if ( submittedValue.trim().equals( "" ) )
        {
            return null;
        } 
        else 
        {
        	try{
	            Integer eventId = Integer.parseInt( submittedValue );
	            
	            EventHome eventHome = (EventHome)FacesUtils.getManagedBean("eventHome");
	            
	            List<Event> events = eventHome.getEventList();
	            if(events ==null || events.size()==0){
	            	ScheduleHome scheduleHome = (ScheduleHome)FacesUtils.getManagedBean("adminScheduleHome");
	    	        
	            	events = scheduleHome.getEventList();
	            	if(events==null || events.size()==0){
	            		SignInOutHome signInOutHome = (SignInOutHome)FacesUtils.getManagedBean("signInOut");
	            		events = signInOutHome.getPublishedEventList();
	            		if(events==null || events.size()==0){
	            			TimeOffBean timeOffBean = (TimeOffBean)FacesUtils.getManagedBean("timeoffBean");
	            			events = timeOffBean.getSickEventList();
	            		}
	            	}
	    	    }
	             
	            if(events!=null){
		            for(Event event : events){
		            	if(event.getId().intValue()==eventId.intValue())
		            		return event;
		            }
	            }
	        
	           
        	}catch(NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"));
            }
        }
        return null;
        
    }
    
    public String getAsString( FacesContext facesContext, UIComponent component, Object value )
    {
    	if ( value == null || value.equals( "" ) ) 
        {  
            return "";  
        } 
        else 
        {  
            return String.valueOf( ((Event) value).getId() );  
        }  
    }
}
