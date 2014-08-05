package com.onetouch.view.converter;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.view.bean.admin.employee.EmployeeHome;
import com.onetouch.view.util.FacesUtils;

@FacesConverter(value="employeeConverter")
public class EmployeeConverter implements Converter {  
  
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {  
        if (submittedValue.trim().equals("")) {  
            return null;  
        } else {  
            try {  
                int id = Integer.parseInt(submittedValue);  
                EmployeeHome employeeHome = (EmployeeHome)FacesUtils.getManagedBean("employeeHome");
                List<Employee> employees = employeeHome.getEmployeeList();
                for (Employee employee : employees) {  
                    if (employee.getId().intValue() == id) {  
                        return employee;  
                    }  
                }  
                  
            } catch(NumberFormatException exception) {  
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"));  
            }  
        }  
  
        return null;  
    }  
  
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
        if (value == null || value.equals("")) {  
            return "";  
        } else {  
            return String.valueOf(((Employee) value).getId());  
        }  
    }  
}  
