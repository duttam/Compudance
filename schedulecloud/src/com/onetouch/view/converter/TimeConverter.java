package com.onetouch.view.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.FacesConverter;

import com.onetouch.model.domainobject.Employee;

import com.onetouch.view.bean.admin.employee.EmployeeHome;
import com.onetouch.view.bean.admin.schedule.ScheduleHome;

import com.onetouch.view.util.FacesUtils;

@FacesConverter(value="customTimeConverter")
public class TimeConverter implements Converter {  
	
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {  
        if (submittedValue.trim().equals("")) {  
            return null;  
        } else {  
        	//DateFormat timingFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        	DateFormat timeformat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    	    Date l=null;
    		
    			try {
					l = timeformat.parse(submittedValue);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            return l;
        }  
  
       
    }  
  
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
    	if ( value == null || value.equals( "" )) 
        {  
            return "";  
        } 
    	else {  
    		SimpleDateFormat timeformat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    		String s = timeformat.format((Date)value);
			return "";
        }  
    }  
}  
