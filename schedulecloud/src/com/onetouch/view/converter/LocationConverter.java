package com.onetouch.view.converter;


import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.onetouch.model.domainobject.Location;

import com.onetouch.view.bean.admin.event.EventSupport;
import com.onetouch.view.bean.admin.location.LocationHome;
import com.onetouch.view.bean.admin.location.LocationSupport;

import com.onetouch.view.util.FacesUtils;



@FacesConverter(value="locationconverter")
public class LocationConverter
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
	            Integer locationId = Integer.parseInt( submittedValue );
	            LocationHome locationHome = (LocationHome)FacesUtils.getManagedBean("locationHome");
	            List<Location> locations = locationHome.getLocationList();
	            for(Location l: locations){
	            	if(l.getId().intValue()==locationId.intValue())
	            		return l;
	            }
	           
        	}catch(NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"));
            }
        }
        return null;
        
    }

    
    
    
    public String getAsString( FacesContext facesContext, UIComponent component, Object value )
    {
    	if ( value == null || value.equals( "" )) 
        {  
            return "";  
        } 
        else 
        {  
            return String.valueOf( ((Location) value).getId() );  
        }  
    }
}
