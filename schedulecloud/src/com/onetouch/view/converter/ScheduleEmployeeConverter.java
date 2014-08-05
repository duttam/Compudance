package com.onetouch.view.converter;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Position;
import com.onetouch.view.bean.admin.employee.EmployeeHome;
import com.onetouch.view.bean.admin.event.AddEventBean;

import com.onetouch.view.bean.admin.position.PositionSupport;
import com.onetouch.view.util.FacesUtils;

@FacesConverter(value="scheduleEmployeeConverter")
public class ScheduleEmployeeConverter implements Converter {  
  
	public Object getAsObject( FacesContext facesContext, UIComponent component, String submittedValue )
    {
        if ( submittedValue.trim().equals( "" ) )
        {
            return null;
        } 
        else 
        {
        	try{
	            Integer positionId = Integer.parseInt( submittedValue );
	            AddEventBean addEventBean = (AddEventBean)FacesUtils.getManagedBean("addEventBean");
	            List<Position> positions = addEventBean.getSelectedPositions();
	            for(Position p: positions){
	            	if(p.getId().intValue()==positionId.intValue())
	            		return p;
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
            return String.valueOf( ((Position) value).getId());  
        }  
    }
}  
