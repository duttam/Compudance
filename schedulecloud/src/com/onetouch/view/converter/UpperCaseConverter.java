package com.onetouch.view.converter;



import javax.faces.component.UIComponent;  
import javax.faces.context.FacesContext;  
import javax.faces.convert.Converter;  
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

 
@FacesConverter(value="convertToUpper")
public class UpperCaseConverter implements Converter {  
  
	 public Object getAsObject(FacesContext facesContext,UIComponent component, String value) {  
		 if(StringUtils.isEmpty(value)){
	            return null;
	        }
	        else{
	        	String name = StringUtils.capitalize(value); 
	        	return name;
	        }
		   
	 }  
  
	 public String getAsString(FacesContext facesContext,UIComponent component, Object value) {  
		 if (value == null || value.equals("")) {
	            return "";
	        } else {
	        	return String.valueOf(value);
	        }
	 }  
  
}