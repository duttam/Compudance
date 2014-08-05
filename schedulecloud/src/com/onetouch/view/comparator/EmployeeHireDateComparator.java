package com.onetouch.view.comparator;

import java.util.Comparator;
import java.util.Date;

import org.joda.time.DateTime;

import com.onetouch.model.domainobject.Employee;

public class EmployeeHireDateComparator implements Comparator<Employee>{

	@Override
	public int compare(Employee e1, Employee e2) {
		if(e1.getHireDate()!=null && e2.getHireDate()!=null)
			return e1.getHireDate().compareTo(e2.getHireDate());
		else
			return 0;
	}

}
