package com.onetouch.view.bean.admin.employee;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.onetouch.view.bean.BaseBean;

@ManagedBean(name="employeeSupport")
@SessionScoped

public class EmployeeSupport extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> employeeStatus;
	private List<String> employeeRating;
	
	public EmployeeSupport(){
		populateEmployeeStatus();
		populateEmployeeRating();
		
	}

	
	
	public List<String> getEmployeeStatus() {
		return employeeStatus;
	}



	public List<String> getEmployeeRating() {
		return employeeRating;
	}



	private void populateEmployeeRating() {
		employeeRating = new ArrayList<String>();
	}

	private void populateEmployeeStatus() {
		employeeStatus = new ArrayList<String>();
		
	}
	
}
