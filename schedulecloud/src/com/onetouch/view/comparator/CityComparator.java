package com.onetouch.view.comparator;



import java.util.Comparator;

import javax.faces.model.SelectItem;

import com.onetouch.model.domainobject.City;


public class CityComparator implements Comparator<City>{

	
	
	// compare method for city entries.
    public int compare(City c1, City c2) {
    	int cityName = 0;
    	try{
    	cityName = c1.getCity().compareToIgnoreCase(c2.getCity());
    	
	    return ((cityName == 0) ? c1.getState().compareToIgnoreCase(c2.getState()) : cityName);
    	}catch (Exception e) {
			
		}
    	return 0;
    }

}
