package com.onetouch.view.comparator;

import java.util.Comparator;

import com.onetouch.model.domainobject.Employee;

public class EmployeeNameComparator implements Comparator<Employee>{

	@Override
	public int compare(Employee e1, Employee e2) {
		int employee = 0;
    	try{
        	employee = e1.getLastname().compareToIgnoreCase(e2.getLastname());
        	
    	    return ((employee == 0) ? e1.getFirstname().compareToIgnoreCase(e2.getFirstname()) : employee);
        	}catch (Exception e) {
    			
    		}
        	return 0;
	}

}
