package com.onetouch.view.comparator;



import java.util.Comparator;
import java.util.Date;

import javax.faces.model.SelectItem;

import com.onetouch.model.domainobject.City;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.view.util.DateUtil;


public class PositionDateTimeComparator implements Comparator<EventPosition>{

	
	@Override
	public int compare(EventPosition ep1, EventPosition ep2) {
		// TODO Auto-generated method stub
		int start = 0;
		try{
			DateUtil.compareDateAndTime(ep1.getStartTime(),ep2.getStartTime());
			return ((start==0) ? DateUtil.compareDateAndTime(ep1.getStartTime(),ep2.getStartTime()) : start);
		}catch(Exception e){
			
		}
		return 0;
	}

}
