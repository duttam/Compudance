package com.onetouch.view.comparator;

import java.util.Comparator;

import com.onetouch.model.domainobject.Employee;

public class EmployeeRatingComparator implements Comparator<Employee>{

	@Override
	public int compare(Employee e1, Employee e2) {
		
    	return e2.getRating().intValue()-e1.getRating().intValue();
        	
    	
	}

}
