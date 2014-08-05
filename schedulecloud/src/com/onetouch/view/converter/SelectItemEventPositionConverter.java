package com.onetouch.view.converter;


import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;

import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;

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



@FacesConverter(value="selectitemeventpositionconverter")
public class SelectItemEventPositionConverter extends SelectItemsConverter
   
{

    
    
    public String getAsString( FacesContext facesContext, UIComponent component, Object value )
    {
    	if ( value == null || value.equals( "" ) ) 
        {  
            return "";  
        } 
        else 
        {  
            return String.valueOf( ((EventPosition) value).getId() );  
        }  
    }
}
