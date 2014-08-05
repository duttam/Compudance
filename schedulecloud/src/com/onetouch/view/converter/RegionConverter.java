package com.onetouch.view.converter;


import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Region;

import com.onetouch.view.bean.admin.event.EventSupport;
import com.onetouch.view.bean.admin.location.LocationHome;
import com.onetouch.view.bean.admin.location.LocationSupport;
import com.onetouch.view.bean.admin.location.RegionBean;

import com.onetouch.view.util.FacesUtils;



@FacesConverter(value="regionconverter")
public class RegionConverter
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
	            Integer regionId = Integer.parseInt( submittedValue );
	            RegionBean regionBean = (RegionBean)FacesUtils.getManagedBean("regionBean");
	            List<Region> regions = regionBean.getRegionList();
	            for(Region region: regions){
	            	if(region.getId().intValue()==regionId.intValue())
	            		return region;
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
            return ((Region) value).getId().toString();  
        }  
    }
}
