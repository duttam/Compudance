package com.onetouch.view.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.comparators.BooleanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.view.comparator.EmployeeAvailComparator;
import com.onetouch.view.comparator.EmployeeHireDateComparator;
import com.onetouch.view.comparator.EmployeeNameComparator;
import com.onetouch.view.comparator.EmployeeRatingComparator;


public class EmployeeUtil {

	public static List<Employee> sortEmployeeByDefaultOrder(List<Employee> employees ){
		List<Employee> sortEmployees = employees;
		
		EmployeeRatingComparator ratingComparator = new EmployeeRatingComparator();
		EmployeeHireDateComparator hireDateComparator = new EmployeeHireDateComparator();
		EmployeeNameComparator nameComparator = new EmployeeNameComparator();
		
		ComparatorChain comparatorChain = new ComparatorChain();
		
		comparatorChain.addComparator(ratingComparator);
		comparatorChain.addComparator(hireDateComparator);
		comparatorChain.addComparator(nameComparator);
		
		Collections.sort(sortEmployees,comparatorChain);
		
		return sortEmployees;
	}
	
	public static List<Employee> sortEmployeeByName(List<Employee> employees ){
		List<Employee> sortEmployees = employees;
		Collections.sort(sortEmployees, new EmployeeNameComparator());
		return sortEmployees;
	}
	public static List<Employee> sortEmployeeByRating(List<Employee> employees ){
		List<Employee> sortEmployees = employees;
		
		EmployeeRatingComparator ratingComparator = new EmployeeRatingComparator();
		EmployeeNameComparator nameComparator = new EmployeeNameComparator();
		
		ComparatorChain comparatorChain = new ComparatorChain();
		
		comparatorChain.addComparator(ratingComparator);
		comparatorChain.addComparator(nameComparator);
		
		Collections.sort(sortEmployees,comparatorChain);
		
		return sortEmployees;
	}
	public static List<Employee> sortEmployeeByHiredate(List<Employee> employees ){
		List<Employee> sortEmployees = employees;
		
		EmployeeHireDateComparator hireDateComparator = new EmployeeHireDateComparator();
		EmployeeNameComparator nameComparator = new EmployeeNameComparator();
		
		ComparatorChain comparatorChain = new ComparatorChain();
		
		comparatorChain.addComparator(hireDateComparator);
		comparatorChain.addComparator(nameComparator);
		
		Collections.sort(sortEmployees,comparatorChain);
		
		return sortEmployees;
	}
	public static List<Employee> sortEmployeeBySchedule(List<Employee> employees ){
		List<Employee> sortEmployees = employees;
		EmployeeRatingComparator ratingComparator = new EmployeeRatingComparator();
		EmployeeNameComparator nameComparator = new EmployeeNameComparator();
		EmployeeAvailComparator availComparator = new EmployeeAvailComparator(true);
		ComparatorChain comparatorChain = new ComparatorChain();
		comparatorChain.addComparator(availComparator);
		comparatorChain.addComparator(ratingComparator);
		comparatorChain.addComparator(nameComparator);
		Collections.sort(sortEmployees,comparatorChain);
		return sortEmployees;
	}
	public static Date getEmployeeRateEndDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date endDate = new Date();
		try {
			endDate = sdf.parse("12/31/3013");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return endDate;
	}
}
