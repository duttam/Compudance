package com.onetouch.view.comparator;

import java.util.Comparator;

import com.onetouch.model.domainobject.Employee;

public class EmployeeAvailComparator implements Comparator<Employee>{

	private final boolean trueLow;

	  public EmployeeAvailComparator(boolean trueLow) {
	    this.trueLow = trueLow;
	  }
	@Override
	public int compare(Employee e1, Employee e2) {
		boolean v1 = e1.isAvailable();
	    boolean v2 = e2.isAvailable();
	    return (v1 ^ v2) ? ((v1 ^ this.trueLow) ? 1 : -1) : 0;
    	 	
    	
	}

}
