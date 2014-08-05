package com.onetouch.view.converter;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;


/**
 * User defined converter for phone numbers.  
 * @author duttam
 *
 */
@FacesConverter(value="phoneconverter")
public class PhoneConverter implements Converter{
    
       
    public Object getAsObject(FacesContext context, UIComponent component,
            String value){
        if(StringUtils.isEmpty(value)){
            return null;
        }
        else{
        	String temp1 = org.springframework.util.StringUtils.trimAllWhitespace(value);
        	String temp2 = StringUtils.remove(temp1,"(");
        	String phoneNumber = StringUtils.replace(temp2, ")" , "-");
        	
            return phoneNumber;
            
        }
        
            
    }
    
    public String getAsString(FacesContext context, UIComponent component,
            Object value){
    	
    	if (value == null || value.equals("")) {
            return "";
        } else if (value instanceof String){
            String temp1 = (String)value;
            int len = temp1.trim().length();
            StringBuilder sBuild = new StringBuilder();
            if (len > 7) {
                sBuild.append(temp1.substring(0, 3));
                sBuild.append("-");
                sBuild.append(temp1.substring(3, 6));
                sBuild.append("-");
                sBuild.append(temp1.substring(6));
                return sBuild.toString();
            } else  {
        	return temp1;
            }
        }
        return "";
    }

   
   
    
}
